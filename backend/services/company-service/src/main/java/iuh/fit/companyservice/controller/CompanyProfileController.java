package iuh.fit.companyservice.controller;

import iuh.fit.companyservice.dto.request.CompanyProfileUpdateRequest;
import iuh.fit.companyservice.dto.response.CompanyProfileResponse;
import iuh.fit.companyservice.Service.CompanyService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/company")
public class CompanyProfileController {

    private final CompanyService companyService;

    public CompanyProfileController(CompanyService companyService) {
        this.companyService = companyService;
    }

    /** Get company profile by ID */
    @GetMapping("/{companyId}/profile")
    public ResponseEntity<CompanyProfileResponse> getProfile(
            @PathVariable String companyId) {
        return ResponseEntity.ok(companyService.getCompanyProfile(companyId));
    }

    /** Employer updates their own company profile */
    @PutMapping("/{companyId}/profile")
    public ResponseEntity<CompanyProfileResponse> updateProfile(
            @PathVariable String companyId,
            @RequestBody CompanyProfileUpdateRequest dto) {
        return ResponseEntity.ok(companyService.updateCompanyProfile(companyId, dto));
    }
}
