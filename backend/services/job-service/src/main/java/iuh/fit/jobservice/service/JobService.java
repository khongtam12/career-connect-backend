package iuh.fit.jobservice.service;

import iuh.fit.jobservice.dto.request.CreateJobRequest;
import iuh.fit.jobservice.dto.request.UpdateJobRequest;
import iuh.fit.jobservice.dto.response.JobResponse;
import iuh.fit.jobservice.dto.response.JobStatsResponse;
import iuh.fit.jobservice.dto.response.PageResponse;

public interface JobService {

    // ── EMPLOYER ──
    JobResponse createJob(String employerId, CreateJobRequest request);

    JobResponse updateJob(String employerId, String jobId, UpdateJobRequest request);

    void deleteJob(String employerId, String jobId);

    PageResponse<JobResponse> getMyJobs(String employerId, String search, String status, int page, int size);

    JobStatsResponse getMyStats(String employerId);

    JobResponse pushToTop(String employerId, String jobId);

    /** Employer chuyển trạng thái: DRAFT→PENDING, ACTIVE→PAUSED/CLOSED, PAUSED→ACTIVE, REJECTED→PENDING */
    JobResponse employerChangeStatus(String employerId, String jobId, String newStatus);

    // ── ADMIN ──
    /** Admin duyệt/từ chối: PENDING→ACTIVE | PENDING→REJECTED */
    JobResponse adminChangeStatus(String adminId, String jobId, String newStatus);

    // ── SYSTEM ──
    /** Tác vụ định kỳ: ACTIVE → EXPIRED khi hết deadline */
    int expireOverdueJobs();

    // ── PUBLIC ──
    JobResponse getJobById(String jobId);

    PageResponse<JobResponse> searchJobs(String search, String industry, String jobType, String location, int page, int size);
}
