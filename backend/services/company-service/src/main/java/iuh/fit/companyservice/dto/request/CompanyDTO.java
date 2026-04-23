package iuh.fit.companyservice.dto.request;

import iuh.fit.companyservice.model.Company;
import iuh.fit.companyservice.model.StatusCompany;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
@Getter
@Setter
@NoArgsConstructor
public class CompanyDTO {
    private String employerId;
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

    public CompanyDTO(String name, String logo, String taxCode, String website, String email, String phone, String address, String description, int companySize, int foundedYear) {
        this.name = name;
        this.logo = logo;
        this.taxCode = taxCode;
        this.website = website;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.description = description;
        this.companySize = companySize;
        this.foundedYear = foundedYear;
    }

    public void setEmployerId(String employerId) {
        this.employerId = employerId;
    }

    public String getEmployerId() {
        return employerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getTaxCode() {
        return taxCode;
    }

    public void setTaxCode(String taxCode) {
        this.taxCode = taxCode;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getCompanySize() {
        return companySize;
    }

    public void setCompanySize(int companySize) {
        this.companySize = companySize;
    }

    public int getFoundedYear() {
        return foundedYear;
    }

    public void setFoundedYear(int foundedYear) {
        this.foundedYear = foundedYear;
    }
}
