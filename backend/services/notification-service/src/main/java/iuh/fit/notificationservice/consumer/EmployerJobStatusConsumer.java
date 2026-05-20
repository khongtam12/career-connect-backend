package iuh.fit.notificationservice.consumer;

import iuh.fit.notificationservice.event.EmployerJobStatusChangedEvent;
import iuh.fit.notificationservice.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmployerJobStatusConsumer {

    private final NotificationService notificationService;

    @KafkaListener(topics = "job-status-changed")
    public void handleEmployerJobStatusChanged(EmployerJobStatusChangedEvent event) {
        log.info("Received EmployerJobStatusChangedEvent for job {} with status {}", event.getJobId(), event.getStatus());
        notificationService.sendEmployerJobStatusNotification(event);
    }
}
