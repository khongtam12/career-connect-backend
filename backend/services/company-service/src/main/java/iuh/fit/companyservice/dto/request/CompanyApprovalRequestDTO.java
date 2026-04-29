package iuh.fit.companyservice.dto.request;

import iuh.fit.companyservice.model.ApprovalStatus;
import lombok.Data;

@Data
public class CompanyApprovalRequestDTO {
    private String companyId;
    private ApprovalStatus action; // APPROVED or REJECTED
    private String note;
}
