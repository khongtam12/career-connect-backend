package iuh.fit.userservice.client;

import iuh.fit.userservice.dto.request.SendEmailRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@FeignClient(name = "notification-service", url = "http://localhost:8085")
public interface NotificationClient {

    @PostMapping("/api/v1/notifications/send-email")
    Map<String, String> sendEmail(@RequestBody SendEmailRequest request);
}
