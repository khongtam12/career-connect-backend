package iuh.fit.companyservice.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class VerifyCompanyDTO {
    private String companyId;
    private String submittedTaxCode;
    private String businessLicense;
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
