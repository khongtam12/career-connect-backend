package iuh.fit.applicationservice.dto.response;

public class EmployerCompanyClientResponse {
    private String employerId;
    private String companyId;

    public EmployerCompanyClientResponse() {
    }

    public String getEmployerId() {
        return employerId;
    }

    public void setEmployerId(String employerId) {
        this.employerId = employerId;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }
}