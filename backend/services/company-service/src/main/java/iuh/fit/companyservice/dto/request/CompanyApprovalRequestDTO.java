package iuh.fit.companyservice.dto.request;

import iuh.fit.companyservice.model.StatusVerification;
import lombok.Data;

@Data
public class CompanyApprovalRequestDTO {
    private String companyId;
    private StatusVerification action; // APPROVED or REJECTED
    private String note;
}
