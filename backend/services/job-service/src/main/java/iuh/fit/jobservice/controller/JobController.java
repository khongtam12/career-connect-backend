package iuh.fit.jobservice.controller;

import iuh.fit.jobservice.dto.JobFilterOptions;
import iuh.fit.jobservice.dto.JobStats;
import iuh.fit.jobservice.dto.request.ApplyMarketingPackageRequest;
import iuh.fit.jobservice.dto.request.CreateJobRequest;
import iuh.fit.jobservice.dto.request.EmployerStatsRequest;
import iuh.fit.jobservice.dto.request.RenewJobRequest;
import iuh.fit.jobservice.dto.request.UpdateJobRequest;
import iuh.fit.jobservice.dto.response.*;
import iuh.fit.jobservice.service.JobService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/job")
public class JobController {

    private static final Logger log = LoggerFactory.getLogger(JobController.class);
    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    // ===== HEALTH CHECK =====
    @GetMapping("/health")
    public String health() {
        return "job service is working";
    }

    // ===== FILTER + STATS (từ HEAD) =====
    @GetMapping("/filters")
    public JobFilterOptions getFilterOptions() {
        return jobService.getFilterOptions();
    }

    @GetMapping("/stats")
    public JobStats getStats() {
        return jobService.getStats();
    }

    // ===== EMPLOYER APIs =====

