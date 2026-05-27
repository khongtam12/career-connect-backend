package iuh.fit.jobservice.service;

import iuh.fit.jobservice.dto.CompanyMarketingAssignmentDTO;
import iuh.fit.jobservice.dto.JobFilterOptions;
import iuh.fit.jobservice.dto.JobStats;
import iuh.fit.jobservice.dto.request.ApplyMarketingPackageRequest;
import iuh.fit.jobservice.dto.request.CreateJobRequest;
import iuh.fit.jobservice.dto.request.EmployerStatsRequest;
import iuh.fit.jobservice.dto.request.RenewJobRequest;
import iuh.fit.jobservice.dto.request.UpdateJobRequest;
import iuh.fit.jobservice.dto.response.*;

import java.util.List;

public interface JobService {

    // ===== EMPLOYER =====
    JobResponse createJob(String employerId, CreateJobRequest request);

    JobResponse updateJob(String employerId, String jobId, UpdateJobRequest request);

    void deleteJob(String employerId, String jobId);

    PageResponse<JobResponse> getMyJobs(String employerId, String search, String status, int page, int size);

    JobStatsResponse getMyStats(String employerId);

    JobResponse employerChangeStatus(String employerId, String jobId, String newStatus);

    JobResponse renewJob(String employerId, String jobId, RenewJobRequest request);

    JobResponse applyMarketingPackage(String employerId, String jobId, ApplyMarketingPackageRequest request);

    JobResponse removeMarketingPackage(String employerId, String jobId);

    CompanyMarketingAssignmentDTO applyCompanyMarketingPackage(String employerId, ApplyMarketingPackageRequest request);

    void removeCompanyMarketingPackage(String employerId, String assignmentId);

    // ===== ADMIN =====
    JobResponse adminChangeStatus(String adminId, String jobId, String newStatus);

    List<EmployerStatsResponse> getEmployerJobStats(EmployerStatsRequest request);

    MonthlyJobStatsResponse getMonthlyJobStats();

    List<JobActivityDTO> getRecentActivities(int limit);

    List<java.util.Map<String, Object>> getWeeklyNewJobs();

    // ===== SYSTEM =====
    int expireOverdueJobs();

    // ===== PUBLIC =====
    JobDetailResponse getJobDetail(String jobId);

        PageResponse<JobCardResponse> searchJobs(
            String keyword,
            String industryId,
            String jobType,
            String marketingPackageCategory,
            String marketingPackageType,
            String location,
            String status,
            Integer experienceMin,
            Integer experienceMax,
            Double salaryMin,
            Double salaryMax,
            String sortBy,
            String sortDir,
            int page,
            int size
        );

    // ===== EXTRA (giữ từ HEAD) =====
    JobFilterOptions getFilterOptions();

    JobStats getStats();

    void incrementApplications(String jobId);

    PageResponse<JobAdminResponse> getAllJobByAdmin(String search, String status, int page, int size);
    void adminDeleteJob(String adminId, String jobId);
}
