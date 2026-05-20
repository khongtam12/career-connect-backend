package iuh.fit.userservice.client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;
import iuh.fit.userservice.dto.response.PaymentActivityDTO;

@FeignClient(name = "payment-service", path = "/api/v1/package")
public interface PaymentClient {

    @GetMapping("/payments/dashboard-stats")
    @CircuitBreaker(name = "paymentService", fallbackMethod = "fallbackGetDashboardStats")
    Map<String, Object> getDashboardStats();

    @GetMapping("/payments/recent")
    @CircuitBreaker(name = "paymentService", fallbackMethod = "fallbackGetRecentPayments")
    List<PaymentActivityDTO> getRecentPayments(@RequestParam("limit") int limit);

    default Map<String, Object> fallbackGetDashboardStats(Throwable throwable) {
        return Map.of(
                "monthlyRevenue", 0,
                "weeklyRevenue", List.of(),
                "revenueGrowth", 0
        );
    }

    default List<PaymentActivityDTO> fallbackGetRecentPayments(int limit, Throwable throwable) {
        return List.of();
    }
}
