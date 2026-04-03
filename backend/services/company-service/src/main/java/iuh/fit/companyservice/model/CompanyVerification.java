package iuh.fit.companyservice.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
@Builder
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "company_verifications")
public class CompanyVerification {

    @Id
    private String verificationId;

    @ManyToOne
    @JoinColumn(name = "company_id")
    private Company company;

    private String businessLicense;

    private String taxCode;

    private String verifiedBy; // adminId

    private LocalDateTime verifiedAt;

    private String note;

    @Enumerated(EnumType.STRING)
    private StatusVerification status;
}