package iuh.fit.companyservice.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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

    @ManyToOne
    @JoinColumn(name = "company_id")
    @JsonBackReference
    private Company company;
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
