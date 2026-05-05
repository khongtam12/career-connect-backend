package iuh.fit.jobservice.client;

import iuh.fit.jobservice.dto.CompanyDTO;
import iuh.fit.jobservice.dto.CompanySubscriptionDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import java.util.List;

@FeignClient(name = "company-service")
public interface
CompanyServiceClient {
    @GetMapping("/api/v1/company/{companyId}")
    CompanyDTO getCompanyById(@PathVariable("companyId") String companyId);

    @GetMapping("/api/v1/company/subscription/{subscriptionId}")
    CompanySubscriptionDTO getSubscriptionById(@PathVariable("subscriptionId") String subscriptionId);

    @PostMapping("/api/v1/company/subscription/{subscriptionId}/consume")
    CompanySubscriptionDTO consumeSubscription(@PathVariable("subscriptionId") String subscriptionId);

    @PostMapping("/api/v1/company/subscription/expire")
    List<String> expireSubscriptions();
}
