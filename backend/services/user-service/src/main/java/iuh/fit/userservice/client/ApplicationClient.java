package iuh.fit.userservice.client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import iuh.fit.userservice.dto.response.ApiResponse;

import java.util.Map;
import java.util.List;

@FeignClient(name = "application-service", path = "/api/v1/apply")
public interface ApplicationClient {

    @GetMapping("/admin/weekly-stats")
    @CircuitBreaker(name = "applicationService", fallbackMethod = "fallbackGetWeeklyStats")
    ApiResponse<List<Map<String, Object>>> getWeeklyStats();

    default ApiResponse<List<Map<String, Object>>> fallbackGetWeeklyStats(Throwable throwable) {
        ApiResponse<List<Map<String, Object>>> res = new ApiResponse<>();
        res.setStatus(200);
        res.setMessage("Fallback");
        res.setData(List.of());
        return res;
    }
}
