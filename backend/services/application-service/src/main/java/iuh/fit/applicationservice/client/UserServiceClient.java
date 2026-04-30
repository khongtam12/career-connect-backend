package iuh.fit.applicationservice.client;

import iuh.fit.applicationservice.dto.response.CandidateSummaryClientResponse;
import iuh.fit.applicationservice.dto.response.EmployerCompanyClientResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service", url = "http://localhost:8083")
public interface UserServiceClient {

    @GetMapping("/api/v1/user/employer/{employerId}")
    EmployerCompanyClientResponse getEmployerById(@PathVariable("employerId") String employerId);

    @GetMapping("/api/v1/user/candidate/{candidateId}")
    CandidateSummaryClientResponse getCandidateById(@PathVariable("candidateId") String candidateId);
}