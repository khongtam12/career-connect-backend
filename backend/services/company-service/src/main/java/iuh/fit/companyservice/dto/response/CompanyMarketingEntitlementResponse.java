package iuh.fit.companyservice.dto.response;

import iuh.fit.companyservice.model.MarketingTargetScope;
import iuh.fit.companyservice.model.StatusMarketingEntitlement;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanyMarketingEntitlementResponse {
    private String id;
    private String companyId;
    private String paymentId;
    private String packageId;
    private String packageLabel;
    private String packageCategory;
    private String packageType;
    private MarketingTargetScope targetScope;
    private int usageLimit;
    private int usedCount;
    private int remainingCount;
    private int quantity;
    private int durationDays;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private StatusMarketingEntitlement status;
}
