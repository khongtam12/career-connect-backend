package iuh.fit.jobservice.dto.response;

import iuh.fit.jobservice.dto.CompanyDTO;
import iuh.fit.jobservice.dto.IndustryDTO;
import iuh.fit.jobservice.model.JobType;
import iuh.fit.jobservice.model.StatusJob;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class JobDetailResponse {
    private String jobId;
    private String title;
    private String description;
    private String candidateRequirements;
    private String salaryDetail;
    private String benefitsDetail;
    private String workSchedule;
    private String location;
    private double salaryMin;
    private double salaryMax;
    private boolean salaryNegotiable;
    private String experience;
    private LocalDate deadline;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private int views;
    private int numberOfApplications;
    private boolean isTop;
    private LocalDateTime deletedAt;
    private String rank;
    private String education;
    private int quantity;
    private String ageRange;
    private String industry;
    private StatusJob status;
    private JobType jobType;
    private String requirementTags;
    private String benefitTags;
    private String specialties;
    private String relatedCategories;
    private String skills;

    private CompanyDTO company;
    private IndustryDTO industryDTO;

}
