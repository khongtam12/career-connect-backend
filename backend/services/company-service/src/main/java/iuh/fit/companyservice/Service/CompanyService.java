package iuh.fit.companyservice.Service;

import iuh.fit.companyservice.client.EmployerClient;
import iuh.fit.companyservice.dto.request.EmployerCompanyRequest;
import iuh.fit.companyservice.dto.response.EmployerResponse;
import iuh.fit.companyservice.model.Company;
import iuh.fit.companyservice.repository.CompanyRepository;
import iuh.fit.companyservice.util.IdGenerator;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import iuh.fit.companyservice.dto.request.CompanyApprovalRequestDTO;
import iuh.fit.companyservice.dto.response.CompanyApprovalResponseDTO;
import iuh.fit.companyservice.dto.response.CompanyFullDetailDTO;
import iuh.fit.companyservice.dto.response.CompanyPendingDTO;
import iuh.fit.companyservice.model.ApprovalStatus;
import iuh.fit.companyservice.model.CompanyApprovalLog;
import iuh.fit.companyservice.repository.CompanyApprovalLogRepository;
import iuh.fit.companyservice.repository.CompanyVerificationRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CompanyService {
    private final CompanyRepository companyRepository;
    private final S3Service s3Service;
    private final EmployerClient employerClient;
    private final CompanyApprovalLogRepository companyApprovalLogRepository;
    private final CompanyVerificationRepository companyVerificationRepository;

    public CompanyService(CompanyRepository companyRepository, S3Service s3Service, EmployerClient employerClient, CompanyApprovalLogRepository companyApprovalLogRepository, CompanyVerificationRepository companyVerificationRepository) {
        this.companyRepository = companyRepository;
        this.s3Service = s3Service;
        this.employerClient = employerClient;
        this.companyApprovalLogRepository = companyApprovalLogRepository;
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
        Company company = companyRepository.findById(id).orElseThrow(() -> new RuntimeException("Company not found"));

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

        // Fetch Verification
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

    public Page<CompanyPendingDTO> getPendingCompanies(int page, int size) {
        Page<Company> companies = companyRepository.findByApprovalStatus(ApprovalStatus.PENDING, PageRequest.of(page, size));
        return companies.map(company -> {
            CompanyPendingDTO dto = new CompanyPendingDTO();
            dto.setId(company.getCompanyId());
            dto.setName(company.getName());
            dto.setCreatedAt(company.getCreatedAt());
            dto.setRequestedBy("N/A");
            return dto;
        });
    }

    public CompanyApprovalResponseDTO processApproval(CompanyApprovalRequestDTO request, String adminId) {
        Company company = companyRepository.findById(request.getCompanyId())
                .orElseThrow(() -> new RuntimeException("Company not found"));

        company.setApprovalStatus(request.getAction());
        company.setApprovedBy(adminId);
        companyRepository.save(company);

        CompanyApprovalLog log = new CompanyApprovalLog();
        log.setCompanyId(company.getCompanyId());
        log.setAction(request.getAction());
        log.setPerformedBy(adminId);
        log.setTimestamp(LocalDateTime.now());
        log.setNote(request.getNote());
        companyApprovalLogRepository.save(log);

        CompanyApprovalResponseDTO response = new CompanyApprovalResponseDTO();
        response.setCompanyId(company.getCompanyId());
        response.setApprovalStatus(company.getApprovalStatus());
        response.setApprovedBy(company.getApprovedBy());
        response.setApprovedAt(log.getTimestamp());
        response.setNote(log.getNote());

        return response;
    }
}
