package iuh.fit.applicationservice.dto.request;

import lombok.Data;

@Data
public class ApplicationNotificationRequest {
    private String companyId;
    private String jobId;
    private String candidateId;
    private String candidateName;
    private String jobTitle;
    private String message;
}
