package iuh.fit.notificationservice.consumer;



import iuh.fit.notificationservice.event.PaymentSuccessEvent;
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

    @KafkaListener(topics = "payment-success", groupId = "notification-group")
    public void handlePaymentSuccess(PaymentSuccessEvent event) {
        log.info("📧 Nhận sự kiện thanh toán thành công để gửi mail: {}", event.getPaymentId());
        try {
            System.out.println(event.getEmployerEmail());
            emailService.sendPaymentSuccessEmail(
                    event.getEmployerEmail(),
                    event.getPackageName(),
                    event.getAmount(),
                    event.getDurationDays()
            );
        } catch (Exception e) {
            log.error("❌ Lỗi khi gửi email thanh toán: {}", e.getMessage());
        }
    }
}
