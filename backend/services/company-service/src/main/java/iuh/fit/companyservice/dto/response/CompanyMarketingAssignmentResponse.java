package iuh.fit.companyservice.dto.response;

import iuh.fit.companyservice.model.MarketingTargetScope;
import iuh.fit.companyservice.model.StatusMarketingAssignment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanyMarketingAssignmentResponse {
    private String id;
    private String entitlementId;
    private String companyId;
    private String targetId;
    private String placement;
    private String packageId;
    private String packageLabel;
    private String packageCategory;
    private String packageType;
    private MarketingTargetScope targetScope;
    private LocalDateTime assignedAt;
    private LocalDateTime expiresAt;
    private StatusMarketingAssignment status;
}
