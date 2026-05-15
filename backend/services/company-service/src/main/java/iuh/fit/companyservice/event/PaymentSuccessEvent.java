package iuh.fit.companyservice.event;

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
    private String packageId;
    private String packageName;
    private String employerEmail;
    private double amount;
    private int durationDays;
    private LocalDateTime paidAt;
    private int jobPostLimit;
    private String packageLabel;
    private String packageCategory;
    private String packageType;
    private int quantity;

}
