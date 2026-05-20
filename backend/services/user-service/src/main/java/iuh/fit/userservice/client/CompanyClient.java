package iuh.fit.userservice.client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import iuh.fit.userservice.dto.response.CompanyDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.List;
import iuh.fit.userservice.dto.response.CompanyApprovalActivityDTO;

@FeignClient(name = "company-service", path = "/api/v1/company")
public interface CompanyClient {
    @GetMapping("/{companyId}")
    @CircuitBreaker(name = "companyService", fallbackMethod = "fallbackGetCompany")
    CompanyDTO getCompanyById(@PathVariable("companyId") String companyId);

    @GetMapping("/recent-approvals")
    @CircuitBreaker(name = "companyService", fallbackMethod = "fallbackGetRecentApprovals")
    List<CompanyApprovalActivityDTO> getRecentApprovals(@RequestParam("limit") int limit);

    default CompanyDTO fallbackGetCompany(String companyId, Throwable throwable) {
        CompanyDTO fallback = new CompanyDTO();
        fallback.setCompanyId(companyId);
        fallback.setName("N/A");
        fallback.setLogo(null);
        return fallback;
    }

    default List<CompanyApprovalActivityDTO> fallbackGetRecentApprovals(int limit, Throwable throwable) {
        return List.of();
    }
}
