package iuh.fit.applicationservice.repository;

import iuh.fit.applicationservice.model.JobApplication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface JobApplicationRepository extends JpaRepository<JobApplication, String> {

    boolean existsByJobIdAndCandidateId(String jobId, String candidateId);

    Optional<JobApplication> findById(String id);

    // lay danh sach ho so cua ung cu vien nay
    Page<JobApplication> findByCandidateIdOrderByAppliedAtDesc(String candidateId, Pageable pageable);
    List<JobApplication> findByCandidateIdOrderByAppliedAtDesc(String candidateId);
    // lay danh sach y=ung cu vien cua 1 cong viec
    Page<JobApplication> findByJobIdOrderByAppliedAtDesc(String jobId, Pageable pageable);

    long countByJobId(String jobId);

    List<JobApplication> findByCandidateId(String candidateId);
}