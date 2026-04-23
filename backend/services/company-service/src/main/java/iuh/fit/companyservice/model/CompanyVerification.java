package iuh.fit.companyservice.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
@Builder
@Entity
@Getter
@Setter

@NoArgsConstructor
@Table(name = "company_verifications")
public class CompanyVerification {

    @Id
    private String verificationId;

    @ManyToOne
    @JoinColumn(name = "company_id")
    private Company company;
    private String submittedTaxCode;
    private String businessLicense;



    private String verifiedBy; // adminId
    private LocalDateTime submittedAt;
    private LocalDateTime verifiedAt;

    private String note;

    @Enumerated(EnumType.STRING)
    private StatusVerification status;

    public CompanyVerification() {
    }

    public CompanyVerification(String verificationId, Company company, String submittedTaxCode, String businessLicense, String verifiedBy, LocalDateTime submittedAt, LocalDateTime verifiedAt, String note, StatusVerification status) {
        this.verificationId = verificationId;
        this.company = company;
        this.submittedTaxCode = submittedTaxCode;
        this.businessLicense = businessLicense;
        this.verifiedBy = verifiedBy;
        this.submittedAt = submittedAt;
        this.verifiedAt = verifiedAt;
        this.note = note;
        this.status = status;
    }

    public String getVerificationId() {
        return verificationId;
    }

    public void setVerificationId(String verificationId) {
        this.verificationId = verificationId;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public String getSubmittedTaxCode() {
        return submittedTaxCode;
    }

    public void setSubmittedTaxCode(String submittedTaxCode) {
        this.submittedTaxCode = submittedTaxCode;
    }

    public String getBusinessLicense() {
        return businessLicense;
    }

    public void setBusinessLicense(String businessLicense) {
        this.businessLicense = businessLicense;
    }

    public String getVerifiedBy() {
        return verifiedBy;
    }

    public void setVerifiedBy(String verifiedBy) {
        this.verifiedBy = verifiedBy;
    }

    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(LocalDateTime submittedAt) {
        this.submittedAt = submittedAt;
    }

    public LocalDateTime getVerifiedAt() {
        return verifiedAt;
    }

    public void setVerifiedAt(LocalDateTime verifiedAt) {
        this.verifiedAt = verifiedAt;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public StatusVerification getStatus() {
        return status;
    }

    public void setStatus(StatusVerification status) {
        this.status = status;
    }
}