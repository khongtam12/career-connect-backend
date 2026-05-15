package iuh.fit.companyservice.controller;

import iuh.fit.companyservice.Service.CompanyVerificationService;
import iuh.fit.companyservice.dto.request.VerifyCompanyDTO;
import iuh.fit.companyservice.mapper.request.CompanyVerificationMapper;
import iuh.fit.companyservice.model.Company;
import iuh.fit.companyservice.model.CompanyVerification;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/company/verification")
public class VerificationController {
    private final CompanyVerificationService companyVerificationService;

    public VerificationController(CompanyVerificationService companyVerificationService) {
        this.companyVerificationService = companyVerificationService;
    }
    @PostMapping
    public ResponseEntity<?> save(@Valid @RequestBody VerifyCompanyDTO dto) {
        CompanyVerification companyVerification = CompanyVerificationMapper.toConvertCompanyVerification(dto);
      CompanyVerification com=  companyVerificationService.save(companyVerification);
        return ResponseEntity.status(HttpStatus.CREATED).body(com);

    }


}
