package iuh.fit.notificationservice.controller;

import iuh.fit.notificationservice.dto.SendEmailRequest;
import iuh.fit.notificationservice.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public String test() {
        return "Notification service";
    }

    @PostMapping("/send-email")
    public ResponseEntity<Map<String, String>> sendEmail(@RequestBody SendEmailRequest request) {
        notificationService.sendApplicationEmail(request);
        return ResponseEntity.ok(Map.of("message", "Email sent successfully"));
    }
}
