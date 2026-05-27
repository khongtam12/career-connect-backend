package iuh.fit.jobservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class JobCardResponse {
    private String jobId;
    private String companyId;
    private String industryId;
    private String companyName;
    private String logo;
    private String title;
    private String location;
    private double salaryMin;
    private double salaryMax;
    private boolean salaryNegotiable;
    private String deadline;
    private boolean deadlineExpired;
    private LocalDateTime createdAt;
    private int views;
    private boolean isTop;
    private String marketingPackageCategory;
    private String marketingPackageType;
    private List<String> benefitTags;
}
