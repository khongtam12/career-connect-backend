package iuh.fit.paymentservice.controller;

import iuh.fit.paymentservice.dto.response.PaymentActivityDTO;
import iuh.fit.paymentservice.model.Payment;
import iuh.fit.paymentservice.model.StatusPayment;
import iuh.fit.paymentservice.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.*;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

@RestController
@RequestMapping("/api/v1/package/payments")
@RequiredArgsConstructor
public class PaymentDashboardController {

    private final PaymentRepository paymentRepository;

    @GetMapping("/dashboard-stats")
    public Map<String, Object> getDashboardStats() {
        Map<String, Object> result = new LinkedHashMap<>();

        // Monthly revenue
        LocalDateTime monthStart = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        LocalDateTime monthEnd = LocalDate.now().with(TemporalAdjusters.lastDayOfMonth()).atTime(23, 59, 59);
        double monthlyRevenue = paymentRepository.sumAmountByStatusAndPaidAtBetween(
                StatusPayment.SUCCEEDED, monthStart, monthEnd);
        result.put("monthlyRevenue", monthlyRevenue);
        LocalDate prevMonthStartDate = monthStart.toLocalDate().minusMonths(1).withDayOfMonth(1);
        LocalDateTime prevMonthStart = prevMonthStartDate.atStartOfDay();
        LocalDateTime prevMonthEnd = prevMonthStartDate.with(TemporalAdjusters.lastDayOfMonth()).atTime(23, 59, 59);
        double prevMonthlyRevenue = paymentRepository.sumAmountByStatusAndPaidAtBetween(
            StatusPayment.SUCCEEDED, prevMonthStart, prevMonthEnd);
        double revenueGrowth = calcGrowthPercent(monthlyRevenue, prevMonthlyRevenue);
        result.put("revenueGrowth", revenueGrowth);

        // Weekly revenue (Rolling 7 days ending today)
        LocalDate today = LocalDate.now();
        List<Map<String, Object>> weeklyRevenue = new ArrayList<>();

        for (int i = 6; i >= 0; i--) {
            LocalDate day = today.minusDays(i);
            LocalDateTime dayStart = day.atStartOfDay();
            LocalDateTime dayEnd = day.atTime(23, 59, 59);

            double dayRevenue = 0;
            try {
                dayRevenue = paymentRepository.sumAmountByStatusAndPaidAtBetween(
                        StatusPayment.SUCCEEDED, dayStart, dayEnd);
            } catch (Exception ignored) {}

            Map<String, Object> entry = new LinkedHashMap<>();
            entry.put("day", day.getDayOfWeek().name());
            entry.put("revenue", dayRevenue);
            weeklyRevenue.add(entry);
        }

        result.put("weeklyRevenue", weeklyRevenue);
        return result;
    }

        @GetMapping("/recent")
        public List<PaymentActivityDTO> getRecentPayments(@RequestParam(value = "limit", defaultValue = "10") int limit) {
        int size = limit <= 0 ? 8 : Math.min(limit, 20);
        var page = paymentRepository.findByStatusOrderByPaidAtDesc(
            StatusPayment.SUCCEEDED,
            PageRequest.of(0, size, Sort.by("paidAt").descending())
        );
        return page.getContent().stream()
            .map(payment -> PaymentActivityDTO.builder()
                .paymentId(payment.getPaymentId())
                .companyId(payment.getCompanyId())
                .employerEmail(payment.getEmployerEmail())
                .amount(payment.getAmount())
                .paidAt(payment.getPaidAt())
                .build())
            .toList();
        }

    private double calcGrowthPercent(double current, double previous) {
        if (previous == 0) {
            return current == 0 ? 0 : 100;
        }
        return ((current - previous) / previous) * 100;
    }
}
