package iuh.fit.jobservice.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CompanyDTO {
    private String companyId;
    private String name;
    private String logo;
    private String website;
    private String email;
    private String phone;
    private String address;
    private String description;
    private int companySize;
    private int foundedYear;
}
