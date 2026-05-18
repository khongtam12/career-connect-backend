package iuh.fit.companyservice.controller;

import iuh.fit.companyservice.model.Company;
import iuh.fit.companyservice.repository.CompanyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import iuh.fit.companyservice.Service.CompanyService;
import iuh.fit.companyservice.dto.request.CompanyDTO;
import iuh.fit.companyservice.mapper.request.CompanyMapper;
import iuh.fit.companyservice.util.IdGenerator;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestHeader;

import java.time.LocalDateTime;
import iuh.fit.companyservice.dto.request.CompanyApprovalRequestDTO;
import iuh.fit.companyservice.dto.response.CompanyApprovalResponseDTO;
import iuh.fit.companyservice.dto.response.CompanyFullDetailDTO;
import iuh.fit.companyservice.dto.response.CompanyPendingDTO;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import iuh.fit.companyservice.Service.VietQrService;
import iuh.fit.companyservice.dto.response.VietQrResponseDTO;

@RestController
@RequestMapping("/api/v1/company")

public class CompanyController {
    private final CompanyRepository companyRepository;
    private final CompanyService companyService;
    private final VietQrService vietQrService;

    public CompanyController(CompanyRepository companyRepository, CompanyService companyService, VietQrService vietQrService) {
        this.companyRepository = companyRepository;
        this.companyService = companyService;
        this.vietQrService = vietQrService;
    }

    @GetMapping
    public String Test() {
        return "Company service is working";
    }

    @GetMapping("/{companyId}")
    public ResponseEntity<CompanyFullDetailDTO> getCompanyById(@PathVariable("companyId") String companyId) {
        return ResponseEntity.ok(companyService.getCompanyFullDetail(companyId));
    };

    @GetMapping("/verify-tax/{taxCode}")
    public ResponseEntity<VietQrResponseDTO> verifyTax(@PathVariable("taxCode") String taxCode) {
        return ResponseEntity.ok(vietQrService.verifyTaxCode(taxCode));
    }

    @PostMapping("/save")
    public ResponseEntity<Company> saveCompany(@Valid @RequestBody CompanyDTO dto) {
        Company company = CompanyMapper.toConvertCompany(dto);
        Company saved = companyService.saveCompany(company, dto.getEmployerId());
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @GetMapping("/pending-approvals")
    public ResponseEntity<Page<CompanyPendingDTO>> getPendingApprovals(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size) {
        Page<CompanyPendingDTO> result = companyService.getPendingCompanies(page, size);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/approval")
    public ResponseEntity<CompanyApprovalResponseDTO> processApproval(
            @RequestBody CompanyApprovalRequestDTO request,
            @RequestHeader(value = "X-User-Id", defaultValue = "admin-1") String adminId) {
        // Assume adminId is passed via header or extracted from auth context
        CompanyApprovalResponseDTO response = companyService.processApproval(request, adminId);
        return ResponseEntity.ok(response);
    }
}
