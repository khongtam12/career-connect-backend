package iuh.fit.userservice.client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;
import java.util.List;

import iuh.fit.userservice.dto.request.EmployerStatsRequestDTO;
import iuh.fit.userservice.dto.response.JobActivityDTO;
import iuh.fit.userservice.dto.response.EmployerStatsResponseDTO;
import iuh.fit.userservice.dto.response.MonthlyJobStatsDTO;

@FeignClient(name = "job-service", path = "/api/v1/job")
public interface JobClient {

    @GetMapping("/stats")
    @CircuitBreaker(name = "jobService", fallbackMethod = "fallbackGetStats")
    Map<String, Object> getStats();

    @GetMapping("/admin/dashboard-stats")
    @CircuitBreaker(name = "jobService", fallbackMethod = "fallbackGetDashboardStats")
    Map<String, Object> getDashboardStats();

    @PostMapping("/admin/employer-stats")
    @CircuitBreaker(name = "jobService", fallbackMethod = "fallbackGetEmployerStats")
    List<EmployerStatsResponseDTO> getEmployerStats(@RequestBody EmployerStatsRequestDTO request);

    @GetMapping("/admin/monthly-job-stats")
    @CircuitBreaker(name = "jobService", fallbackMethod = "fallbackGetMonthlyJobStats")
    MonthlyJobStatsDTO getMonthlyJobStats();

    @GetMapping("/admin/recent-activities")
    @CircuitBreaker(name = "jobService", fallbackMethod = "fallbackGetRecentActivities")
    List<JobActivityDTO> getRecentActivities(@RequestParam("limit") int limit);

    default Map<String, Object> fallbackGetStats(Throwable throwable) {
        return Map.of("totalJobs", 0, "openJobs", 0, "newJobs24h", 0, "pendingJobs", 0);
    }

    default Map<String, Object> fallbackGetDashboardStats(Throwable throwable) {
        return Map.of("totalJobs", 0, "activeJobs", 0, "pendingJobs", 0);
    }

    default List<EmployerStatsResponseDTO> fallbackGetEmployerStats(EmployerStatsRequestDTO request, Throwable throwable) {
        return List.of();
    }

    default MonthlyJobStatsDTO fallbackGetMonthlyJobStats(Throwable throwable) {
        MonthlyJobStatsDTO fallback = new MonthlyJobStatsDTO();
        fallback.setCurrentMonthJobs(0);
        fallback.setPreviousMonthJobs(0);
        return fallback;
    }

    default List<JobActivityDTO> fallbackGetRecentActivities(int limit, Throwable throwable) {
        return List.of();
    }

    @GetMapping("/admin/weekly-stats")
    @CircuitBreaker(name = "jobService", fallbackMethod = "fallbackGetWeeklyStats")
    List<Map<String, Object>> getWeeklyStats();

    default List<Map<String, Object>> fallbackGetWeeklyStats(Throwable throwable) {
        return List.of();
    }
}
