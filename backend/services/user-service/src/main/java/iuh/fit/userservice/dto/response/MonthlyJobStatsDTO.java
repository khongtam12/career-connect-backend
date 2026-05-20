package iuh.fit.userservice.dto.response;

import lombok.Data;

@Data
public class MonthlyJobStatsDTO {
    private long currentMonthJobs;
    private long previousMonthJobs;
}
