package iuh.fit.applicationservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateJobApplicationRequest {

    @NotBlank(message = "Job ID is required")
    private String jobId;
    @NotBlank(message = "CV ID is required")
    private String cvId;
    private String note;

    public CreateJobApplicationRequest() {
    }

    public CreateJobApplicationRequest(String jobId, String cvId, String note) {
        this.jobId = jobId;
        this.cvId = cvId;
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

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
