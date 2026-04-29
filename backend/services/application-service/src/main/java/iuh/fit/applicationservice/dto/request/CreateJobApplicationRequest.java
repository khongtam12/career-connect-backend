package iuh.fit.applicationservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter

public class CreateJobApplicationRequest {

    @NotBlank(message = "Job ID is required")
    private String jobId;
    @NotBlank(message = "CV ID is required")
    private String cvId;
    private String companyId;
    private String industryId;
    private String url;
    private String note;

    public CreateJobApplicationRequest() {
    }

    public CreateJobApplicationRequest(String jobId, String cvId,String companyId,String industryId,String url, String note) {
        this.jobId = jobId;
        this.cvId = cvId;
        this.companyId = companyId;
        this.industryId = industryId;
        this.url = url;
        this.note = note;
    }

    public @NotBlank(message = "Job ID is required") String getJobId() {
        return jobId;
    }

    public void setJobId(@NotBlank(message = "Job ID is required") String jobId) {
        this.jobId = jobId;
    }

    public @NotBlank(message = "CV ID is required") String getCvId() {
        return cvId;
    }

    public void setCvId(@NotBlank(message = "CV ID is required") String cvId) {
        this.cvId = cvId;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public String getIndustryId() {
        return industryId;
    }

    public void setIndustryId(String industryId) {
        this.industryId = industryId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
