package iuh.fit.applicationservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "job_applications")
public class JobApplication {
    @Id
    private String id;
    private String jobId;

    private String candidateId;

    private String cvId;
    private String companyId;
    private String industryId;
    private String url;

    private String note;

    @Enumerated(EnumType.STRING)
    private StatusApply status;

    private LocalDateTime appliedAt;

    private LocalDateTime updatedAt;

    private String interviewDate;
    private String interviewTime;
    private String interviewLocation;
    private String rejectionReason;
    @Column(columnDefinition = "TEXT")
    private String matchInsightJson;
    private LocalDateTime analyzedAt;
    @Column(columnDefinition = "TEXT")
    private String analysisFingerprint;

    public JobApplication() {
    }

    public JobApplication(String id, String jobId, String candidateId, String cvId, String companyId,String industryId,String url, String note, StatusApply status, LocalDateTime appliedAt, LocalDateTime updatedAt) {
        this.id = id;
        this.jobId = jobId;
        this.candidateId = candidateId;
        this.cvId = cvId;
        this.companyId = companyId;
        this.industryId = industryId;
        this.url = url;
        this.note = note;
        this.status = status;
        this.appliedAt = appliedAt;
        this.updatedAt = updatedAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getMatchInsightJson() {
        return matchInsightJson;
    }

    public void setMatchInsightJson(String matchInsightJson) {
        this.matchInsightJson = matchInsightJson;
    }

    public LocalDateTime getAnalyzedAt() {
        return analyzedAt;
    }

    public void setAnalyzedAt(LocalDateTime analyzedAt) {
        this.analyzedAt = analyzedAt;
    }

    public String getAnalysisFingerprint() {
        return analysisFingerprint;
    }

    public void setAnalysisFingerprint(String analysisFingerprint) {
        this.analysisFingerprint = analysisFingerprint;
    }
}
