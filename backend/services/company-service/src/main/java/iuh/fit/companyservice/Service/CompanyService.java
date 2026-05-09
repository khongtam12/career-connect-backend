package iuh.fit.companyservice.Service;

import iuh.fit.companyservice.client.EmployerClient;
import iuh.fit.companyservice.dto.request.CompanyApprovalRequestDTO;
import iuh.fit.companyservice.dto.request.EmployerCompanyRequest;
import iuh.fit.companyservice.dto.response.CompanyApprovalResponseDTO;
import iuh.fit.companyservice.dto.response.CompanyFullDetailDTO;
import iuh.fit.companyservice.dto.response.CompanyPendingDTO;
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
import java.util.stream.Collectors;

@Service
public class CompanyService {
    private final CompanyRepository companyRepository;
    private final S3Service s3Service;
    private final EmployerClient employerClient;
    private final CompanyVerificationRepository companyVerificationRepository;

    public CompanyService(CompanyRepository companyRepository, S3Service s3Service,
            EmployerClient employerClient,
            CompanyVerificationRepository companyVerificationRepository) {
        this.companyRepository = companyRepository;
        this.s3Service = s3Service;
        this.employerClient = employerClient;
        this.companyVerificationRepository = companyVerificationRepository;
    }

    public Company saveCompany(Company company, String employerId) {
        if (company.getCompanyId() == null || company.getCompanyId().isBlank()) {
            company.setCompanyId(IdGenerator.generatorIdCompanny());
            company.setCreatedAt(LocalDateTime.now());
        }
        Company savedCompany = companyRepository.save(company);
        EmployerCompanyRequest request = new EmployerCompanyRequest();
        request.setCompanyId(company.getCompanyId());
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

        if (oldLogo != null && oldLogo.contains("/")) {
            String key = oldLogo.substring(oldLogo.lastIndexOf("/") + 1);
            s3Service.deleteFile(key);
        }
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
}
