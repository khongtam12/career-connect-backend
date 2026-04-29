package iuh.fit.jobservice.dto.response;

import iuh.fit.jobservice.model.StatusJob;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class JobAdminResponse {
    private String jobId;
    private String title;
    private String companyName;
    private String companyLogoUrl;
    private String location;
    private double salaryMin;

    private double salaryMax;
    private StatusJob status;
    private LocalDateTime createdAt;
    private int views;
    private int numberOfApplications;


}
