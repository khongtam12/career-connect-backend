package iuh.fit.companyservice.Service;

import feign.FeignException;
import iuh.fit.companyservice.client.StorageClient;
import iuh.fit.companyservice.client.EmployerClient;
import iuh.fit.companyservice.dto.request.CompanyApprovalRequestDTO;
import iuh.fit.companyservice.dto.request.CompanyProfileUpdateRequest;
import iuh.fit.companyservice.dto.request.EmployerCompanyRequest;
import iuh.fit.companyservice.dto.response.CompanyApprovalActivityDTO;
import iuh.fit.companyservice.dto.response.CompanyApprovalResponseDTO;
import iuh.fit.companyservice.dto.response.CompanyFullDetailDTO;
import iuh.fit.companyservice.dto.response.CompanyPendingDTO;
import iuh.fit.companyservice.dto.response.CompanyProfileResponse;
import iuh.fit.companyservice.dto.response.EmployerResponse;
import iuh.fit.companyservice.model.Company;
import iuh.fit.companyservice.model.CompanyVerification;
import iuh.fit.companyservice.model.StatusCompany;
import iuh.fit.companyservice.model.StatusVerification;
import iuh.fit.companyservice.repository.CompanyRepository;
import iuh.fit.companyservice.repository.CompanyVerificationRepository;
import iuh.fit.companyservice.util.IdGenerator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CompanyService {
    private final CompanyRepository companyRepository;
    private final StorageClient storageClient;
    private final EmployerClient employerClient;
    private final CompanyVerificationRepository companyVerificationRepository;

    public CompanyService(CompanyRepository companyRepository, StorageClient storageClient,
            EmployerClient employerClient,
            CompanyVerificationRepository companyVerificationRepository) {
        this.companyRepository = companyRepository;
        this.storageClient = storageClient;
        this.employerClient = employerClient;
        this.companyVerificationRepository = companyVerificationRepository;
    }

    public Company saveCompany(Company company, String employerId) {
        String oldLogo = null;
        if (company.getCompanyId() == null || company.getCompanyId().isBlank()) {
            company.setCompanyId(IdGenerator.generatorIdCompanny());
            company.setCreatedAt(LocalDateTime.now());
            company.setStatusCompany(StatusCompany.PENDING);
        } else {
            Company existingCompany = companyRepository.findById(company.getCompanyId())
                    .orElseThrow(() -> new RuntimeException("Company not found"));
            oldLogo = existingCompany.getLogo();
            company.setCreatedAt(existingCompany.getCreatedAt());
            company.setStatusCompany(existingCompany.getStatusCompany());
            company.setSubscriptions(existingCompany.getSubscriptions());
            if (company.getLogo() == null || company.getLogo().isBlank()) {
                company.setLogo(existingCompany.getLogo());
            }
        }
        Company savedCompany = companyRepository.save(company);
        deleteReplacedLogo(oldLogo, savedCompany.getLogo());
        EmployerCompanyRequest request = new EmployerCompanyRequest();
        request.setCompanyId(savedCompany.getCompanyId());
        request.setEmployerId(employerId);
        employerClient.saveEmployerCompany(request);
        return savedCompany;
    }

    public CompanyFullDetailDTO getCompanyFullDetail(String id) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Company not found"));

        CompanyFullDetailDTO dto = new CompanyFullDetailDTO();
        dto.setId(company.getCompanyId());
        dto.setName(company.getName());
        dto.setLogo(company.getLogo());
        dto.setTaxCode(company.getTaxCode());
        dto.setWebsite(company.getWebsite());
        dto.setEmail(company.getEmail());
        dto.setPhone(company.getPhone());
        dto.setAddress(company.getAddress());
        dto.setDescription(company.getDescription());
        dto.setCompanySize(company.getCompanySize());
        dto.setFoundedYear(company.getFoundedYear());
        dto.setCreatedAt(company.getCreatedAt());
        dto.setStatusCompany(company.getStatusCompany() != null ? company.getStatusCompany().name() : null);

        // Lấy thông tin xác minh từ CompanyVerification
        companyVerificationRepository.findByCompanyCompanyId(id).ifPresent(v -> {
            dto.setSubmittedTaxCode(v.getSubmittedTaxCode());
            dto.setBusinessLicense(v.getBusinessLicense());
            dto.setVerificationNote(v.getNote());
            dto.setVerificationStatus(v.getStatus().name());
        });

        return dto;
    }

    public void updateCompanyLogo(String companyId, String newLogoUrl) {
        Company company = companyRepository.findById(companyId).orElseThrow();
        String oldLogo = company.getLogo();
        company.setLogo(newLogoUrl);
        companyRepository.save(company);

        deleteReplacedLogo(oldLogo, newLogoUrl);
    }

    /**
     * Lấy danh sách công ty có trạng thái xác minh PENDING
     * (dùng CompanyVerification.status thay vì trường approvalStatus cũ)
     */
    public Page<CompanyPendingDTO> getPendingCompanies(int page, int size) {
        Page<Company> companies = companyRepository.findByVerificationStatus(
                StatusVerification.PENDING, PageRequest.of(page, size));
        return companies.map(company -> {
            CompanyPendingDTO dto = new CompanyPendingDTO();
            dto.setId(company.getCompanyId());
            dto.setName(company.getName());
            dto.setLogo(company.getLogo());
            dto.setCreatedAt(company.getCreatedAt());
            dto.setRequestedBy("N/A");
            return dto;
        });
    }

    /**
     * Xử lý phê duyệt / từ chối công ty bằng cách cập nhật
     * CompanyVerification.status
     */
    public CompanyApprovalResponseDTO processApproval(CompanyApprovalRequestDTO request, String adminId) {
        Company company = companyRepository.findById(request.getCompanyId())
                .orElseThrow(() -> new RuntimeException("Company not found"));

        // Tìm bản ghi xác minh hiện có, nếu không có thì tạo mới
        CompanyVerification verification = companyVerificationRepository
                .findByCompanyCompanyId(company.getCompanyId())
                .orElseGet(() -> {
                    CompanyVerification v = new CompanyVerification();
                    v.setVerificationId(IdGenerator.generatorIdCompannyVerified());
                    v.setCompany(company);
                    v.setSubmittedAt(LocalDateTime.now());
                    return v;
                });

        verification.setStatus(request.getAction());
        verification.setVerifiedBy(adminId);
        verification.setVerifiedAt(LocalDateTime.now());
        verification.setNote(request.getNote());
        CompanyVerification saved = companyVerificationRepository.save(verification);

        // Cập nhật trạng thái của Company cho đồng nhất
        if (request.getAction() == StatusVerification.APPROVED) {
            company.setStatusCompany(StatusCompany.VERIFIED);
        } else {
            company.setStatusCompany(StatusCompany.PENDING);
        }
        companyRepository.save(company);

        CompanyApprovalResponseDTO response = new CompanyApprovalResponseDTO();
        response.setCompanyId(company.getCompanyId());
        response.setVerificationStatus(saved.getStatus());
        response.setVerifiedBy(saved.getVerifiedBy());
        response.setVerifiedAt(saved.getVerifiedAt());
        response.setNote(saved.getNote());

        return response;
    }

    public List<CompanyApprovalActivityDTO> getRecentApprovals(int limit) {
        int size = limit <= 0 ? 8 : Math.min(limit, 20);
        List<StatusVerification> statuses = List.of(StatusVerification.APPROVED, StatusVerification.REJECTED);
        Page<CompanyVerification> page = companyVerificationRepository
                .findByStatusInOrderByVerifiedAtDesc(statuses, PageRequest.of(0, size));
        return page.getContent().stream().map(verification -> {
            Company company = verification.getCompany();
            return CompanyApprovalActivityDTO.builder()
                    .companyId(company != null ? company.getCompanyId() : null)
                    .companyName(company != null ? company.getName() : null)
                    .status(verification.getStatus())
                    .verifiedAt(verification.getVerifiedAt())
                    .build();
        }).collect(Collectors.toList());
    }

    // ---- Company profile management ----

    public CompanyProfileResponse getCompanyProfile(String companyId) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Company not found: " + companyId));
        return toProfileResponse(company);
    }

    @org.springframework.transaction.annotation.Transactional
    public CompanyProfileResponse updateCompanyProfile(String companyId, CompanyProfileUpdateRequest dto) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Company not found: " + companyId));
        String oldLogo = company.getLogo();
        company.setName(dto.getName());
        if (dto.getLogo() != null) {
            company.setLogo(dto.getLogo());
        }
        if (dto.getWebsite() != null) company.setWebsite(dto.getWebsite());
        if (dto.getEmail() != null) company.setEmail(dto.getEmail());
        if (dto.getPhone() != null) company.setPhone(dto.getPhone());
        if (dto.getAddress() != null) company.setAddress(dto.getAddress());
        if (dto.getDescription() != null) company.setDescription(dto.getDescription());
        company.setCompanySize(dto.getCompanySize());
        company.setFoundedYear(dto.getFoundedYear());
        Company saved = companyRepository.save(company);

        if (dto.getLogo() != null) {
            deleteReplacedLogo(oldLogo, dto.getLogo());
        }

        return toProfileResponse(saved);
    }

    private CompanyProfileResponse toProfileResponse(Company c) {
        return new CompanyProfileResponse(
                c.getCompanyId(),
                c.getName(),
                c.getLogo(),
                c.getTaxCode(),
                c.getWebsite(),
                c.getEmail(),
                c.getPhone(),
                c.getAddress(),
                c.getDescription(),
                c.getCompanySize(),
                c.getFoundedYear(),
                c.getStatusCompany() != null ? c.getStatusCompany().name() : null,
                c.getCreatedAt()
        );
    }

    private String extractObjectKey(String fileUrl) {
        if (fileUrl == null || fileUrl.isBlank() || !fileUrl.contains("/")) {
            return null;
        }

        // Only treat URLs generated by our S3/storage flow as deletable objects.
        // External image URLs (google/bing/cdn/etc.) must be ignored.
        String normalized = fileUrl.trim();
        String s3Marker = ".s3.amazonaws.com/";
        int s3Index = normalized.indexOf(s3Marker);
        if (s3Index < 0) {
            return null;
        }

        int keyStart = s3Index + s3Marker.length();
        if (keyStart >= normalized.length()) {
            return null;
        }

        return normalized.substring(keyStart);
    }

    private void deleteReplacedLogo(String oldLogoUrl, String newLogoUrl) {
        String oldLogoKey = extractObjectKey(oldLogoUrl);
        String nextLogoKey = extractObjectKey(newLogoUrl);
        if (oldLogoKey != null && !oldLogoKey.equals(nextLogoKey)) {
            try {
                storageClient.deleteFile(oldLogoKey);
            } catch (FeignException ex) {
                System.err.println("Skip deleting old logo from storage-service: " + oldLogoKey + " - " + ex.getMessage());
            }
        }
    }
}
