package iuh.fit.userservice.controller;

import iuh.fit.userservice.dto.request.EmployerCompanyRequest;
import iuh.fit.userservice.dto.response.EmployerCompanyResponse;
import iuh.fit.userservice.model.Employer;
import iuh.fit.userservice.service.EmployerService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/user/employer")
public class EmployerController {
    private EmployerService employerService;
    public EmployerController(EmployerService employerService) {
        this.employerService = employerService;
    }
    @PostMapping("/save")
    public Employer saveCompanyForEmployer(@RequestBody EmployerCompanyRequest dto
                                          ) {
        Employer employer = employerService.findById(dto.getEmployerId());
        if (employer == null) {
            throw new RuntimeException("Employer not found: " + dto.getEmployerId());
        }
        employer.setCompanyId(dto.getCompanyId());
        return employerService.save(employer);
    }

    @GetMapping("/{employerId}")
    public EmployerCompanyResponse getEmployerById(@PathVariable String employerId) {
        Employer employer = employerService.findById(employerId);
        if (employer == null) {
            throw new RuntimeException("Employer not found: " + employerId);
        }

        return new EmployerCompanyResponse(
                employer.getEmployerId(),
                employer.getCompanyId()
        );
    }
}
