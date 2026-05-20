package iuh.fit.notificationservice.consumer;

import iuh.fit.notificationservice.event.OrderPaymentSuccessEvent;
import iuh.fit.notificationservice.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentConsumer {

    private final EmailService emailService;

    @KafkaListener(topics = "payment-order-success")
    public void handlePaymentSuccess(OrderPaymentSuccessEvent event) {
        log.info("Nhận sự kiện thanh toán tổng hợp để gửi 1 email: {}", event.getPaymentId());
        try {
            emailService.sendPaymentSuccessEmail(event);
        } catch (Exception e) {
            log.error("Lỗi khi gửi email thanh toán tổng hợp: {}", e.getMessage(), e);
        }
    }
}
