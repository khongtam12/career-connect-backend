package iuh.fit.applicationservice.service;

import iuh.fit.applicationservice.dto.request.CreateJobApplicationRequest;
import iuh.fit.applicationservice.dto.response.JobApplicationResponse;
import iuh.fit.applicationservice.exception.AppException;
import iuh.fit.applicationservice.exception.ErrorCode;
import iuh.fit.applicationservice.model.JobApplication;
import iuh.fit.applicationservice.model.StatusApply;
import iuh.fit.applicationservice.repository.JobApplicationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.rmi.server.UID;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service

public class JobApplicationService {

    private  JobApplicationRepository jobApplicationRepository;
    private final String USER_SERVICE_URL = "http://user-service/api/v1/user";
    private final String JOB_SERVICE_URL = "http://job-service/api/v1/job";

    public JobApplicationService(JobApplicationRepository jobApplicationRepository) {
        this.jobApplicationRepository = jobApplicationRepository;
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

        if (jobApplicationRepository.existsByJobIdAndCandidateId(request.getJobId(),candidateId)){
            throw new AppException(ErrorCode.DUPLICATE_APPLICATION);
        }
        // tao ho so
        JobApplication app = new JobApplication();
        app.setId(UUID.randomUUID().toString());
        app.setJobId(request.getJobId());
        app.setCandidateId(candidateId);
        app.setCvId(request.getCvId());
        app.setNote(request.getNote());
        app.setStatus(StatusApply.APPLIED);
        app.setAppliedAt(LocalDateTime.now());
        app.setUpdatedAt(LocalDateTime.now());

        jobApplicationRepository.save(app);
        return mapToResponse(app);
    }


    // chi tiet ho so ung cu vien
    public JobApplicationResponse getById(String id){
        JobApplication app = jobApplicationRepository.findById(id).
                orElseThrow(()->new AppException(ErrorCode.APPLICATION_NOT_FOUND));
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
        res.setNote(app.getNote());
        res.setStatus(app.getStatus());
        res.setAppliedAt(app.getAppliedAt());
        res.setUpdatedAt(app.getUpdatedAt());

        return res;
    }


}
