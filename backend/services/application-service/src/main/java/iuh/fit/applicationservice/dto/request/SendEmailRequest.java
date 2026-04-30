package iuh.fit.applicationservice.dto.request;

public class SendEmailRequest {
    private String to;
    private String candidateName;
    private String jobName;
    private String type;
    private String interviewDate;
    private String interviewTime;
    private String interviewLocation;
    private String note;
    private String rejectionReason;

    public SendEmailRequest() {}

    public String getTo() { return to; }
    public void setTo(String to) { this.to = to; }

    public String getCandidateName() { return candidateName; }
    public void setCandidateName(String candidateName) { this.candidateName = candidateName; }

    public String getJobName() { return jobName; }
    public void setJobName(String jobName) { this.jobName = jobName; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getInterviewDate() { return interviewDate; }
    public void setInterviewDate(String interviewDate) { this.interviewDate = interviewDate; }

    public String getInterviewTime() { return interviewTime; }
    public void setInterviewTime(String interviewTime) { this.interviewTime = interviewTime; }

    public String getInterviewLocation() { return interviewLocation; }
    public void setInterviewLocation(String interviewLocation) { this.interviewLocation = interviewLocation; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public String getRejectionReason() { return rejectionReason; }
    public void setRejectionReason(String rejectionReason) { this.rejectionReason = rejectionReason; }
}
