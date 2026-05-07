package iuh.fit.companyservice.dto.response;

import iuh.fit.companyservice.model.StatusVerification;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CompanyApprovalResponseDTO {
    private String companyId;
    private StatusVerification verificationStatus;
    private String verifiedBy;
    private LocalDateTime verifiedAt;
    private String note;
}
