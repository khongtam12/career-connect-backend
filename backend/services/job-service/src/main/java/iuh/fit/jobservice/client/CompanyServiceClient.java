package iuh.fit.jobservice.client;

import iuh.fit.jobservice.dto.CompanyDTO;
import iuh.fit.jobservice.dto.CompanyMarketingAssignmentDTO;
import iuh.fit.jobservice.dto.CompanyMarketingAssignmentRequest;
import iuh.fit.jobservice.dto.CompanySubscriptionDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
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

    @PostMapping("/api/v1/company/marketing-entitlements/{entitlementId}/assign")
    CompanyMarketingAssignmentDTO assignMarketingEntitlement(
            @PathVariable("entitlementId") String entitlementId,
            @RequestBody CompanyMarketingAssignmentRequest request
    );

    @DeleteMapping("/api/v1/company/marketing-entitlements/assignments/{assignmentId}")
    CompanyMarketingAssignmentDTO removeMarketingAssignment(
            @PathVariable("assignmentId") String assignmentId,
            @RequestParam("companyId") String companyId
    );

    @GetMapping("/api/v1/company/marketing-entitlements/company/{companyId}/active-assignment")
    CompanyMarketingAssignmentDTO getActiveMarketingAssignment(
            @PathVariable("companyId") String companyId,
            @RequestParam("targetScope") String targetScope,
            @RequestParam("targetId") String targetId
    );
}
