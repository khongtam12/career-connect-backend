package iuh.fit.paymentservice.dto.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CompanySubscriptionRequest {
    private String companyId;
    private String packageId;
    private int jobPostLimit;
    private int durationDays;


}
