package iuh.fit.companyservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompanyProfileResponse {
    private String companyId;
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
    private String statusCompany;
    private LocalDateTime createdAt;
}
