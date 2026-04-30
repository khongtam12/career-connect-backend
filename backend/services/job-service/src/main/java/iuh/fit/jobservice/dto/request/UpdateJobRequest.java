package iuh.fit.jobservice.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateJobRequest {

    private String title;
    private String industry;
    private String address;
    private String jobType;
    private String experience;

    private Double salaryMin;
    private Double salaryMax;
    private Boolean salaryNegotiable;

    private String deadline;

    // Thông tin chung
    private String rank;
    private String education;
    private Integer quantity;
    private String ageRange;

    // Tags
    private List<String> requirementTags;
    private List<String> benefitTags;
    private List<String> specialties;

    // Rich text (HTML)
    private String description;
    private String candidateRequirements;
    private String salaryDetail;
    private String benefitsDetail;
    private String workSchedule;

    // Danh mục & Kỹ năng
    private List<String> relatedCategories;
    private List<String> skills;
}
