package iuh.fit.notificationservice.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SendEmailRequest {
    private String to;
    private String candidateName;
    private String jobName;
    private String type; // INTERVIEW_SCHEDULE, INTERVIEW_CANCEL, ACCEPTED, REJECTED

    // Interview fields
    private String interviewDate;
    private String interviewTime;
    private String interviewLocation;
    private String note;

    // Rejection fields
    private String rejectionReason;
}
