package iuh.fit.userservice.dto.response;

import lombok.Data;

@Data
public class EmployerStatsResponseDTO {
    private String employerId;
    private Long jobCount;
    private Long viewCount;
}
