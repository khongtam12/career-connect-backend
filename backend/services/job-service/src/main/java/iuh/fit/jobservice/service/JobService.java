package iuh.fit.jobservice.service;

import iuh.fit.jobservice.dto.JobFilterOptions;
import iuh.fit.jobservice.dto.JobStats;
import iuh.fit.jobservice.dto.request.CreateJobRequest;
import iuh.fit.jobservice.dto.request.UpdateJobRequest;
import iuh.fit.jobservice.dto.response.JobDetailResponse;
import iuh.fit.jobservice.dto.response.JobResponse;
import iuh.fit.jobservice.dto.response.JobStatsResponse;
import iuh.fit.jobservice.dto.response.PageResponse;

public interface JobService {

    // ===== EMPLOYER =====
    JobResponse createJob(String employerId, CreateJobRequest request);

    JobResponse updateJob(String employerId, String jobId, UpdateJobRequest request);

    void deleteJob(String employerId, String jobId);

    PageResponse<JobResponse> getMyJobs(String employerId, String search, String status, int page, int size);

    JobStatsResponse getMyStats(String employerId);

    JobResponse pushToTop(String employerId, String jobId);

    JobResponse employerChangeStatus(String employerId, String jobId, String newStatus);

    // ===== ADMIN =====
    JobResponse adminChangeStatus(String adminId, String jobId, String newStatus);

    // ===== SYSTEM =====
    int expireOverdueJobs();

    // ===== PUBLIC =====
    JobDetailResponse getJobDetail(String jobId);

    PageResponse<JobResponse> searchJobs(String search, String industry, String jobType, String location, int page, int size);

    // ===== EXTRA (giữ từ HEAD) =====
    JobFilterOptions getFilterOptions();

    JobStats getStats();
}