    // tạo tin tuyển dụng
    @PostMapping("/employer/create")
    public ResponseEntity<?> createJob(
            @RequestHeader("X-User-Id") String employerId,
            @RequestBody CreateJobRequest request
    ) {
        JobResponse response = jobService.createJob(employerId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // cập nhật tin
    @PutMapping("/employer/{jobId}")
    public ResponseEntity<?> updateJob(
            @RequestHeader("X-User-Id") String employerId,
            @PathVariable("jobId") String jobId,
            @RequestBody UpdateJobRequest request
    ) {
        JobResponse response = jobService.updateJob(employerId, jobId, request);
        return ResponseEntity.ok(response);
    }

    // gia hạn tin
    @PutMapping("/employer/{jobId}/renew")
    public ResponseEntity<?> renewJob(
            @RequestHeader("X-User-Id") String employerId,
            @PathVariable("jobId") String jobId,
            @RequestBody RenewJobRequest request
    ) {
        JobResponse response = jobService.renewJob(employerId, jobId, request);
        return ResponseEntity.ok(response);
    }

    // xóa tin
    @DeleteMapping("/employer/{jobId}")
    public ResponseEntity<?> deleteJob(
            @RequestHeader("X-User-Id") String employerId,
            @PathVariable("jobId") String jobId
    ) {
        jobService.deleteJob(employerId, jobId);
        return ResponseEntity.ok(Map.of("message", "Job deleted successfully"));
    }

    // danh sách tin của employer
    @GetMapping("/employer/my-jobs")
    public ResponseEntity<?> getMyJobs(
            @RequestHeader("X-User-Id") String employerId,
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "status", required = false, defaultValue = "all") String status,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        PageResponse<JobResponse> response = jobService.getMyJobs(employerId, search, status, page, size);
        return ResponseEntity.ok(response);
    }

    // thống kê employer
    @GetMapping("/employer/stats")
    public ResponseEntity<?> getMyStats(@RequestHeader("X-User-Id") String employerId) {
        JobStatsResponse stats = jobService.getMyStats(employerId);
        return ResponseEntity.ok(stats);
    }

    // đổi trạng thái employer
    @PutMapping("/employer/{jobId}/status")
    public ResponseEntity<?> employerChangeStatus(
            @RequestHeader("X-User-Id") String employerId,
            @PathVariable("jobId") String jobId,
            @RequestParam("status") String status
    ) {
        JobResponse response = jobService.employerChangeStatus(employerId, jobId, status);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/employer/{jobId}/marketing")
    public ResponseEntity<?> applyMarketingPackage(
            @RequestHeader("X-User-Id") String employerId,
            @PathVariable("jobId") String jobId,
            @RequestBody ApplyMarketingPackageRequest request
    ) {
        JobResponse response = jobService.applyMarketingPackage(employerId, jobId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/employer/{jobId}/marketing")
    public ResponseEntity<?> removeMarketingPackage(
            @RequestHeader("X-User-Id") String employerId,
            @PathVariable("jobId") String jobId
    ) {
        JobResponse response = jobService.removeMarketingPackage(employerId, jobId);
        return ResponseEntity.ok(response);
    }

    // ===== ADMIN =====
    @PutMapping("/admin/{jobId}/status")
    public ResponseEntity<?> adminChangeStatus(
            @RequestHeader("X-Admin-Id") String adminId,
            @PathVariable("jobId") String jobId,
            @RequestParam("status") String status
    ) {
        JobResponse response = jobService.adminChangeStatus(adminId, jobId, status);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/admin/employer-stats")
    public ResponseEntity<?> getEmployerStats(@RequestBody EmployerStatsRequest request) {
        return ResponseEntity.ok(jobService.getEmployerJobStats(request));
    }

    @GetMapping("/admin/monthly-job-stats")
    public ResponseEntity<?> getMonthlyJobStats() {
        return ResponseEntity.ok(jobService.getMonthlyJobStats());
    }

    @GetMapping("/admin/recent-activities")
    public ResponseEntity<?> getRecentActivities(@RequestParam(value = "limit", defaultValue = "10") int limit) {
        return ResponseEntity.ok(jobService.getRecentActivities(limit));
    }

    @GetMapping("/admin/weekly-stats")
    public ResponseEntity<?> getWeeklyNewJobs() {
        return ResponseEntity.ok(jobService.getWeeklyNewJobs());
    }

    @DeleteMapping("/admin/{jobId}")
    public ResponseEntity<?> adminDeleteJob(
            @RequestHeader("X-Admin-Id") String adminId,
            @PathVariable("jobId") String jobId
    ) {
        jobService.adminDeleteJob(adminId, jobId);
        return ResponseEntity.ok(Map.of("message", "Job deleted successfully"));
    }

    // ===== PUBLIC =====

    @GetMapping("/{jobId}")
    public ResponseEntity<?> getJobById(@PathVariable("jobId") String jobId) {
        JobDetailResponse response = jobService.getJobDetail(jobId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{jobId}/applications/increment")
    public ResponseEntity<?> incrementApplications(@PathVariable("jobId") String jobId) {
        jobService.incrementApplications(jobId);
        return ResponseEntity.ok(Map.of("message", "Applications incremented"));
    }

    // search public
    @GetMapping("/search")
    public ResponseEntity<?> searchJobs(
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "industry", required = false) String industry,
            @RequestParam(value = "industryId", required = false) String industryId,
            @RequestParam(value = "jobType", required = false) String jobType,
            @RequestParam(value = "location", required = false) String location,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "experienceMin", required = false) Integer experienceMin,
            @RequestParam(value = "experienceMax", required = false) Integer experienceMax,
            @RequestParam(value = "salaryMin", required = false) Double salaryMin,
            @RequestParam(value = "salaryMax", required = false) Double salaryMax,
            @RequestParam(value = "sortBy", required = false) String sortBy,
            @RequestParam(value = "sortDir", required = false) String sortDir,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        String resolvedKeyword = keyword != null ? keyword : search;
        String resolvedIndustry = industryId != null ? industryId : industry;

        PageResponse<JobCardResponse> response = jobService.searchJobs(
            resolvedKeyword,
            resolvedIndustry,
            jobType,
            location,
            status,
            experienceMin,
            experienceMax,
            salaryMin,
            salaryMax,
            sortBy,
            sortDir,
            page,
            size
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/admin/filter")
    public ResponseEntity<?> getAllAdminJobs( @RequestParam(value = "search", required = false) String search,
                                              @RequestParam(value = "status", required = false) String status,
                                              @RequestParam(value = "page", defaultValue = "1") int page,
                                              @RequestParam(value = "size", defaultValue = "10") int size) {
        PageResponse<JobAdminResponse> response =
                jobService.getAllJobByAdmin(search, status, page, size);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/admin/jobs/{jobId}")
    public ResponseEntity<?> deleteJobByAdmin(
                                              @RequestHeader("adminId") String adminId,
                                              @PathVariable("jobId") String jobId
                                                )
    {
        jobService.adminDeleteJob(adminId, jobId);
        return ResponseEntity.ok("Deleted successfully");
    }


}
