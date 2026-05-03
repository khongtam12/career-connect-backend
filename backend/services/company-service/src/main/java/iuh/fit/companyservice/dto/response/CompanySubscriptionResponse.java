package iuh.fit.companyservice.dto.response;

import iuh.fit.companyservice.model.StatusPackage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanySubscriptionResponse {
    private String id;
    private String companyId;
    private String packageId;
    private String packageLabel;
    private int jobPostLimit;
    private int jobPostedCount;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private StatusPackage status;
}
