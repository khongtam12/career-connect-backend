package iuh.fit.userservice.controller;

import iuh.fit.userservice.dto.response.ApiResponse;
import iuh.fit.userservice.dto.response.TopEmployerDTO;
import iuh.fit.userservice.service.AdminDashboardService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/dashboard")
public class AdminDashboardController {

    private final AdminDashboardService dashboardService;

    public AdminDashboardController(AdminDashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/stats")
    public ApiResponse<Map<String, Object>> getStats() {
        return ApiResponse.success(dashboardService.getStats());
    }

    @GetMapping("/weekly-revenue")
    public ApiResponse<List<Map<String, Object>>> getWeeklyRevenue() {
        return ApiResponse.success(dashboardService.getWeeklyRevenue());
    }

    @GetMapping("/recent-activities")
    public ApiResponse<List<Map<String, Object>>> getRecentActivities() {
        return ApiResponse.success(dashboardService.getRecentActivities());
    }

    @GetMapping("/top-employers")
    public ApiResponse<List<TopEmployerDTO>> getTopEmployers() {
        return ApiResponse.success(dashboardService.getTopEmployers());
    }

    @GetMapping("/weekly-interactions")
    public ApiResponse<List<Map<String, Object>>> getWeeklyInteractions() {
        return ApiResponse.success(dashboardService.getWeeklyInteractions());
    }
}
