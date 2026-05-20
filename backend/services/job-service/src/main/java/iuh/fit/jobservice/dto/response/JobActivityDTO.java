package iuh.fit.jobservice.dto.response;

import iuh.fit.jobservice.model.StatusJob;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobActivityDTO {
    private String type;
    private String jobId;
    private String title;
    private String companyName;
    private StatusJob status;
    private LocalDateTime eventAt;
    private Integer numberOfApplications;
}
