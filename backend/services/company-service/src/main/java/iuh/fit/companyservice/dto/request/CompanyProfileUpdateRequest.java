package iuh.fit.companyservice.dto.request;

import lombok.Data;

@Data
public class CompanyProfileUpdateRequest {

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
