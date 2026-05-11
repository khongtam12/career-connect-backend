package iuh.fit.paymentservice.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentSuccessEvent {
    private String paymentId;
    private String companyId;
    private String employerEmail;
    private String packageId;
    private String packageName;
    private Double amount;
    private Integer durationDays;
    private LocalDateTime paidAt;
    private int jobPostLimit;
    private String packageLabel;

}