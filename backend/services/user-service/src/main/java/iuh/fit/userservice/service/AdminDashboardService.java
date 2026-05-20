package iuh.fit.userservice.service;

import iuh.fit.userservice.client.CompanyClient;
import iuh.fit.userservice.client.JobClient;
import iuh.fit.userservice.client.PaymentClient;
import iuh.fit.userservice.dto.request.EmployerStatsRequestDTO;
import iuh.fit.userservice.dto.response.CompanyApprovalActivityDTO;
import iuh.fit.userservice.dto.response.JobActivityDTO;
import iuh.fit.userservice.dto.response.PaymentActivityDTO;
import iuh.fit.userservice.dto.response.EmployerStatsResponseDTO;
import iuh.fit.userservice.dto.response.TopEmployerDTO;
import iuh.fit.userservice.repository.CandidateRepository;
import iuh.fit.userservice.repository.EmployerRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
public class AdminDashboardService {

    private final CandidateRepository candidateRepository;
    private final EmployerRepository employerRepository;
    private final JobClient jobClient;
    private final PaymentClient paymentClient;
    private final CompanyClient companyClient;
    private final iuh.fit.userservice.client.ApplicationClient applicationClient;

    public AdminDashboardService(CandidateRepository candidateRepository,
                                  EmployerRepository employerRepository,
                                  JobClient jobClient,
                                  PaymentClient paymentClient,
                                  CompanyClient companyClient,
                                  iuh.fit.userservice.client.ApplicationClient applicationClient) {
        this.candidateRepository = candidateRepository;
        this.employerRepository = employerRepository;
        this.jobClient = jobClient;
        this.paymentClient = paymentClient;
        this.companyClient = companyClient;
        this.applicationClient = applicationClient;
    }

    // Tổng hợp số liệu thống kê cho bảng điều khiển admin
    public Map<String, Object> getStats() {
        Map<String, Object> result = new LinkedHashMap<>();

        LocalDate now = LocalDate.now();
        LocalDate currentStart = now.withDayOfMonth(1);
        LocalDate currentEnd = now.withDayOfMonth(now.lengthOfMonth());
        LocalDate previousStart = currentStart.minusMonths(1);
        LocalDate previousEnd = previousStart.withDayOfMonth(previousStart.lengthOfMonth());

        // Thống kê người dùng
        long totalCandidates = candidateRepository.count();
        long totalEmployers = employerRepository.count();
        long totalUsers = totalCandidates + totalEmployers;
        long newEmployersToday = employerRepository.countByCreatedAt(LocalDate.now());

        result.put("totalUsers", totalUsers);
        result.put("totalCandidates", totalCandidates);
        result.put("totalEmployers", totalEmployers);
        result.put("newEmployersToday", newEmployersToday);
        long currentNewCandidates = candidateRepository.countByCreatedAtBetween(currentStart, currentEnd);
        long currentNewEmployers = employerRepository.countByCreatedAtBetween(currentStart, currentEnd);
        long previousNewCandidates = candidateRepository.countByCreatedAtBetween(previousStart, previousEnd);
        long previousNewEmployers = employerRepository.countByCreatedAtBetween(previousStart, previousEnd);
        long currentNewUsers = currentNewCandidates + currentNewEmployers;
        long previousNewUsers = previousNewCandidates + previousNewEmployers;
        result.put("userGrowth", calcGrowthPercent(currentNewUsers, previousNewUsers));
        result.put("employerGrowth", 0);

        // Thống kê tin tuyển dụng (gọi qua FeignClient)
        try {
            Map<String, Object> jobStats = jobClient.getStats();
            result.put("totalJobs", toInt(jobStats.get("totalJobs")));
            result.put("activeJobs", toInt(jobStats.get("openJobs")));
            result.put("pendingApprovals", toInt(jobStats.get("pendingJobs")));
            double jobGrowth = 0;
            try {
                var monthlyJobStats = jobClient.getMonthlyJobStats();
                jobGrowth = calcGrowthPercent(monthlyJobStats.getCurrentMonthJobs(), monthlyJobStats.getPreviousMonthJobs());
            } catch (Exception ignored) {}
            result.put("jobGrowth", jobGrowth);
        } catch (Exception e) {
            result.put("totalJobs", 0);
            result.put("activeJobs", 0);
            result.put("pendingApprovals", 0);
            result.put("jobGrowth", 0);
        }

        // Thống kê thanh toán (gọi qua FeignClient)
        try {
            Map<String, Object> paymentStats = paymentClient.getDashboardStats();
            result.put("monthlyRevenue", paymentStats.getOrDefault("monthlyRevenue", 0));
            Object growthValue = paymentStats.getOrDefault("revenueGrowth", 0);
            double revenueGrowth = growthValue instanceof Number ? ((Number) growthValue).doubleValue() : 0;
            result.put("revenueGrowth", revenueGrowth);
        } catch (Exception e) {
            result.put("monthlyRevenue", 0);
            result.put("revenueGrowth", 0);
        }

        result.put("newToday", newEmployersToday);
        result.put("monthlyApplications", 125); // Giả lập số lượng ứng tuyển trong tháng

        return result;
    }

