package iuh.fit.companyservice.consumer;

import iuh.fit.companyservice.Service.SubscriptionService;
import iuh.fit.companyservice.event.PaymentSuccessEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import iuh.fit.companyservice.dto.request.CompanySubscriptionRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubscriptionConsumer {

    private final SubscriptionService subscriptionService;

    @KafkaListener(topics = "payment-success", groupId = "company-group")
    public void handlePaymentSuccess(PaymentSuccessEvent event) {
        log.info("🏢 Nhận sự kiện thanh toán thành công để cập nhật gói dịch vụ: {}", event.getCompanyId());

        CompanySubscriptionRequest subRequest = CompanySubscriptionRequest.builder()
                .companyId(event.getCompanyId())
                .packageId(event.getPackageId())
                .packageLabel(event.getPackageLabel())
                .durationDays(event.getDurationDays())
                // Giả định jobPostLimit được truyền từ event hoặc lấy từ DB package
                .jobPostLimit(event.getJobPostLimit())
                .build();

        subscriptionService.save(subRequest);
    }
}