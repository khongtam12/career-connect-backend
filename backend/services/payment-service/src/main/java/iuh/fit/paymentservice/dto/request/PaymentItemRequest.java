package iuh.fit.paymentservice.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentItemRequest {
    private String packageId;
    private String packageName;
    private Long amount;
    private int durationDays;
    private int quantity;
}