    // Lấy doanh thu theo tuần (gọi qua payment-service)
    public List<Map<String, Object>> getWeeklyRevenue() {
        try {
            Map<String, Object> paymentStats = paymentClient.getDashboardStats();
            Object weeklyRevenue = paymentStats.get("weeklyRevenue");
            if (weeklyRevenue instanceof List) {
                return (List<Map<String, Object>>) weeklyRevenue;
            }
        } catch (Exception e) {
            // fallback
        }

        // Default: trả về dữ liệu trống cho 7 ngày gần nhất (rolling 7 days)
        List<Map<String, Object>> result = new ArrayList<>();
        LocalDate today = LocalDate.now();
        for (int i = 6; i >= 0; i--) {
            LocalDate day = today.minusDays(i);
            Map<String, Object> entry = new LinkedHashMap<>();
            entry.put("day", day.getDayOfWeek().name());
            entry.put("revenue", 0);
            result.add(entry);
        }
        return result;
    }

    public List<Map<String, Object>> getWeeklyInteractions() {
        Map<String, Long> newJobsMap = new HashMap<>();
        try {
            List<Map<String, Object>> jobs = jobClient.getWeeklyStats();
            for (Map<String, Object> job : jobs) {
                newJobsMap.put(job.get("date").toString(), ((Number) job.get("count")).longValue());
            }
        } catch (Exception e) {}

        Map<String, Long> appsMap = new HashMap<>();
        try {
            iuh.fit.userservice.dto.response.ApiResponse<List<Map<String, Object>>> res = applicationClient.getWeeklyStats();
            if (res.getData() != null) {
                for (Map<String, Object> app : res.getData()) {
                    appsMap.put(app.get("date").toString(), ((Number) app.get("count")).longValue());
                }
            }
        } catch (Exception e) {}

        List<Map<String, Object>> result = new ArrayList<>();
        LocalDate now = LocalDate.now();
        for (int i = 6; i >= 0; i--) {
            LocalDate date = now.minusDays(i);
            String dateStr = date.toString();
            Map<String, Object> entry = new LinkedHashMap<>();
            int dow = date.getDayOfWeek().getValue();
            String label = dow == 7 ? "CN" : "T" + (dow + 1);
            
            entry.put("day", label);
            entry.put("applications", appsMap.getOrDefault(dateStr, 0L));
            entry.put("newJobs", newJobsMap.getOrDefault(dateStr, 0L));
            result.add(entry);
        }
        return result;
    }

    // Lấy danh sách hoạt động gần đây (nhà tuyển dụng, ứng viên mới tạo)
    public List<Map<String, Object>> getRecentActivities() {
        List<Map<String, Object>> activities = new ArrayList<>();
        Map<String, String> companyNameCache = new HashMap<>();

        // Lấy employer mới nhất
        var employers = employerRepository.findAll(
                org.springframework.data.domain.PageRequest.of(0, 5,
                        org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "createdAt"))
        ).getContent();

        for (var emp : employers) {
            Map<String, Object> act = new LinkedHashMap<>();
            act.put("type", "NEW_EMPLOYER");
            act.put("entityName", emp.getFullName() != null ? emp.getFullName() : emp.getEmail());
            act.put("createdAt", emp.getCreatedAt() != null ? emp.getCreatedAt().toString() : null);
            activities.add(act);
        }

