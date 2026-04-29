package iuh.fit.companyservice.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter

@Table(name = "companies")
public class Company {
    @Id
    private String companyId;

    private String name;

    private String logo;
    private String taxCode;
    private String website;

    private String email;

    private String phone;

    private String address;

    private String description;

    private int companySize;

    private int foundedYear;
    @Enumerated(EnumType.STRING)
    private StatusCompany statusCompany;
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    private ApprovalStatus approvalStatus = ApprovalStatus.PENDING;

    private String approvedBy;

    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<CompanySubscription> subscriptions = new ArrayList<>();

    public Company(String companyId, String name, String logo, String taxCode, String website, String email,
            String phone, String address, String description, int companySize, int foundedYear,
            StatusCompany statusCompany, LocalDateTime createdAt, List<CompanySubscription> subscriptions) {
        this.companyId = companyId;
        this.name = name;
        this.logo = logo;
        this.taxCode = taxCode;
        this.website = website;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.description = description;
        this.companySize = companySize;
        this.foundedYear = foundedYear;
        this.statusCompany = statusCompany;
        this.createdAt = createdAt;
        this.subscriptions = subscriptions;
    }

    public Company(String companyId) {
        this.companyId = companyId;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getTaxCode() {
        return taxCode;
    }

    public void setTaxCode(String taxCode) {
        this.taxCode = taxCode;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getCompanySize() {
        return companySize;
    }

    public void setCompanySize(int companySize) {
        this.companySize = companySize;
    }

    public int getFoundedYear() {
        return foundedYear;
    }

    public void setFoundedYear(int foundedYear) {
        this.foundedYear = foundedYear;
    }

    public StatusCompany getStatusCompany() {
        return statusCompany;
    }

    public void setStatusCompany(StatusCompany statusCompany) {
        this.statusCompany = statusCompany;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<CompanySubscription> getSubscriptions() {
        return subscriptions;
    }

    public void setSubscriptions(List<CompanySubscription> subscriptions) {
        this.subscriptions = subscriptions;
    }

    public ApprovalStatus getApprovalStatus() {
        return approvalStatus;
    }

    public void setApprovalStatus(ApprovalStatus approvalStatus) {
        this.approvalStatus = approvalStatus;
    }

    public String getApprovedBy() {
        return approvedBy;
    }

    public void setApprovedBy(String approvedBy) {
        this.approvedBy = approvedBy;
    }
}