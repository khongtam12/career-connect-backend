package iuh.fit.applicationservice.dto.response;

import iuh.fit.applicationservice.model.StatusApply;

import java.time.LocalDateTime;

public class CandidateApplicationResponse {
    private String applicationId;
    private String jobId;
    private String jobName;
    private String candidateId;
    private String fullName;
    private String experienceYear;
    private String dateOfBirth;
    private String cvId;
    private String companyId;
    private String industryId;
    private String industryName;
    private String url;
    private String note;
    private StatusApply status;
    private LocalDateTime appliedAt;
    private LocalDateTime updatedAt;
    private String interviewDate;
    private String interviewTime;
    private String interviewLocation;
    private String rejectionReason;
    private CandidateMatchInsight matchInsight;

    public CandidateApplicationResponse() {
    }

    public CandidateApplicationResponse(String applicationId, String jobId,String jobName,String candidateId, String fullName, String experienceYear, String dateOfBirth, String cvId, String companyId, String industryId, String industryName, String url, String note, StatusApply status, LocalDateTime appliedAt, LocalDateTime updatedAt) {
        this.applicationId = applicationId;
        this.jobId = jobId;
        this.jobName = jobName;
        this.candidateId = candidateId;
        this.fullName = fullName;
        this.experienceYear = experienceYear;
        this.dateOfBirth = dateOfBirth;
        this.cvId = cvId;
        this.companyId = companyId;
        this.industryId = industryId;
        this.industryName = industryName;
        this.url = url;
        this.note = note;
        this.status = status;
        this.appliedAt = appliedAt;
        this.updatedAt = updatedAt;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
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

    public String getCvId() {
        return cvId;
    }

    public void setCvId(String cvId) {
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

    public String getIndustryName() {
        return industryName;
    }

    public void setIndustryName(String industryName) {
        this.industryName = industryName;
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

    public StatusApply getStatus() {
        return status;
    }

    public void setStatus(StatusApply status) {
        this.status = status;
    }

    public LocalDateTime getAppliedAt() {
        return appliedAt;
    }

    public void setAppliedAt(LocalDateTime appliedAt) {
        this.appliedAt = appliedAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
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

    public String getInterviewLocation() {
        return interviewLocation;
    }

    public void setInterviewLocation(String interviewLocation) {
        this.interviewLocation = interviewLocation;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    public CandidateMatchInsight getMatchInsight() {
        return matchInsight;
    }

    public void setMatchInsight(CandidateMatchInsight matchInsight) {
        this.matchInsight = matchInsight;
    }
}
