package iuh.fit.jobservice.service;

import iuh.fit.jobservice.dto.JobFilterOptions;
import iuh.fit.jobservice.dto.JobStats;
import iuh.fit.jobservice.dto.request.ApplyMarketingPackageRequest;
import iuh.fit.jobservice.dto.request.CreateJobRequest;
import iuh.fit.jobservice.dto.request.UpdateJobRequest;
import iuh.fit.jobservice.dto.response.*;

public interface JobService {

    // ===== EMPLOYER =====
    JobResponse createJob(String employerId, CreateJobRequest request);

    JobResponse updateJob(String employerId, String jobId, UpdateJobRequest request);

    void deleteJob(String employerId, String jobId);

    PageResponse<JobResponse> getMyJobs(String employerId, String search, String status, int page, int size);

    JobStatsResponse getMyStats(String employerId);

    JobResponse employerChangeStatus(String employerId, String jobId, String newStatus);

    JobResponse applyMarketingPackage(String employerId, String jobId, ApplyMarketingPackageRequest request);

    JobResponse removeMarketingPackage(String employerId, String jobId);

    // ===== ADMIN =====
    JobResponse adminChangeStatus(String adminId, String jobId, String newStatus);

    // ===== SYSTEM =====
    int expireOverdueJobs();

    // ===== PUBLIC =====
    JobDetailResponse getJobDetail(String jobId);

        PageResponse<JobResponse> searchJobs(
            String keyword,
            String industryId,
            String jobType,
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
