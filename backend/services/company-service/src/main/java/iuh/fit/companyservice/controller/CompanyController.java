package iuh.fit.companyservice.controller;

import iuh.fit.companyservice.model.Company;
import iuh.fit.companyservice.repository.CompanyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import iuh.fit.companyservice.Service.CompanyService;
import iuh.fit.companyservice.dto.request.CompanyDTO;
import iuh.fit.companyservice.mapper.request.CompanyMapper;
import iuh.fit.companyservice.model.Company;
import iuh.fit.companyservice.util.IdGenerator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/company")
@RequiredArgsConstructor
public class CompanyController {
    private final CompanyRepository companyRepository;
    private final CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

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
    @PostMapping("/save")
    public ResponseEntity<Company> saveCompany(@RequestBody CompanyDTO dto){


        Company company = CompanyMapper.toConvertCompany(dto);
        Company saved = companyService.saveCompany(company,dto.getEmployerId());

        return ResponseEntity.status(HttpStatus.CREATED).body(saved);


    }

}
