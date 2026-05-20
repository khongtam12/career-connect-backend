package iuh.fit.notificationservice.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployerJobStatusChangedEvent {
    private String companyId;
    private String companyName;
    private String employerEmail;
    private String jobId;
    private String jobTitle;
    private String status;
}
