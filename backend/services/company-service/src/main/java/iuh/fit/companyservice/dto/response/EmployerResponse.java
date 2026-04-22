package iuh.fit.companyservice.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmployerResponse {
    private String employerId;
    private String companyId;

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public String getEmployerId() {
        return employerId;
    }

    public void setEmployerId(String employerId) {
        this.employerId = employerId;
    }
}
