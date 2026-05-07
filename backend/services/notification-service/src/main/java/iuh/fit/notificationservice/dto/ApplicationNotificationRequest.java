package iuh.fit.notificationservice.dto;

import lombok.Data;


public class ApplicationNotificationRequest {
    private String companyId;
    private String jobId;
    private String candidateId;
    private String candidateName;
    private String jobTitle;
    private String message;

    public ApplicationNotificationRequest() {
    }

    public ApplicationNotificationRequest(String companyId, String jobId, String candidateId, String candidateName, String jobTitle, String message) {
        this.companyId = companyId;
        this.jobId = jobId;
        this.candidateId = candidateId;
        this.candidateName = candidateName;
        this.jobTitle = jobTitle;
        this.message = message;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getCandidateId() {
        return candidateId;
    }

    public void setCandidateId(String candidateId) {
        this.candidateId = candidateId;
    }

    public String getCandidateName() {
        return candidateName;
    }

    public void setCandidateName(String candidateName) {
        this.candidateName = candidateName;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
