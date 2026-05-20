package iuh.fit.jobservice.dto.request;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class EmployerStatsRequest {
    private List<String> employerIds;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
