package iuh.fit.userservice.dto.response;

public class EmployerCompanyResponse {
    private String employerId;
    private String companyId;

    public EmployerCompanyResponse() {
    }

    public EmployerCompanyResponse(String employerId, String companyId) {
        this.employerId = employerId;
        this.companyId = companyId;
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
