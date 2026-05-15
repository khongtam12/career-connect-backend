package iuh.fit.jobservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanyMarketingAssignmentDTO {
    private String id;
    private String entitlementId;
    private String companyId;
    private String targetId;
    private String placement;
    private String packageId;
    private String packageLabel;
    private String packageCategory;
    private String packageType;
    private String targetScope;
    private LocalDateTime assignedAt;
    private LocalDateTime expiresAt;
    private String status;
}
