package iuh.fit.companyservice.client;

import iuh.fit.companyservice.dto.request.EmployerCompanyRequest;
import iuh.fit.companyservice.dto.response.EmployerResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "user-service")
public interface EmployerClient {
    @PostMapping("/api/v1/user/employer/save")
    EmployerResponse saveEmployerCompany(@RequestBody EmployerCompanyRequest request);
}
