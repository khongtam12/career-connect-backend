package iuh.fit.notificationservice.service;

import iuh.fit.notificationservice.dto.ApplicationNotificationRequest;
import iuh.fit.notificationservice.dto.SendEmailRequest;
import iuh.fit.notificationservice.model.Notification;
import iuh.fit.notificationservice.model.NotificationType;
import iuh.fit.notificationservice.model.UserType;
import iuh.fit.notificationservice.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final EmailService emailService;
    private final SimpMessagingTemplate messagingTemplate;

    @Async
    public void sendApplicationEmail(SendEmailRequest request) {
        try {
            switch (request.getType()) {
                case "INTERVIEW_SCHEDULE" -> emailService.sendInterviewScheduleEmail(
                        request.getTo(),
                        request.getCandidateName(),
                        request.getJobName(),
                        request.getInterviewDate(),
                        request.getInterviewTime(),
                        request.getInterviewLocation(),
                        request.getNote()
                );
                case "INTERVIEW_CANCEL" -> emailService.sendCancelInterviewEmail(
                        request.getTo(),
                        request.getCandidateName(),
                        request.getJobName()
                );
                case "ACCEPTED" -> emailService.sendAcceptEmail(
                        request.getTo(),
                        request.getCandidateName(),
                        request.getJobName()
                );
                case "REJECTED" -> emailService.sendRejectEmail(
                        request.getTo(),
                        request.getCandidateName(),
                        request.getJobName(),
                        request.getRejectionReason()
                );
                case "OTP" -> emailService.sendOtpEmail(
                        request.getTo(),
                        request.getOtp()
                );
                default -> log.warn("Unknown email type: {}", request.getType());
            }

            try {
                // Luu notification vao MongoDB
                Notification notification = Notification.builder()
                        .title(buildTitle(request.getType(), request.getJobName()))
                        .message(buildMessage(request))
                        .type(mapNotificationType(request.getType()))
                        .userType(UserType.CANDIDATE)
                        .isRead(false)
                        .createdAt(LocalDateTime.now())
                        .build();

                notificationRepository.save(notification);
                log.info("Email sent and notification saved for: {}", request.getTo());
            } catch (Exception mongoEx) {
                log.error("Email sent, but failed to save notification to MongoDB: {}", mongoEx.getMessage());
            }

        } catch (Exception e) {
            log.error("Failed to send email to {}: {}", request.getTo(), e.getMessage());
        }
    }

    private String buildTitle(String type, String jobName) {
        return switch (type) {
            case "INTERVIEW_SCHEDULE" -> "Lịch phỏng vấn - " + jobName;
            case "INTERVIEW_CANCEL" -> "Hủy phỏng vấn - " + jobName;
            case "ACCEPTED" -> "Chấp nhận ứng tuyển - " + jobName;
            case "REJECTED" -> "Từ chối ứng tuyển - " + jobName;
            default -> "Thông báo - " + jobName;
        };
    }

    private String buildMessage(SendEmailRequest request) {
        return switch (request.getType()) {
            case "INTERVIEW_SCHEDULE" -> String.format("Bạn được mời phỏng vấn vị trí %s vào %s lúc %s tại %s",
                    request.getJobName(), request.getInterviewDate(), request.getInterviewTime(), request.getInterviewLocation());
            case "INTERVIEW_CANCEL" -> "Lịch phỏng vấn vị trí " + request.getJobName() + " đã bị hủy";
            case "ACCEPTED" -> "Chúc mừng! Bạn đã được chấp nhận cho vị trí " + request.getJobName();
            case "REJECTED" -> "Hồ sơ ứng tuyển vị trí " + request.getJobName() + " đã bị từ chối";
            default -> "Thông báo về vị trí " + request.getJobName();
        };
    }

    private NotificationType mapNotificationType(String type) {
        return switch (type) {
            case "INTERVIEW_SCHEDULE", "INTERVIEW_CANCEL" -> NotificationType.INTERVIEW_INVITE;
            case "ACCEPTED", "REJECTED" -> NotificationType.APPLICATION_STATUS;
            default -> NotificationType.SYSTEM;
        };
    }

    public void sendCandidateAppliedNotification(ApplicationNotificationRequest request) {
        Notification notification = Notification.builder()
                .userId(request.getCompanyId()) // Lưu companyId vào cột userId
                .title("Ứng viên mới: " + request.getJobTitle())
                .message(request.getMessage() != null ? request.getMessage() : "Có ứng viên vừa ứng tuyển")
                .type(NotificationType.SYSTEM)
                .userType(UserType.EMPLOYER)
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .build();
        notification = notificationRepository.save(notification);
        // Phát sự kiện qua Websocket
        messagingTemplate.convertAndSend("/topic/company/" + request.getCompanyId() + "/notifications", notification);
    }
    public List<Notification> getNotificationsByCompanyId(String companyId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(companyId);
    }
    public void markAsRead(String id) {
        notificationRepository.findById(id).ifPresent(noti -> {
            noti.setRead(true);
            notificationRepository.save(noti);
        });
    }
}
