package iuh.fit.jobservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class JobResponse {

    private String jobId;
    private String employerId;
    private String companyId;
    private String companyName;
    private String logo;

    private String title;
    private String industry;
    private String location;
    private String jobType;
    private String experience;

    private double salaryMin;
    private double salaryMax;
    private boolean salaryNegotiable;

    private String deadline;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private int views;
    private int applicants;
    private boolean isTop;
    private String status;

    private String packageId;
    private String packageLabel;

    // Thông tin chung
    private String rank;
    private String education;
    private int quantity;
    private String ageRange;

    // Tags
    private List<String> requirementTags;
    private List<String> benefitTags;
    private List<String> specialties;

    // Rich text
    private String description;
    private String candidateRequirements;
    private String salaryDetail;
    private String benefitsDetail;
    private String workSchedule;

    // Danh mục & Kỹ năng
    private List<String> relatedCategories;
    private List<String> skills;
}
