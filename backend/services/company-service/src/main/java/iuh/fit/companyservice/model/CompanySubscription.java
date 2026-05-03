package iuh.fit.companyservice.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Builder
@Entity
@Getter
@Setter
@NoArgsConstructor


@Table(name = "company_subscriptions")
public class CompanySubscription {
    @Id
    private String id;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "company_id")
    private Company company;
    private String packageId;
    private String packageLabel;
    private int jobPostLimit;

    private int jobPostedCount;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    @Enumerated(EnumType.STRING)
    private StatusPackage status;

    public CompanySubscription(String id, Company company, String packageId, String packageLabel, int jobPostLimit, int jobPostedCount,
            LocalDateTime startDate, LocalDateTime endDate, StatusPackage status) {
        this.id = id;
        this.company = company;
        this.packageId = packageId;
        this.packageLabel = packageLabel;
        this.jobPostLimit = jobPostLimit;
        this.jobPostedCount = jobPostedCount;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public String getPackageId() {
        return packageId;
    }

    public void setPackageId(String packageId) {
        this.packageId = packageId;
    }

    public String getPackageLabel() {
        return packageLabel;
    }

    public void setPackageLabel(String packageLabel) {
        this.packageLabel = packageLabel;
    }

    public int getJobPostLimit() {
        return jobPostLimit;
    }

    public void setJobPostLimit(int jobPostLimit) {
        this.jobPostLimit = jobPostLimit;
    }

    public int getJobPostedCount() {
        return jobPostedCount;
    }

    public void setJobPostedCount(int jobPostedCount) {
        this.jobPostedCount = jobPostedCount;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public StatusPackage getStatus() {
        return status;
    }

    public void setStatus(StatusPackage status) {
        this.status = status;
    }
}
