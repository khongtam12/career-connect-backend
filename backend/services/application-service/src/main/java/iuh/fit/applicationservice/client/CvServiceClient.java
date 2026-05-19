package iuh.fit.applicationservice.client;

import iuh.fit.applicationservice.dto.response.CvDetailClientResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "cv-service", url = "${CV_SERVICE_URL:http://cv-service:8087}")
public interface CvServiceClient {

    @GetMapping("/api/v1/cvs/{cvId}")
    CvDetailClientResponse getCvById(@PathVariable("cvId") String cvId);
}
