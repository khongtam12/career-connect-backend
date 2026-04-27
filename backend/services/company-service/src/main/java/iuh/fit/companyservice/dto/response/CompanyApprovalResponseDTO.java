package iuh.fit.companyservice.dto.response;

import iuh.fit.companyservice.model.ApprovalStatus;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CompanyApprovalResponseDTO {
    private String companyId;
    private ApprovalStatus approvalStatus;
    private String approvedBy;
    private LocalDateTime approvedAt;
    private String note;
}