        // Lấy candidate mới nhất
        var candidates = candidateRepository.findAll(
                org.springframework.data.domain.PageRequest.of(0, 5,
                        org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "createdAt"))
        ).getContent();

        for (var c : candidates) {
            Map<String, Object> act = new LinkedHashMap<>();
            act.put("type", "NEW_USER");
            act.put("entityName", c.getFullName() != null ? c.getFullName() : c.getEmail());
            act.put("createdAt", c.getCreatedAt() != null ? c.getCreatedAt().toString() : null);
            activities.add(act);
        }

        try {
            List<JobActivityDTO> jobActivities = jobClient.getRecentActivities(20);
            for (JobActivityDTO jobActivity : jobActivities) {
                Map<String, Object> act = new LinkedHashMap<>();
                String activityType = jobActivity.getType();
                String status = jobActivity.getStatus();
                if ("JOB_STATUS_CHANGED".equals(activityType) && status != null) {
                    String normalized = status.trim().toUpperCase();
                    if ("REJECTED".equals(normalized)) {
                        activityType = "JOB_REJECTED";
                    } else if ("ACTIVE".equals(normalized)) {
                        activityType = "JOB_APPROVED";
                    }
                }
                act.put("type", activityType);
                act.put("entityName", jobActivity.getTitle());
                act.put("companyName", jobActivity.getCompanyName());
                act.put("status", jobActivity.getStatus());
                act.put("applicationCount", jobActivity.getNumberOfApplications());
                act.put("createdAt", jobActivity.getEventAt() != null ? jobActivity.getEventAt().toString() : null);
                activities.add(act);
            }
        } catch (Exception ignored) {}

        try {
            List<CompanyApprovalActivityDTO> approvals = companyClient.getRecentApprovals(10);
            for (CompanyApprovalActivityDTO approval : approvals) {
                Map<String, Object> act = new LinkedHashMap<>();
                String status = approval.getStatus();
                act.put("type", "APPROVED".equalsIgnoreCase(status) ? "COMPANY_APPROVED" : "COMPANY_REJECTED");
                act.put("entityName", approval.getCompanyName() != null ? approval.getCompanyName() : approval.getCompanyId());
                act.put("createdAt", approval.getVerifiedAt() != null ? approval.getVerifiedAt().toString() : null);
                activities.add(act);
            }
        } catch (Exception ignored) {}

        try {
            List<PaymentActivityDTO> payments = paymentClient.getRecentPayments(10);
            for (PaymentActivityDTO payment : payments) {
                String companyId = payment.getCompanyId();
                String companyName = null;
                if (companyId != null && !companyId.isBlank()) {
                    companyName = companyNameCache.get(companyId);
                    if (companyName == null) {
                        try {
                            var company = companyClient.getCompanyById(companyId);
                            companyName = company != null ? company.getName() : null;
                        } catch (Exception ignored) {
                            companyName = null;
                        }
                        companyNameCache.put(companyId, companyName);
                    }
                }
                Map<String, Object> act = new LinkedHashMap<>();
                act.put("type", "PAYMENT_SUCCEEDED");
                act.put("entityName", companyName != null ? companyName : payment.getEmployerEmail());
                act.put("amount", payment.getAmount());
                act.put("createdAt", payment.getPaidAt() != null ? payment.getPaidAt().toString() : null);
                activities.add(act);
            }
        } catch (Exception ignored) {}

        // Sort by createdAt desc
        activities.sort((a, b) -> {
            String dateA = (String) a.get("createdAt");
            String dateB = (String) b.get("createdAt");
            if (dateA == null && dateB == null) return 0;
            if (dateA == null) return 1;
            if (dateB == null) return -1;
            return dateB.compareTo(dateA);
        });

        return activities.size() > 10 ? activities.subList(0, 10) : activities;
    }

    // Lấy danh sách nhà tuyển dụng tiêu biểu
    public List<TopEmployerDTO> getTopEmployers() {
        List<TopEmployerDTO> result = new ArrayList<>();

        var employers = employerRepository.findAll();
        Map<String, List<iuh.fit.userservice.model.Employer>> byCompany = new LinkedHashMap<>();
        Map<String, iuh.fit.userservice.model.Employer> employerById = new LinkedHashMap<>();

        for (var emp : employers) {
            if (emp.getEmployerId() != null) {
                employerById.put(emp.getEmployerId(), emp);
            }
            if (emp.getCompanyId() != null && !emp.getCompanyId().isBlank()) {
                byCompany.computeIfAbsent(emp.getCompanyId(), k -> new ArrayList<>()).add(emp);
            }
        }

        List<String> employerIds = new ArrayList<>(employerById.keySet());
        Map<String, Long> jobCountByEmployer = new HashMap<>();
        Map<String, Long> viewCountByEmployer = new HashMap<>();
        if (!employerIds.isEmpty()) {
            try {
                LocalDate today = LocalDate.now();
                LocalDate firstDay = today.withDayOfMonth(1);
                LocalDate firstDayNextMonth = firstDay.plusMonths(1);
                EmployerStatsRequestDTO request = new EmployerStatsRequestDTO();
                request.setEmployerIds(employerIds);
                request.setStartDate(firstDay.atStartOfDay());
                request.setEndDate(firstDayNextMonth.atStartOfDay());
                List<EmployerStatsResponseDTO> stats = jobClient.getEmployerStats(request);
                for (EmployerStatsResponseDTO row : stats) {
                    String employerId = row.getEmployerId();
                    if (employerId == null || employerId.isBlank()) {
                        continue;
                    }
                    jobCountByEmployer.put(employerId, row.getJobCount() != null ? row.getJobCount() : 0L);
                    viewCountByEmployer.put(employerId, row.getViewCount() != null ? row.getViewCount() : 0L);
                }
            } catch (Exception e) {
                // fallback: giữ mặc định 0
            }
        }

        List<TopEmployerDTO> unsortedList = new ArrayList<>();
        for (var entry : byCompany.entrySet()) {
            String companyId = entry.getKey();
            String companyName;
            String companyLogo = null;
            try {
                var companyDTO = companyClient.getCompanyById(companyId);
                companyName = companyDTO != null && companyDTO.getName() != null ? companyDTO.getName() : "N/A";
                companyLogo = companyDTO != null ? companyDTO.getLogo() : null;
            } catch (Exception e) {
                companyName = "Công ty #" + companyId;
            }

            long totalJobs = 0L;
            long totalViews = 0L;
            String email = null;
            for (var emp : entry.getValue()) {
                if (email == null && emp.getEmail() != null) {
                    email = emp.getEmail();
                }
                String employerId = emp.getEmployerId();
                totalJobs += jobCountByEmployer.getOrDefault(employerId, 0L);
                totalViews += viewCountByEmployer.getOrDefault(employerId, 0L);
            }

            TopEmployerDTO item = new TopEmployerDTO();
            item.setCompanyId(companyId);
            item.setCompanyName(companyName);
            item.setCompanyLogo(companyLogo);
            item.setEmail(email);
            item.setJobCount(totalJobs);
            item.setViewCount(totalViews);
            unsortedList.add(item);
        }

        unsortedList.sort((a, b) -> {
            int byJobs = Long.compare(
                    b.getJobCount() != null ? b.getJobCount() : 0L,
                    a.getJobCount() != null ? a.getJobCount() : 0L);
            if (byJobs != 0) return byJobs;
            return Long.compare(
                    b.getViewCount() != null ? b.getViewCount() : 0L,
                    a.getViewCount() != null ? a.getViewCount() : 0L);
        });

        int rank = 1;
        int limit = Math.min(50, unsortedList.size());
        for (int i = 0; i < limit; i++) {
            TopEmployerDTO item = unsortedList.get(i);
            item.setRank(rank++);
            result.add(item);
        }

        return result;
    }

    private int toInt(Object val) {
        if (val == null) return 0;
        if (val instanceof Number) return ((Number) val).intValue();
        try { return Integer.parseInt(val.toString()); } catch (Exception e) { return 0; }
    }

    private double calcGrowthPercent(long current, long previous) {
        if (previous == 0) {
            return current == 0 ? 0 : 100;
        }
        return ((double) (current - previous) / previous) * 100;
    }

}
