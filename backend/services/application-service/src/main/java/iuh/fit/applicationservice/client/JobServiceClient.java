package iuh.fit.applicationservice.client;

import iuh.fit.applicationservice.dto.response.JobDetailClientResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "job-service")
public interface JobServiceClient {

    @GetMapping("/api/v1/job/{jobId}")
    JobDetailClientResponse getJobById(@PathVariable("jobId") String jobId);

    @PostMapping("/api/v1/job/{jobId}/applications/increment")
    void incrementApplications(@PathVariable("jobId") String jobId);
}