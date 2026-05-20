package iuh.fit.companyservice.dto.response;

import iuh.fit.companyservice.model.StatusVerification;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanyApprovalActivityDTO {
    private String companyId;
    private String companyName;
    private StatusVerification status;
    private LocalDateTime verifiedAt;
}
