package iuh.fit.jobservice.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "jobs")
@Getter
@Setter
@AllArgsConstructor
public class Job {

    @Id
    private String jobId;

    private String companyId;

    private String companyName;

    // Employer (người đăng tin)
    private String employerId;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT")
    private String candidateRequirements;

    @Column(columnDefinition = "TEXT")
    private String salaryDetail;

    @Column(columnDefinition = "TEXT")
    private String benefitsDetail;

    @Column(columnDefinition = "TEXT")
    private String workSchedule;

    private String location;

    private double salaryMin;

    private double salaryMax;

    private boolean salaryNegotiable;

    @Column(name = "experience_required", nullable = false)
    private String experience;

    private LocalDate deadline;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private int views;

    private int numberOfApplications;

    private boolean isTop;

    private String companySubscriptionId;

    private String packageId;

    private String packageLabel;

    private LocalDateTime deletedAt; // null = chưa xóa, có giá trị = đã xóa mềm

    // Thông tin chung
    private String rank;

    private String education;

    private int quantity;

    private String ageRange;

    private String industryId;

    @Enumerated(EnumType.STRING)
    private StatusJob status;

    @Enumerated(EnumType.STRING)
    private JobType jobType;

    // Tags — stored as JSON arrays
    @Column(columnDefinition = "TEXT")
    private String requirementTags;

    @Column(columnDefinition = "TEXT")
    private String benefitTags;

    @Column(columnDefinition = "TEXT")
    private String specialties;

    @Column(columnDefinition = "TEXT")
    private String relatedCategories;

    @Column(columnDefinition = "TEXT")
    private String skills;

    // Extra info
    private String companyLogoUrl;
    private String companySize;
    private String companyAddress;
    private String jobLevel;
    private String workType;
    private String employmentType;

    private String provinceCode;
    private String district;

    private String contactEmail;
    private String contactPhone;

    @OneToMany(mappedBy = "job", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Favourite> favourites;

    public Job() {}

    public Job(
            String jobId,
            String companyId,
            String companyName,
            String employerId,
            String industryId,
            String title,
            String description,
            String candidateRequirements,
            String benefitsDetail,
            double salaryMin,
            double salaryMax,
            boolean salaryNegotiable,
            String location,
            String experience,
            LocalDate deadline,
            LocalDateTime createdAt,
            LocalDateTime updatedAt,
            int numberOfApplications,
            StatusJob status,
            JobType jobType,
            List<Favourite> favourites
    ) {
        this.jobId = jobId;
        this.companyId = companyId;
        this.companyName = companyName;
        this.employerId = employerId;
        this.industryId = industryId;
        this.title = title;
        this.description = description;
        this.candidateRequirements = candidateRequirements;
        this.benefitsDetail = benefitsDetail;
        this.salaryMin = salaryMin;
        this.salaryMax = salaryMax;
        this.salaryNegotiable = salaryNegotiable;
        this.location = location;
        this.experience = experience;
        this.deadline = deadline;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.numberOfApplications = numberOfApplications;
        this.status = status;
        this.jobType = jobType;
        this.favourites = favourites;
    }

    @PrePersist
    public void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        if (createdAt == null) {
            createdAt = now;
        }
        if (updatedAt == null) {
            updatedAt = now;
        }
    }

    @PreUpdate
    public void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // ===== Getter & Setter =====


    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getEmployerId() {
        return employerId;
    }

