package iuh.fit.userservice.dto.response;

public class CandidateSummaryResponse {
    private String candidateId;
    private String fullName;
    private String experienceYear;
    private String dateOfBirth;
    private String email;

    public CandidateSummaryResponse() {
    }

    public CandidateSummaryResponse(String candidateId, String fullName, String experienceYear, String dateOfBirth, String email) {
        this.candidateId = candidateId;
        this.fullName = fullName;
        this.experienceYear = experienceYear;
        this.dateOfBirth = dateOfBirth;
        this.email = email;
    }

    public String getCandidateId() {
        return candidateId;
    }

    public void setCandidateId(String candidateId) {
        this.candidateId = candidateId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getExperienceYear() {
        return experienceYear;
    }

    public void setExperienceYear(String experienceYear) {
        this.experienceYear = experienceYear;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
