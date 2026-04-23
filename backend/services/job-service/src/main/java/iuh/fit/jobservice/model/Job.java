package iuh.fit.jobservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "jobs")
public class Job {

    @Id
    private String jobId;

    private String companyId;

    // Employer (người đăng tin) — lấy từ X-User-Id header
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

    @OneToMany(mappedBy = "job", cascade = CascadeType.ALL)
    private List<Favourite> favourites;
}