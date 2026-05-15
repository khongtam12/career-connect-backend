package iuh.fit.companyservice.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanyMarketingAssignmentRequest {
    private String companyId;
    private String targetId;
    private String targetScope;
    private String placement;
}
