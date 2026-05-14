package iuh.fit.userservice.service;

import iuh.fit.userservice.client.CompanyClient;
import iuh.fit.userservice.dto.request.EmployerRequestDTO;
import iuh.fit.userservice.dto.request.EmployerProfileUpdateRequest;
import iuh.fit.userservice.dto.response.CompanyDTO;
import iuh.fit.userservice.dto.response.EmployerPageDTO;
import iuh.fit.userservice.dto.response.EmployerProfileResponse;
import iuh.fit.userservice.dto.response.EmployerResponseDTO;
import iuh.fit.userservice.mapper.EmployerMapper;
import iuh.fit.userservice.model.Employer;
import iuh.fit.userservice.model.Status;
import iuh.fit.userservice.repository.EmployerRepository;
import iuh.fit.userservice.util.IdGenerator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Service
public class EmployerService {

    private final EmployerRepository employerRepository;
    private final EmployerMapper employerMapper;
    private final CompanyClient companyClient;

    public EmployerService(EmployerRepository employerRepository, EmployerMapper employerMapper, CompanyClient companyClient) {
        this.employerRepository = employerRepository;
        this.employerMapper = employerMapper;
        this.companyClient = companyClient;
    }

    public Employer save(Employer employer) {
        return employerRepository.save(employer);
    }

    public Employer findById(String id) {
        return employerRepository.findById(id).orElse(null);
    }

    private String getCompanyNameSafe(String companyId) {
        if (companyId == null || companyId.isEmpty()) return "N/A";
        try {
            CompanyDTO companyDTO = companyClient.getCompanyById(companyId);
            return companyDTO != null && companyDTO.getName() != null ? companyDTO.getName() : "Unknown Company";
        } catch (Exception e) {
            return "N/A";
        }
    }

    /**
     * Lấy tên công ty song song (parallel) cho tất cả companyId không trùng lặp.
     * Timeout 3 giây cho mỗi cuộc gọi, nếu thất bại trả về "N/A".
     */
    private Map<String, String> fetchCompanyNamesAsync(Set<String> companyIds) {
        Map<String, String> result = new ConcurrentHashMap<>();

        if (companyIds == null || companyIds.isEmpty()) {
            return result;
        }

        // Tạo các CompletableFuture chạy song song
        Map<String, CompletableFuture<String>> futures = new HashMap<>();
        for (String cid : companyIds) {
            futures.put(cid, CompletableFuture.supplyAsync(() -> getCompanyNameSafe(cid)));
        }

        // Chờ tất cả hoàn thành với timeout 3 giây
        for (Map.Entry<String, CompletableFuture<String>> entry : futures.entrySet()) {
            try {
                result.put(entry.getKey(), entry.getValue().get(3, TimeUnit.SECONDS));
            } catch (Exception e) {
                result.put(entry.getKey(), "N/A");
            }
        }

        return result;
    }

    public EmployerPageDTO getAllEmployers(String keyword, Status status, int page, int size) {
        Page<Employer> employerPage = employerRepository.findByKeywordAndStatus(keyword, status, PageRequest.of(page, size));

        List<Employer> employers = employerPage.getContent();

        // Thu thập tất cả companyId duy nhất (loại bỏ null)
        Set<String> uniqueCompanyIds = employers.stream()
                .map(Employer::getCompanyId)
                .filter(id -> id != null && !id.isEmpty())
                .collect(Collectors.toSet());

        // Gọi song song lấy tên công ty - chỉ gọi 1 lần cho mỗi companyId
        Map<String, String> companyNameMap = fetchCompanyNamesAsync(uniqueCompanyIds);

        // Map kết quả
        List<EmployerResponseDTO> content = employers.stream()
                .map(e -> employerMapper.toDto(
                        e,
                        Optional.ofNullable(e.getCompanyId())
                                .map(id -> companyNameMap.getOrDefault(id, "N/A"))
                                .orElse("N/A")
                ))
                .collect(Collectors.toList());

        return new EmployerPageDTO(
                content,
                employerPage.getNumber(),
                employerPage.getSize(),
                employerPage.getTotalElements(),
                employerPage.getTotalPages()
        );
    }

