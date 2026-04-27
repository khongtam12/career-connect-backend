package iuh.fit.companyservice.dto.response;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CompanyFullDetailDTO {
    private String id;
    private String name;
    private String logo;
    private String taxCode;
    private String website;
    private String email;
    private String phone;
    private String address;
    private String description;
    private int companySize;
    private int foundedYear;
    private LocalDateTime createdAt;
    
    // Verification Info
    private String submittedTaxCode;
    private String businessLicense;
    private String verificationNote;
    private String verificationStatus;
}
