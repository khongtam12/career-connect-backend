package iuh.fit.userservice.dto.request;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmployerCompanyRequest {
    private String companyId;
    private String employerId;

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