    @Transactional
    public EmployerResponseDTO createEmployer(EmployerRequestDTO dto) {
        if (employerRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Email đã tồn tại: " + dto.getEmail());
        }

        Employer employer = employerMapper.toEntity(dto);
        employer.setEmployerId(IdGenerator.generatorIdEmployer());
        employer.setCreatedAt(LocalDate.now());
        employer.setUpdatedAt(LocalDate.now());

        Employer saved = employerRepository.save(employer);
        return employerMapper.toDto(saved, getCompanyNameSafe(saved.getCompanyId()));
    }

    @Transactional
    public EmployerResponseDTO updateEmployer(String id, EmployerRequestDTO dto) {
        Employer employer = employerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy Employer với ID: " + id));

        if (!employer.getEmail().equals(dto.getEmail()) && employerRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Email đã tồn tại: " + dto.getEmail());
        }

        employer.setFullName(dto.getFullName());
        employer.setEmail(dto.getEmail());
        employer.setCompanyId(dto.getCompanyId());
        if (dto.getStatus() != null) {
            employer.setStatus(dto.getStatus());
        }
        employer.setUpdatedAt(LocalDate.now());

        Employer updated = employerRepository.save(employer);
        return employerMapper.toDto(updated, getCompanyNameSafe(updated.getCompanyId()));
    }

    @Transactional
    public void deleteEmployer(String id) {
        Employer employer = employerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy Employer với ID: " + id));
        employer.setStatus(Status.BANNED);
        employer.setUpdatedAt(LocalDate.now());
        employerRepository.save(employer);
    }

    @Transactional
    public EmployerResponseDTO changeStatus(String id, Status status) {
        Employer employer = employerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy Employer với ID: " + id));
        employer.setStatus(status);
        employer.setUpdatedAt(LocalDate.now());
        Employer updated = employerRepository.save(employer);
        return employerMapper.toDto(updated, getCompanyNameSafe(updated.getCompanyId()));
    }

    public Map<String, Object> getEmployerStats() {
        long total = employerRepository.count();
        long active = employerRepository.countByStatus(Status.ACTIVE);
        long banned = employerRepository.countByStatus(Status.BANNED);
        long newToday = employerRepository.countByCreatedAt(LocalDate.now());

        Map<String, Object> stats = new HashMap<>();
        stats.put("total", total);
        stats.put("active", active);
        stats.put("banned", banned);
        stats.put("newToday", newToday);
        return stats;
    }

    // ---- Self-service profile management ----

    public EmployerProfileResponse getProfile(String id) {
        Employer e = employerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Employer not found: " + id));
        String companyName = getCompanyNameSafe(e.getCompanyId());
        return toProfileResponse(e, companyName);
    }

    @Transactional
    public EmployerProfileResponse updateProfile(String id, EmployerProfileUpdateRequest dto) {
        Employer e = employerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Employer not found: " + id));
        e.setFullName(dto.getFullName());
        if (dto.getPhone() != null) e.setPhone(dto.getPhone());
        if (dto.getAvatar() != null) e.setAvatar(dto.getAvatar());
        if (dto.getPosition() != null) e.setPosition(dto.getPosition());
        e.setUpdatedAt(LocalDate.now());
        Employer saved = employerRepository.save(e);
        return toProfileResponse(saved, getCompanyNameSafe(saved.getCompanyId()));
    }

    private EmployerProfileResponse toProfileResponse(Employer e, String companyName) {
        return new EmployerProfileResponse(
                e.getEmployerId(),
                e.getEmail(),
                e.getFullName(),
                e.getPhone(),
                e.getAvatar(),
                e.getPosition(),
                e.getCompanyId(),
                companyName,
                e.getStatus() != null ? e.getStatus().name() : null,
                e.getCreatedAt(),
                e.getUpdatedAt()
        );
    }
}
