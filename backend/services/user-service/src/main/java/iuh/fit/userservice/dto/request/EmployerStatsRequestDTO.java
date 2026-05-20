package iuh.fit.userservice.dto.request;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class EmployerStatsRequestDTO {
    private List<String> employerIds;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
