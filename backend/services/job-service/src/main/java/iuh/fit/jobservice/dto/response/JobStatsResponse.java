package iuh.fit.jobservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class JobStatsResponse {
    private long active;
    private long paused;
    private long closed;
    private int totalApplicants;
}
