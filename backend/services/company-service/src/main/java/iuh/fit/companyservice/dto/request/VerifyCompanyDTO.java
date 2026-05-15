package iuh.fit.companyservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class VerifyCompanyDTO {
    @NotBlank(message = "Company ID is required")
    private String companyId;

    @NotBlank(message = "Submitted tax code is required")
    @Size(max = 30, message = "Submitted tax code must not exceed 30 characters")
    private String submittedTaxCode;

    @NotBlank(message = "Business license is required")
    private String businessLicense;

    @Size(max = 500, message = "Note must not exceed 500 characters")
    private String note;

    public VerifyCompanyDTO(String companyId, String submittedTaxCode, String businessLicense, String note) {
        this.companyId = companyId;
        this.submittedTaxCode = submittedTaxCode;
        this.businessLicense = businessLicense;
        this.note = note;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public String getSubmittedTaxCode() {
        return submittedTaxCode;
    }

    public void setSubmittedTaxCode(String submittedTaxCode) {
        this.submittedTaxCode = submittedTaxCode;
    }

    public String getBusinessLicense() {
        return businessLicense;
    }

    public void setBusinessLicense(String businessLicense) {
        this.businessLicense = businessLicense;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
