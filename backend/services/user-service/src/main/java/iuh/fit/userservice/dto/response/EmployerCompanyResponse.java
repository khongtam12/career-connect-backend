package iuh.fit.userservice.dto.response;

public class EmployerCompanyResponse {
    private String employerId;
    private String companyId;
    private String email;

    public EmployerCompanyResponse() {
    }

    public EmployerCompanyResponse(String employerId, String companyId, String email) {
        this.employerId = employerId;
        this.companyId = companyId;
        this.email = email;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
