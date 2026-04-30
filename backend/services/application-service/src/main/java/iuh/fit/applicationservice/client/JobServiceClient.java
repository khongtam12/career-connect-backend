package iuh.fit.applicationservice.client;

import iuh.fit.applicationservice.dto.response.JobDetailClientResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "job-service", url = "http://localhost:8082")
public interface JobServiceClient {

    @GetMapping("/api/v1/job/{jobId}")
    JobDetailClientResponse getJobById(@PathVariable("jobId") String jobId);
}