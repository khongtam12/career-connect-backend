package iuh.fit.jobservice.controller;

import iuh.fit.jobservice.dto.JobFilterOptions;
import iuh.fit.jobservice.dto.JobStats;
import iuh.fit.jobservice.dto.request.CreateJobRequest;
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
        try {
            JobResponse response = jobService.createJob(employerId, request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            log.error("[createJob] employerId={} error={}", employerId, e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName()));
        }
    }

    // cập nhật tin
    @PutMapping("/employer/{jobId}")
    public ResponseEntity<?> updateJob(
            @RequestHeader("X-User-Id") String employerId,
            @PathVariable String jobId,
            @RequestBody UpdateJobRequest request
    ) {
        try {
            JobResponse response = jobService.updateJob(employerId, jobId, request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not authorized"))
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));
            if (e.getMessage().contains("not found"))
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // xóa tin
    @DeleteMapping("/employer/{jobId}")
    public ResponseEntity<?> deleteJob(
            @RequestHeader("X-User-Id") String employerId,
            @PathVariable String jobId
    ) {
        try {
            jobService.deleteJob(employerId, jobId);
            return ResponseEntity.ok(Map.of("message", "Job deleted successfully"));
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not authorized"))
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));
            if (e.getMessage().contains("not found"))
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // danh sách tin của employer
    @GetMapping("/employer/my-jobs")
    public ResponseEntity<?> getMyJobs(
            @RequestHeader("X-User-Id") String employerId,
            @RequestParam(required = false) String search,
            @RequestParam(required = false, defaultValue = "all") String status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
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

    // đẩy TOP
    @PutMapping("/employer/{jobId}/push-top")
    public ResponseEntity<?> pushToTop(
            @RequestHeader("X-User-Id") String employerId,
            @PathVariable String jobId
    ) {
        try {
            JobResponse response = jobService.pushToTop(employerId, jobId);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not authorized"))
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // đổi trạng thái employer
    @PutMapping("/employer/{jobId}/status")
    public ResponseEntity<?> employerChangeStatus(
            @RequestHeader("X-User-Id") String employerId,
            @PathVariable String jobId,
            @RequestParam String status
    ) {
        try {
            JobResponse response = jobService.employerChangeStatus(employerId, jobId, status);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not authorized"))
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ===== ADMIN =====
    @PutMapping("/admin/{jobId}/status")
    public ResponseEntity<?> adminChangeStatus(
            @RequestHeader("X-Admin-Id") String adminId,
            @PathVariable String jobId,
            @RequestParam String status
    ) {
        try {
            JobResponse response = jobService.adminChangeStatus(adminId, jobId, status);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/admin/{jobId}")
    public ResponseEntity<?> adminDeleteJob(
            @RequestHeader("X-Admin-Id") String adminId,
            @PathVariable String jobId
    ) {
        try {
            jobService.adminDeleteJob(adminId, jobId);
            return ResponseEntity.ok(Map.of("message", "Job deleted successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ===== PUBLIC =====

    // xem chi tiết
    @GetMapping("/{jobId}")
    public ResponseEntity<?> getJobById(@PathVariable String jobId) {
        try {
            JobDetailResponse response = jobService.getJobDetail(jobId);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // search public
    @GetMapping("/search")
    public ResponseEntity<?> searchJobs(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String industry,
            @RequestParam(required = false) String industryId,
            @RequestParam(required = false) String jobType,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Integer experienceMin,
            @RequestParam(required = false) Integer experienceMax,
            @RequestParam(required = false) Double salaryMin,
            @RequestParam(required = false) Double salaryMax,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortDir,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        String resolvedKeyword = keyword != null ? keyword : search;
        String resolvedIndustry = industryId != null ? industryId : industry;

        PageResponse<JobResponse> response = jobService.searchJobs(
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
    public ResponseEntity<?> getAllAdminJobs( @RequestParam(required = false) String search,
                                              @RequestParam(required = false) String status,
                                              @RequestParam(defaultValue = "1") int page,
                                              @RequestParam(defaultValue = "10") int size) {
        PageResponse<JobAdminResponse> response =
                jobService.getAllJobByAdmin(search, status, page, size);

        return ResponseEntity.ok(response);
    }
    @DeleteMapping("/admin/jobs/{jobId}")
    public ResponseEntity<?> deleteJobByAdmin(
                                              @RequestHeader("adminId") String adminId,
                                              @PathVariable String jobId
                                                )
    {
        jobService.adminDeleteJob(adminId, jobId);
        return ResponseEntity.ok("Deleted successfully");
    }


}