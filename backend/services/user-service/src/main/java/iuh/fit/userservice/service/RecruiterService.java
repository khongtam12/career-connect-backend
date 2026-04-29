package iuh.fit.userservice.service;

import iuh.fit.userservice.dto.request.RecruiterRequestDTO;
import iuh.fit.userservice.dto.response.RecruiterPageDTO;
import iuh.fit.userservice.dto.response.RecruiterResponseDTO;
import iuh.fit.userservice.mapper.RecruiterMapper;
import iuh.fit.userservice.model.Recruiter;
import iuh.fit.userservice.model.RecruiterStatus;
import iuh.fit.userservice.model.Role;
import iuh.fit.userservice.repository.RecruiterRepository;
import iuh.fit.userservice.util.IdGenerator;
import iuh.fit.userservice.client.CompanyClient;
import iuh.fit.userservice.dto.response.CompanyDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RecruiterService {

    private final RecruiterRepository recruiterRepository;
    private final RecruiterMapper recruiterMapper;
    private final CompanyClient companyClient;

    public RecruiterService(RecruiterRepository recruiterRepository, RecruiterMapper recruiterMapper, CompanyClient companyClient) {
        this.recruiterRepository = recruiterRepository;
        this.recruiterMapper = recruiterMapper;
        this.companyClient = companyClient;
    }

    private String getCompanyNameSafe(String companyId) {
        try {
            CompanyDTO companyDTO = companyClient.getCompanyById(companyId);
            return companyDTO != null && companyDTO.getName() != null ? companyDTO.getName() : "Unknown Company";
        } catch (Exception e) {
            return "Company " + companyId;
        }
    }

    public RecruiterPageDTO getAllRecruiters(int page, int size) {
        Page<Recruiter> recruiterPage = recruiterRepository.findAllByRole(Role.RECRUITER, PageRequest.of(page, size));
        
        List<RecruiterResponseDTO> content = recruiterPage.getContent().stream()
                .map(r -> recruiterMapper.toDto(r, getCompanyNameSafe(r.getCompanyId())))
                .collect(Collectors.toList());

        return new RecruiterPageDTO(
                content,
                recruiterPage.getNumber(),
                recruiterPage.getSize(),
                recruiterPage.getTotalElements()
        );
    }

    @Transactional
    public RecruiterResponseDTO createRecruiter(RecruiterRequestDTO dto) {
        if (recruiterRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Email đã tồn tại: " + dto.getEmail());
        }

        Recruiter recruiter = recruiterMapper.toEntity(dto);
        recruiter.setId(IdGenerator.generatorIdRecruiter());
        recruiter.setCreatedAt(LocalDateTime.now());
        recruiter.setUpdatedAt(LocalDateTime.now());

        Recruiter saved = recruiterRepository.save(recruiter);
        return recruiterMapper.toDto(saved, getCompanyNameSafe(saved.getCompanyId()));
    }

    @Transactional
    public RecruiterResponseDTO updateRecruiter(String id, RecruiterRequestDTO dto) {
        Recruiter recruiter = recruiterRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy Recruiter với ID: " + id));

        // Nếu email thay đổi thì cần check trùng
        if (!recruiter.getEmail().equals(dto.getEmail()) && recruiterRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Email đã tồn tại: " + dto.getEmail());
        }

        recruiter.setUsername(dto.getUsername());
        recruiter.setEmail(dto.getEmail());
        recruiter.setCompanyId(dto.getCompanyId());
        if (dto.getStatus() != null) {
            recruiter.setStatus(dto.getStatus());
        }
        recruiter.setUpdatedAt(LocalDateTime.now());

        Recruiter updated = recruiterRepository.save(recruiter);
        return recruiterMapper.toDto(updated, getCompanyNameSafe(updated.getCompanyId()));
    }

    @Transactional
    public void deleteRecruiter(String id) {
        Recruiter recruiter = recruiterRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy Recruiter với ID: " + id));
        recruiter.setStatus(RecruiterStatus.INACTIVE);
        recruiter.setUpdatedAt(LocalDateTime.now());
        recruiterRepository.save(recruiter);
    }

    @Transactional
    public RecruiterResponseDTO changeStatus(String id, RecruiterStatus status) {
        Recruiter recruiter = recruiterRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy Recruiter với ID: " + id));
        recruiter.setStatus(status);
        recruiter.setUpdatedAt(LocalDateTime.now());
        Recruiter updated = recruiterRepository.save(recruiter);
        return recruiterMapper.toDto(updated, getCompanyNameSafe(updated.getCompanyId()));
    }
}
