package iuh.fit.jobservice.dto.response;

import lombok.Data;

@Data
public class MonthlyJobStatsResponse {
    private long currentMonthJobs;
    private long previousMonthJobs;
}
