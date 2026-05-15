package iuh.fit.notificationservice.event;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentSuccessEvent {

    private String paymentId;
    private String companyId;
    private String packageId;
    private String packageName;
    private double amount;
    private int durationDays;
    private String employerEmail;
    private LocalDateTime paidAt;
    private int jobPostLimit;
    private String packageLabel;
    private String packageCategory;
    private String packageType;
    private int quantity;

}
