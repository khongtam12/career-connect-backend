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
@Table(name = "company_marketing_assignments")
public class CompanyMarketingAssignment {
    @Id
    private String id;

    private String entitlementId;
    @ManyToOne
    @JoinColumn(name = "company_id")
    @JsonBackReference
    private Company company;
    private String targetId;
    private String placement;
    private String packageId;
    private String packageLabel;
    private String packageCategory;
    private String packageType;

    @Enumerated(EnumType.STRING)
    private MarketingTargetScope targetScope;

    private LocalDateTime assignedAt;
    private LocalDateTime expiresAt;

    @Enumerated(EnumType.STRING)
    private StatusMarketingAssignment status;
}
