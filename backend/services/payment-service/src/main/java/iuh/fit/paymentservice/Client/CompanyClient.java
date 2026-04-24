package iuh.fit.paymentservice.Client;

import iuh.fit.paymentservice.dto.request.CompanySubscriptionRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "company-service",url = "http://localhost:8081")
public interface CompanyClient {
    @PostMapping("/api/v1/company/subscription")
void saveCompanySubscription(@RequestBody CompanySubscriptionRequest companySubscription);
}
