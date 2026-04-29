package iuh.fit.applicationservice.dto.request;

public class ScheduleInterviewRequest {
    private String interviewDate;
    private String interviewTime;
    private String location;
    private String note;

    public ScheduleInterviewRequest() {
    }

    public ScheduleInterviewRequest(String interviewDate, String interviewTime, String location, String note) {
        this.interviewDate = interviewDate;
        this.interviewTime = interviewTime;
        this.location = location;
        this.note = note;
    }

    public String getInterviewDate() {
        return interviewDate;
    }

    public void setInterviewDate(String interviewDate) {
        this.interviewDate = interviewDate;
    }

    public String getInterviewTime() {
        return interviewTime;
    }

    public void setInterviewTime(String interviewTime) {
        this.interviewTime = interviewTime;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
