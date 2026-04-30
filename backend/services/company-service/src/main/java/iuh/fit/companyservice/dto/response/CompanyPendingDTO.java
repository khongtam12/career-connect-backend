package iuh.fit.companyservice.dto.response;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CompanyPendingDTO {
    private String id;
    private String name;
    private LocalDateTime createdAt;
    private String requestedBy;
}
