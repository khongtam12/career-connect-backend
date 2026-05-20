package iuh.fit.jobservice.dto.response;

import lombok.Data;

@Data
public class EmployerStatsResponse {
    private String employerId;
    private Long jobCount;
    private Long viewCount;
}
