package iuh.fit.jobservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanySubscriptionDTO {
    private String id;
    private String companyId;
    private String packageId;
    private String packageLabel;
    private String packageCategory;
    private int jobPostLimit;
    private int jobPostedCount;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String status;
}
