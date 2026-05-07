package iuh.fit.notificationservice.controller;

import iuh.fit.notificationservice.dto.ApplicationNotificationRequest;
import iuh.fit.notificationservice.dto.SendEmailRequest;
import iuh.fit.notificationservice.model.Notification;
import iuh.fit.notificationservice.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
    @PostMapping("/push-candidate-applied")
    public ResponseEntity<Map<String, String>> pushCandidateApplied(@RequestBody ApplicationNotificationRequest request) {
        notificationService.sendCandidateAppliedNotification(request);
        return ResponseEntity.ok(Map.of("message", "Notification pushed successfully"));
    }
    @GetMapping("/company/{companyId}")
    public ResponseEntity<List<Notification>> getNotificationsByCompanyId(@PathVariable String companyId) {
        return ResponseEntity.ok(notificationService.getNotificationsByCompanyId(companyId));
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable String id) {
        notificationService.markAsRead(id);
        return ResponseEntity.ok().build();
    }
}