    public void setEmployerId(String employerId) {
        this.employerId = employerId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCandidateRequirements() {
        return candidateRequirements;
    }

    public void setCandidateRequirements(String candidateRequirements) {
        this.candidateRequirements = candidateRequirements;
    }

    public String getSalaryDetail() {
        return salaryDetail;
    }

    public void setSalaryDetail(String salaryDetail) {
        this.salaryDetail = salaryDetail;
    }

    public String getBenefitsDetail() {
        return benefitsDetail;
    }

    public void setBenefitsDetail(String benefitsDetail) {
        this.benefitsDetail = benefitsDetail;
    }

    public String getWorkSchedule() {
        return workSchedule;
    }

    public void setWorkSchedule(String workSchedule) {
        this.workSchedule = workSchedule;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public double getSalaryMin() {
        return salaryMin;
    }

    public void setSalaryMin(double salaryMin) {
        this.salaryMin = salaryMin;
    }

    public double getSalaryMax() {
        return salaryMax;
    }

    public void setSalaryMax(double salaryMax) {
        this.salaryMax = salaryMax;
    }

    public boolean isSalaryNegotiable() {
        return salaryNegotiable;
    }

    public void setSalaryNegotiable(boolean salaryNegotiable) {
        this.salaryNegotiable = salaryNegotiable;
    }

    public String getExperience() {
        return experience;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }

    public LocalDate getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDate deadline) {
        this.deadline = deadline;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public int getViews() {
        return views;
    }

    public void setViews(int views) {
        this.views = views;
    }

    public int getNumberOfApplications() {
        return numberOfApplications;
    }

    public void setNumberOfApplications(int numberOfApplications) {
        this.numberOfApplications = numberOfApplications;
    }

    public boolean isTop() {
        return isTop;
    }

    public void setTop(boolean top) {
        isTop = top;
    }

    public String getCompanySubscriptionId() {
        return companySubscriptionId;
    }

    public void setCompanySubscriptionId(String companySubscriptionId) {
        this.companySubscriptionId = companySubscriptionId;
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

    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getAgeRange() {
        return ageRange;
    }

    public void setAgeRange(String ageRange) {
        this.ageRange = ageRange;
    }

    public String getIndustry() {
        return industryId;
    }

    public void setIndustry(String industryId) {
        this.industryId = industryId;
    }

    public StatusJob getStatus() {
        return status;
    }

    public void setStatus(StatusJob status) {
        this.status = status;
    }

    public JobType getJobType() {
        return jobType;
    }

    public void setJobType(JobType jobType) {
        this.jobType = jobType;
    }

    public String getRequirementTags() {
        return requirementTags;
    }

    public void setRequirementTags(String requirementTags) {
        this.requirementTags = requirementTags;
    }

    public String getBenefitTags() {
        return benefitTags;
    }

    public void setBenefitTags(String benefitTags) {
        this.benefitTags = benefitTags;
    }

    public String getSpecialties() {
        return specialties;
    }

    public void setSpecialties(String specialties) {
        this.specialties = specialties;
    }

    public String getRelatedCategories() {
        return relatedCategories;
    }

    public void setRelatedCategories(String relatedCategories) {
        this.relatedCategories = relatedCategories;
    }

    public String getSkills() {
        return skills;
    }

    public void setSkills(String skills) {
        this.skills = skills;
    }

    public String getCompanyLogoUrl() {
        return companyLogoUrl;
    }

    public void setCompanyLogoUrl(String companyLogoUrl) {
        this.companyLogoUrl = companyLogoUrl;
    }

    public String getCompanySize() {
        return companySize;
    }

    public void setCompanySize(String companySize) {
        this.companySize = companySize;
    }

    public String getCompanyAddress() {
        return companyAddress;
    }

    public void setCompanyAddress(String companyAddress) {
        this.companyAddress = companyAddress;
    }

    public String getJobLevel() {
        return jobLevel;
    }

    public void setJobLevel(String jobLevel) {
        this.jobLevel = jobLevel;
    }

    public String getWorkType() {
        return workType;
    }

    public void setWorkType(String workType) {
        this.workType = workType;
    }

    public String getEmploymentType() {
        return employmentType;
    }

    public void setEmploymentType(String employmentType) {
        this.employmentType = employmentType;
    }

    public String getProvinceCode() {
        return provinceCode;
    }

    public void setProvinceCode(String provinceCode) {
        this.provinceCode = provinceCode;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public List<Favourite> getFavourites() {
        return favourites;
    }

    public void setFavourites(List<Favourite> favourites) {
        this.favourites = favourites;
    }
}