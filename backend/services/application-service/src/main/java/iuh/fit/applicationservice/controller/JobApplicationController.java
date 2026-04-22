package iuh.fit.applicationservice.controller;

import iuh.fit.applicationservice.dto.request.CreateJobApplicationRequest;
import iuh.fit.applicationservice.dto.response.ApiResponse;
import iuh.fit.applicationservice.dto.response.JobApplicationResponse;
import iuh.fit.applicationservice.service.JobApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/apply")

public class JobApplicationController {
    private final JobApplicationService jobApplicationService;

    public JobApplicationController(JobApplicationService jobApplicationService) {
        this.jobApplicationService = jobApplicationService;
    }

    @PostMapping
    public ApiResponse<JobApplicationResponse> apply(
            @RequestBody CreateJobApplicationRequest request,
            @RequestHeader("X-User-Id") String candidateId) {

        JobApplicationResponse res =
                jobApplicationService.applyForJob(candidateId, request);

        return new ApiResponse<>(200, "Apply success", res);
    }

}

