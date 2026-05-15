package iuh.fit.companyservice.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.AssertTrue;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Year;
@Getter
@Setter
@NoArgsConstructor
public class CompanyDTO {
    private String companyId;

    @NotBlank(message = "Employer ID is required")
    private String employerId;

    @NotBlank(message = "Company name is required")
    @Size(max = 255, message = "Company name must not exceed 255 characters")
    private String name;

    @Size(max = 1000, message = "Logo URL must not exceed 1000 characters")
    private String logo;

    @NotBlank(message = "Tax code is required")
    @Pattern(regexp = "^\\d{10,13}$", message = "Tax code must contain 10 to 13 digits")
    private String taxCode;

    @Pattern(
            regexp = "^(https?://).+|^$",
            message = "Website must start with http:// or https://"
    )
    private String website;

    @Email(message = "Email is invalid")
    @Size(max = 255, message = "Email must not exceed 255 characters")
    private String email;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^(\\+?\\d{10,15})$", message = "Phone number must contain 10 to 15 digits")
    private String phone;

    @Size(max = 500, message = "Address must not exceed 500 characters")
    private String address;

    @Size(max = 2000, message = "Description must not exceed 2000 characters")
    private String description;

    @Positive(message = "Company size must be greater than 0")
    private Integer companySize;

    @Min(value = 1800, message = "Founded year must be greater than or equal to 1800")
    private Integer foundedYear;

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

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
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

    public Integer getCompanySize() {
        return companySize;
    }

    public void setCompanySize(Integer companySize) {
        this.companySize = companySize;
    }

    public Integer getFoundedYear() {
        return foundedYear;
    }

    public void setFoundedYear(Integer foundedYear) {
        this.foundedYear = foundedYear;
    }

    @AssertTrue(message = "Founded year must not be greater than the current year")
    public boolean isFoundedYearValid() {
        return foundedYear == null || foundedYear <= Year.now().getValue();
    }
}
