package iuh.fit.jobservice.client;

import iuh.fit.jobservice.dto.response.EmployerCompanyResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service")
public interface UserServiceClient {
    @GetMapping("/api/v1/user/employer/{employerId}")
    EmployerCompanyResponse getEmployerById(@PathVariable("employerId") String employerId);
}