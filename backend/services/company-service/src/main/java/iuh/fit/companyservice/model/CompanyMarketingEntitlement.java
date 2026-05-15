package iuh.fit.companyservice.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "company_marketing_entitlements")
public class CompanyMarketingEntitlement {
    @Id
    private String id;

    private String companyId;
    private String paymentId;
    private String packageId;
    private String packageLabel;
    private String packageCategory;
    private String packageType;

    @Enumerated(EnumType.STRING)
    private MarketingTargetScope targetScope;

    private int usageLimit;
    private int usedCount;
    private int quantity;
    private int durationDays;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    @Enumerated(EnumType.STRING)
    private StatusMarketingEntitlement status;
}
