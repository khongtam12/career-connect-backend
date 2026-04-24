package iuh.fit.companyservice.controller;

import iuh.fit.companyservice.Service.SubscriptionService;
import iuh.fit.companyservice.dto.request.CompanySubscriptionRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/company/subscription")
public class SubscriptionController {
    private final SubscriptionService subscriptionService;

    public SubscriptionController(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    @PostMapping()
    void saveCompanySubscription(@RequestBody CompanySubscriptionRequest companySubscription){
        subscriptionService.save(companySubscription);

    }

}
