package iuh.fit.jobservice.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplyMarketingPackageRequest {
    private String entitlementId;
    private String placement;
}
