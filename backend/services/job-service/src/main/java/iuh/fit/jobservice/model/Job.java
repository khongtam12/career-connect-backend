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

    private LocalDateTime deletedAt; // null = chưa xóa, có giá trị = đã xóa mềm

    // Thông tin chung
    private String rank;

    private String education;

    private int quantity;

    private String ageRange;

    private String industry;

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
            String title,
            String description,
            String candidateRequirements,
            String benefitsDetail,
            double salaryMin,
            double salaryMax,
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
        this.title = title;
        this.description = description;
        this.candidateRequirements = candidateRequirements;
        this.benefitsDetail = benefitsDetail;
        this.salaryMin = salaryMin;
        this.salaryMax = salaryMax;
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



}