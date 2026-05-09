package iuh.fit.applicationservice.controller;

import iuh.fit.applicationservice.dto.request.CreateJobApplicationRequest;
import iuh.fit.applicationservice.dto.request.ScheduleInterviewRequest;
import iuh.fit.applicationservice.dto.request.UpdateStatusRequest;
import iuh.fit.applicationservice.dto.response.ApiResponse;
import iuh.fit.applicationservice.dto.response.CandidateApplicationResponse;
import iuh.fit.applicationservice.dto.response.FileUploadResponse;
import iuh.fit.applicationservice.dto.response.JobApplicationResponse;
import iuh.fit.applicationservice.service.ApplicationFileStorageService;
import iuh.fit.applicationservice.service.JobApplicationService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/apply")

public class JobApplicationController {
    private final JobApplicationService jobApplicationService;
    private final ApplicationFileStorageService applicationFileStorageService;

    public JobApplicationController(JobApplicationService jobApplicationService,
                                    ApplicationFileStorageService applicationFileStorageService) {
        this.jobApplicationService = jobApplicationService;
        this.applicationFileStorageService = applicationFileStorageService;
    }

    @PostMapping
    public ApiResponse<JobApplicationResponse> apply(
            @RequestBody CreateJobApplicationRequest request,
            @RequestHeader("X-User-Id") String candidateId) {

        JobApplicationResponse res =
                jobApplicationService.applyForJob(candidateId, request);

        return new ApiResponse<>(200, "Apply success", res);
    }


    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<FileUploadResponse> uploadFile(@RequestParam("file") MultipartFile file) {
        FileUploadResponse res = applicationFileStorageService.uploadApplicationFile(file);
        return new ApiResponse<>(200, "Upload success", res);
    }

    @GetMapping("/my-applications")
    public ApiResponse<List<JobApplicationResponse>> getMyApplications(
            @RequestHeader("X-User-Id") String candidateId) {
        List<JobApplicationResponse> res = jobApplicationService.getByCandidate(candidateId);
        return new ApiResponse<>(200, "Get applications success", res);
    }




    @GetMapping("/employer/candidates")
    public ApiResponse<List<CandidateApplicationResponse>> getCandidatesForEmployer(
            @RequestHeader("X-User-Id") String employerId) {

        List<CandidateApplicationResponse> res =
                jobApplicationService.getCandidatesByEmployer(employerId);

        return new ApiResponse<>(200, "Get candidate applications success", res);
    }

    // len lich phong van
    @PutMapping("/{applicationId}/schedule-interview")
    public ApiResponse<JobApplicationResponse> scheduleInterview(
            @PathVariable("applicationId") String applicationId,
            @RequestBody ScheduleInterviewRequest request) {

        JobApplicationResponse res =
                jobApplicationService.scheduleInterview(applicationId, request);

        return new ApiResponse<>(200, "Schedule interview success", res);
    }

    // huy phong van
    @PutMapping("/{applicationId}/cancel-interview")
    public ApiResponse<JobApplicationResponse> cancelInterview(
            @PathVariable("applicationId") String applicationId) {

        JobApplicationResponse res =
                jobApplicationService.cancelInterview(applicationId);

        return new ApiResponse<>(200, "Cancel interview success", res);
    }

    // cap nhat trang thai
    @PutMapping("/{applicationId}/status")
    public ApiResponse<JobApplicationResponse> updateStatus(
            @PathVariable("applicationId") String applicationId,
            @RequestBody UpdateStatusRequest request) {

        JobApplicationResponse res =
                jobApplicationService.updateStatus(applicationId, request);

        return new ApiResponse<>(200, "Update status success", res);
    }

    // tu choi ho so
    @PutMapping("/{applicationId}/reject")
    public ApiResponse<JobApplicationResponse> rejectApplication(
            @PathVariable("applicationId") String applicationId,
            @RequestBody UpdateStatusRequest request) {

        JobApplicationResponse res =
                jobApplicationService.rejectApplication(applicationId, request.getReason());

        return new ApiResponse<>(200, "Reject application success", res);
    }
}
