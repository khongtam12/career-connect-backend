package iuh.fit.companyservice.controller;

import iuh.fit.companyservice.Service.SubscriptionService;
import iuh.fit.companyservice.dto.request.CompanySubscriptionRequest;
import iuh.fit.companyservice.dto.response.CompanySubscriptionResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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

    @GetMapping("/{subscriptionId}")
    public ResponseEntity<CompanySubscriptionResponse> getSubscription(@PathVariable("subscriptionId") String subscriptionId) {
        return ResponseEntity.ok(subscriptionService.getById(subscriptionId));
    }

    @GetMapping("/company/{companyId}")
    public ResponseEntity<List<CompanySubscriptionResponse>> getByCompany(@PathVariable("companyId") String companyId) {
        return ResponseEntity.ok(subscriptionService.getByCompanyId(companyId));
    }

    @PostMapping("/{subscriptionId}/consume")
    public ResponseEntity<CompanySubscriptionResponse> consumeSubscription(@PathVariable("subscriptionId") String subscriptionId) {
        return ResponseEntity.ok(subscriptionService.consumeJobPost(subscriptionId));
    }

    @PostMapping("/expire")
    public ResponseEntity<List<String>> expireSubscriptions() {
        return ResponseEntity.ok(subscriptionService.expireSubscriptions());
    }

}
