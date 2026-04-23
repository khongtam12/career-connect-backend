package iuh.fit.companyservice.controller;

import iuh.fit.companyservice.model.Company;
import iuh.fit.companyservice.repository.CompanyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/company")
@RequiredArgsConstructor
public class CompanyController {
    private final CompanyRepository companyRepository;
    @GetMapping
    public String Test()
{
return "Company service is working";
}

    @GetMapping("/{companyId}")
    public ResponseEntity<Company> getCompanyById(@PathVariable String companyId) {
        return companyRepository.findById(companyId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

}
