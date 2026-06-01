package iuh.fit.applicationservice.client;

import iuh.fit.applicationservice.dto.request.ApplicationNotificationRequest;
import iuh.fit.applicationservice.dto.request.SendEmailRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@FeignClient(name = "notification-service", path = "/api/v1/notifications")
public interface NotificationServiceClient {

    @PostMapping("/send-email")
    Map<String, String> sendEmail(@RequestBody SendEmailRequest request);

    @PostMapping("/push-candidate-applied")
    Map<String, String> pushCandidateApplied(@RequestBody ApplicationNotificationRequest request);
}
