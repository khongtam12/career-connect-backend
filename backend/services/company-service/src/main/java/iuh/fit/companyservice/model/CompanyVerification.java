package iuh.fit.companyservice.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "company_verifications")
public class CompanyVerification {

    @Id
    private String verificationId;

    private String companyId;

    private String businessLicense;

    private String taxCode;

    private String verifiedBy; // adminId

    private LocalDateTime verifiedAt;

    private String note;

    @Enumerated(EnumType.STRING)
    private StatusVerification status;
}