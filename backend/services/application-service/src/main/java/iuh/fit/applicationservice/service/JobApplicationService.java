package iuh.fit.applicationservice.service;

import iuh.fit.applicationservice.client.JobServiceClient;
import iuh.fit.applicationservice.client.NotificationServiceClient;
import iuh.fit.applicationservice.client.UserServiceClient;
import iuh.fit.applicationservice.dto.request.CreateJobApplicationRequest;
import iuh.fit.applicationservice.dto.request.ScheduleInterviewRequest;
import iuh.fit.applicationservice.dto.request.SendEmailRequest;
import iuh.fit.applicationservice.dto.request.UpdateStatusRequest;
import iuh.fit.applicationservice.dto.response.CandidateApplicationResponse;
import iuh.fit.applicationservice.dto.response.CandidateSummaryClientResponse;
import iuh.fit.applicationservice.dto.response.EmployerCompanyClientResponse;
import iuh.fit.applicationservice.dto.response.JobApplicationResponse;
import iuh.fit.applicationservice.dto.response.JobDetailClientResponse;
import iuh.fit.applicationservice.exception.AppException;
import iuh.fit.applicationservice.exception.ErrorCode;
import iuh.fit.applicationservice.model.JobApplication;
import iuh.fit.applicationservice.model.StatusApply;
import iuh.fit.applicationservice.repository.JobApplicationRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class JobApplicationService {

    private final JobApplicationRepository jobApplicationRepository;
    private final UserServiceClient userServiceClient;
    private final JobServiceClient jobServiceClient;
    private final NotificationServiceClient notificationServiceClient;

    public JobApplicationService(JobApplicationRepository jobApplicationRepository,
                                 UserServiceClient userServiceClient,
                                 JobServiceClient jobServiceClient,
                                 NotificationServiceClient notificationServiceClient) {
        this.jobApplicationRepository = jobApplicationRepository;
        this.userServiceClient = userServiceClient;
        this.jobServiceClient = jobServiceClient;
        this.notificationServiceClient = notificationServiceClient;
    }

    public JobApplicationResponse applyForJob(String candidateId, CreateJobApplicationRequest request){
        //check ung cu vien ton tai
        if(candidateId == null || candidateId.isEmpty()){
            throw new AppException(ErrorCode.CANDIDATE_NOT_FOUND);
        }
        if (request.getJobId() == null || request.getJobId().isEmpty()){
            throw new AppException(ErrorCode.JOB_NOT_FOUND);
        }
        if (request.getCvId() == null || request.getCvId().isEmpty()){
            throw new AppException(ErrorCode.CV_NOT_FOUND);
        }
        if (request.getCompanyId() == null || request.getCompanyId().isEmpty()){
            throw new AppException(ErrorCode.COMPANY_NOT_FOUND);
        }
        if (request.getIndustryId() == null || request.getIndustryId().isEmpty()){
            throw new AppException(ErrorCode.INDUSTRY_NOT_FOUND);
        }

        if (jobApplicationRepository.existsByJobIdAndCandidateId(request.getJobId(),candidateId)){
            throw new AppException(ErrorCode.DUPLICATE_APPLICATION);
        }
        // tao ho so
        JobApplication app = new JobApplication();
        app.setId(UUID.randomUUID().toString());
        app.setJobId(request.getJobId());
        app.setCandidateId(candidateId);
        app.setCvId(request.getCvId());
        app.setCompanyId(request.getCompanyId());
        app.setIndustryId(request.getIndustryId());
        app.setUrl(request.getUrl());
        app.setNote(request.getNote());
        app.setStatus(StatusApply.APPLIED);
        app.setAppliedAt(LocalDateTime.now());
        app.setUpdatedAt(LocalDateTime.now());

        jobApplicationRepository.save(app);
        return mapToResponse(app);
    }


    // chi tiet ho so ung cu vien
    public JobApplicationResponse getById(String id) {
        JobApplication app = jobApplicationRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.APPLICATION_NOT_FOUND));
        return mapToResponse(app);
    }

    // len lich phong van
    public JobApplicationResponse scheduleInterview(String applicationId, ScheduleInterviewRequest request) {
        JobApplication app = jobApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new AppException(ErrorCode.APPLICATION_NOT_FOUND));

        app.setStatus(StatusApply.INTERVIEW);
        app.setInterviewDate(request.getInterviewDate());
        app.setInterviewTime(request.getInterviewTime());
        app.setInterviewLocation(request.getLocation());
        app.setNote(request.getNote());
        app.setUpdatedAt(LocalDateTime.now());

        jobApplicationRepository.save(app);

        // Gui email thong bao phong van
        try {
            CandidateSummaryClientResponse candidate = userServiceClient.getCandidateById(app.getCandidateId());
            JobDetailClientResponse job = jobServiceClient.getJobById(app.getJobId());
            if (candidate != null && candidate.getEmail() != null) {
                SendEmailRequest emailReq = new SendEmailRequest();
                emailReq.setTo(candidate.getEmail());
                emailReq.setCandidateName(candidate.getFullName());
                emailReq.setJobName(job != null ? job.getTitle() : "N/A");
                emailReq.setType("INTERVIEW_SCHEDULE");
                emailReq.setInterviewDate(request.getInterviewDate());
                emailReq.setInterviewTime(request.getInterviewTime());
                emailReq.setInterviewLocation(request.getLocation());
                emailReq.setNote(request.getNote());
                notificationServiceClient.sendEmail(emailReq);
            }
        } catch (Exception e) {
            System.err.println("Failed to send interview email: " + e.getMessage());
        }

        return mapToResponse(app);
    }

    // huy phong van
    public JobApplicationResponse cancelInterview(String applicationId) {
        JobApplication app = jobApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new AppException(ErrorCode.APPLICATION_NOT_FOUND));

        if (app.getStatus() != StatusApply.INTERVIEW) {
            throw new AppException(ErrorCode.INVALID_STATUS);
        }

        app.setStatus(StatusApply.APPLIED);
        app.setInterviewDate(null);
        app.setInterviewTime(null);
        app.setInterviewLocation(null);
        app.setUpdatedAt(LocalDateTime.now());

        jobApplicationRepository.save(app);

        // Gui email huy phong van
        try {
            CandidateSummaryClientResponse candidate = userServiceClient.getCandidateById(app.getCandidateId());
            JobDetailClientResponse job = jobServiceClient.getJobById(app.getJobId());
            if (candidate != null && candidate.getEmail() != null) {
                SendEmailRequest emailReq = new SendEmailRequest();
                emailReq.setTo(candidate.getEmail());
                emailReq.setCandidateName(candidate.getFullName());
                emailReq.setJobName(job != null ? job.getTitle() : "N/A");
                emailReq.setType("INTERVIEW_CANCEL");
                notificationServiceClient.sendEmail(emailReq);
            }
        } catch (Exception e) {
            System.err.println("Failed to send cancel interview email: " + e.getMessage());
        }

        return mapToResponse(app);
    }

    // cap nhat trang thai
    public JobApplicationResponse updateStatus(String applicationId, UpdateStatusRequest request) {
        JobApplication app = jobApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new AppException(ErrorCode.APPLICATION_NOT_FOUND));

        try {
            StatusApply newStatus = StatusApply.valueOf(request.getStatus());
            app.setStatus(newStatus);
        } catch (IllegalArgumentException e) {
            throw new AppException(ErrorCode.INVALID_STATUS);
        }

        if (request.getReason() != null && !request.getReason().isEmpty()) {
            app.setRejectionReason(request.getReason());
        }

        app.setUpdatedAt(LocalDateTime.now());
        jobApplicationRepository.save(app);

        // Gui email khi chap nhan
        if (app.getStatus() == StatusApply.ACCEPTED) {
            try {
                CandidateSummaryClientResponse candidate = userServiceClient.getCandidateById(app.getCandidateId());
                JobDetailClientResponse job = jobServiceClient.getJobById(app.getJobId());
                if (candidate != null && candidate.getEmail() != null) {
                    SendEmailRequest emailReq = new SendEmailRequest();
                    emailReq.setTo(candidate.getEmail());
                    emailReq.setCandidateName(candidate.getFullName());
                    emailReq.setJobName(job != null ? job.getTitle() : "N/A");
                    emailReq.setType("ACCEPTED");
                    notificationServiceClient.sendEmail(emailReq);
                }
            } catch (Exception e) {
                System.err.println("Failed to send accept email: " + e.getMessage());
            }
        }

        return mapToResponse(app);
    }

    // tu choi ho so
    public JobApplicationResponse rejectApplication(String applicationId, String reason) {
        JobApplication app = jobApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new AppException(ErrorCode.APPLICATION_NOT_FOUND));

        app.setStatus(StatusApply.REJECTED);
        app.setRejectionReason(reason);
        app.setUpdatedAt(LocalDateTime.now());

        jobApplicationRepository.save(app);

        // Gui email tu choi
        try {
            CandidateSummaryClientResponse candidate = userServiceClient.getCandidateById(app.getCandidateId());
            JobDetailClientResponse job = jobServiceClient.getJobById(app.getJobId());
            if (candidate != null && candidate.getEmail() != null) {
                SendEmailRequest emailReq = new SendEmailRequest();
                emailReq.setTo(candidate.getEmail());
                emailReq.setCandidateName(candidate.getFullName());
                emailReq.setJobName(job != null ? job.getTitle() : "N/A");
                emailReq.setType("REJECTED");
                emailReq.setRejectionReason(reason);
                notificationServiceClient.sendEmail(emailReq);
            }
        } catch (Exception e) {
            System.err.println("Failed to send reject email: " + e.getMessage());
        }

        return mapToResponse(app);
    }

    public List<JobApplicationResponse> getByCandidate(String candidateId) {
        if (candidateId == null || candidateId.isEmpty()) {
            throw new AppException(ErrorCode.CANDIDATE_NOT_FOUND);
        }
        return jobApplicationRepository.findByCandidateIdOrderByAppliedAtDesc(candidateId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private JobApplicationResponse mapToResponse(JobApplication app) {

        JobApplicationResponse res = new JobApplicationResponse();

        res.setApplicationId(app.getId());
        res.setJobId(app.getJobId());
        res.setCandidateId(app.getCandidateId());
        res.setCvId(app.getCvId());
        res.setCompanyId(app.getCompanyId());
        res.setIndustryId(app.getIndustryId());
        res.setUrl(app.getUrl());
        res.setNote(app.getNote());
        res.setStatus(app.getStatus());
        res.setAppliedAt(app.getAppliedAt());
        res.setUpdatedAt(app.getUpdatedAt());

        return res;
    }

    //lay danh sach ung vien
    public List<CandidateApplicationResponse> getCandidatesByEmployer(String employerId) {
        if (employerId == null || employerId.isEmpty()) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        EmployerCompanyClientResponse employer = userServiceClient.getEmployerById(employerId);
        if (employer == null || employer.getCompanyId() == null || employer.getCompanyId().isEmpty()) {
            throw new AppException(ErrorCode.COMPANY_NOT_FOUND);
        }

        List<JobApplication> applications =
                jobApplicationRepository.findByCompanyIdOrderByAppliedAtDesc(employer.getCompanyId());

        return applications.stream()
                .map(this::mapToCandidateApplicationResponse)
                .toList();
    }

    private CandidateApplicationResponse mapToCandidateApplicationResponse(JobApplication app) {
        CandidateSummaryClientResponse candidate = userServiceClient.getCandidateById(app.getCandidateId());
        JobDetailClientResponse job = jobServiceClient.getJobById(app.getJobId());

        CandidateApplicationResponse res = new CandidateApplicationResponse();
        res.setApplicationId(app.getId());
        res.setJobId(app.getJobId());
        res.setJobName(job.getTitle());
        res.setCandidateId(app.getCandidateId());
        res.setCvId(app.getCvId());
        res.setCompanyId(app.getCompanyId());
        res.setIndustryId(app.getIndustryId());
        res.setUrl(app.getUrl());
        res.setNote(app.getNote());
        res.setStatus(app.getStatus());
        res.setAppliedAt(app.getAppliedAt());
        res.setUpdatedAt(app.getUpdatedAt());
        res.setInterviewDate(app.getInterviewDate());
        res.setInterviewTime(app.getInterviewTime());
        res.setInterviewLocation(app.getInterviewLocation());
        res.setRejectionReason(app.getRejectionReason());

        if (candidate != null) {
            res.setFullName(candidate.getFullName());
            res.setExperienceYear(candidate.getExperienceYear());
            res.setDateOfBirth(candidate.getDateOfBirth());
        }

        if (job != null && job.getIndustryDTO() != null) {
            res.setIndustryName(job.getIndustryDTO().getName());
        }

        return res;
    }
}
