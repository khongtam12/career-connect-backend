package iuh.fit.companyservice.consumer;

import iuh.fit.companyservice.Service.MarketingEntitlementService;
import iuh.fit.companyservice.Service.SubscriptionService;
import iuh.fit.companyservice.dto.request.CompanyMarketingEntitlementRequest;
import iuh.fit.companyservice.dto.request.CompanySubscriptionRequest;
import iuh.fit.companyservice.event.PaymentSuccessEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubscriptionConsumer {

    private static final String JOB_POSTING = "JOB_POSTING";

    private final SubscriptionService subscriptionService;
    private final MarketingEntitlementService marketingEntitlementService;

    @KafkaListener(topics = "payment-success", groupId = "company-group")
    public void handlePaymentSuccess(PaymentSuccessEvent event) {
        log.info("Nhan su kien thanh toan thanh cong de cap nhat goi dich vu: {}", event.getCompanyId());

        if (!JOB_POSTING.equalsIgnoreCase(event.getPackageCategory())) {
            CompanyMarketingEntitlementRequest entitlementRequest = CompanyMarketingEntitlementRequest.builder()
                    .companyId(event.getCompanyId())
                    .paymentId(event.getPaymentId())
                    .packageId(event.getPackageId())
                    .packageLabel(event.getPackageLabel())
                    .packageCategory(event.getPackageCategory())
                    .packageType(event.getPackageType())
                    .jobLimit(event.getJobPostLimit())
                    .quantity(event.getQuantity())
                    .durationDays(event.getDurationDays())
                    .build();

            marketingEntitlementService.save(entitlementRequest);
            return;
        }

        CompanySubscriptionRequest subRequest = CompanySubscriptionRequest.builder()
                .companyId(event.getCompanyId())
                .packageId(event.getPackageId())
                .packageLabel(event.getPackageLabel())
                .packageCategory(event.getPackageCategory())
                .durationDays(event.getDurationDays())
                .jobPostLimit(event.getJobPostLimit())
                .quantity(event.getQuantity())
                .build();

        subscriptionService.save(subRequest);
    }
}
