package iuh.fit.jobservice.client;

import iuh.fit.jobservice.dto.CompanyDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "company-service")
public interface
CompanyServiceClient {
    @GetMapping("/api/v1/company/{companyId}")
    CompanyDTO getCompanyById(@PathVariable("companyId") String companyId);
}
