package iuh.fit.companyservice.controller;

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
public class CompanyController {
    private final CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @GetMapping
    public String Test()
{
return "Company service is working";
}

    @PostMapping("/save")
    public ResponseEntity<Company> saveCompany(@RequestBody CompanyDTO dto){


        Company company = CompanyMapper.toConvertCompany(dto);
        Company saved = companyService.saveCompany(company,dto.getEmployerId());

        return ResponseEntity.status(HttpStatus.CREATED).body(saved);


    }

}
