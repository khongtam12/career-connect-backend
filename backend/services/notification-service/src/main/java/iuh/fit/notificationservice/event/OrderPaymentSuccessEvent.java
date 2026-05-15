package iuh.fit.notificationservice.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderPaymentSuccessEvent {
    private String paymentId;
    private String companyId;
    private String employerEmail;
    private Double totalAmount;
    private LocalDateTime paidAt;
    private List<ItemEvent> items;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ItemEvent {
        private String packageId;
        private String packageName;
        private Integer durationDays;
        private Integer quantity;
        private Double amount;
    }
}
