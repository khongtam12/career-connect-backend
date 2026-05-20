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

    Page<JobApplication> findByCandidateIdOrderByAppliedAtDesc(String candidateId, Pageable pageable);



    List<JobApplication> findByCandidateIdOrderByAppliedAtDesc(String candidateId);
    // lay danh sach y=ung cu vien cua 1 cong viec

    Page<JobApplication> findByJobIdOrderByAppliedAtDesc(String jobId, Pageable pageable);

    long countByJobId(String jobId);

    List<JobApplication> findByCandidateId(String candidateId);

    List<JobApplication> findByCompanyIdOrderByAppliedAtDesc(String companyId);

    List<JobApplication> findByCompanyIdAndJobIdOrderByAppliedAtDesc(String companyId, String jobId);

    @org.springframework.data.jpa.repository.Query(value = "SELECT DATE(a.applied_at) as date, COUNT(a.id) as count " +
                   "FROM job_applications a " +
                   "WHERE a.applied_at >= :startDate " +
                   "GROUP BY DATE(a.applied_at) " +
                   "ORDER BY DATE(a.applied_at) ASC", nativeQuery = true)
    List<Object[]> countApplicationsByDay(@org.springframework.data.repository.query.Param("startDate") java.time.LocalDateTime startDate);
}
