package iuh.fit.companyservice.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanyMarketingEntitlementRequest {
    private String companyId;
    private String paymentId;
    private String packageId;
    private String packageLabel;
    private String packageCategory;
    private String packageType;
    private int jobLimit;
    private int quantity;
    private int durationDays;
}
