package iuh.fit.userservice.dto.response;

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
    private String status;
    private LocalDateTime verifiedAt;
}
