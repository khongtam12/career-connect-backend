package iuh.fit.jobservice.repository;

import iuh.fit.jobservice.model.Job;
import iuh.fit.jobservice.model.StatusJob;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface JobRepository extends JpaRepository<Job, String>, JpaSpecificationExecutor<Job> {

        // ===== HEAD (giữ lại) =====

        @Query("select distinct j.location from Job j where j.location is not null and j.location <> ''")
        List<String> findDistinctLocations();

        long countByStatus(StatusJob status);

        long countByCreatedAtAfter(LocalDateTime createdAt);

        // ===== FEATURE (giữ full) =====

        // tìm job chưa bị xóa mềm
        @Query("SELECT j FROM Job j WHERE j.jobId = :jobId AND j.deletedAt IS NULL")
        Optional<Job> findActiveById(@Param("jobId") String jobId);

        // đếm theo status (chưa xóa)
        @Query("SELECT COUNT(j) FROM Job j WHERE j.employerId = :employerId AND j.status = :status AND j.deletedAt IS NULL")
        long countByEmployerIdAndStatus(@Param("employerId") String employerId,
                        @Param("status") StatusJob status);

        // danh sách tin employer có filter + phân trang (native)
        @Query(value = "SELECT * FROM jobs j WHERE j.employer_id = :employerId " +
                        "AND j.deleted_at IS NULL " +
                        "AND (:status IS NULL OR j.status = :status) " +
                        "AND (:search IS NULL OR j.title LIKE CONCAT('%', :search, '%')) " +
                        "ORDER BY j.created_at DESC", countQuery = "SELECT count(*) FROM jobs j WHERE j.employer_id = :employerId "
                                        +
                                        "AND j.deleted_at IS NULL " +
                                        "AND (:status IS NULL OR j.status = :status) " +
                                        "AND (:search IS NULL OR j.title ILIKE CONCAT('%', :search, '%'))", nativeQuery = true)
        Page<Job> findByEmployerWithFilter(
                        @Param("employerId") String employerId,
                        @Param("status") String status,
                        @Param("search") String search,
                        Pageable pageable);

        // tổng ứng viên của employer
        @Query("SELECT COALESCE(SUM(j.numberOfApplications), 0) FROM Job j WHERE j.employerId = :employerId AND j.deletedAt IS NULL")
        int sumApplicationsByEmployerId(@Param("employerId") String employerId);

        // tìm kiếm public — chỉ ACTIVE
        @Query(value = "SELECT * FROM jobs j WHERE j.status = 'ACTIVE' " +
                        "AND j.deleted_at IS NULL " +
                        "AND (:search IS NULL OR j.title LIKE CONCAT('%', :search, '%')) " +
                        "AND (:industry IS NULL OR j.industry = :industry) " +
                        "AND (:jobType IS NULL OR j.job_type = :jobType) " +
                        "AND (:location IS NULL OR j.location LIKE CONCAT('%', :location, '%')) " +
                        "ORDER BY j.created_at DESC", countQuery = "SELECT count(*) FROM jobs j WHERE j.status = 'ACTIVE' "
                                        +
                                        "AND j.deleted_at IS NULL " +
                                        "AND (:search IS NULL OR j.title ILIKE CONCAT('%', :search, '%')) " +
                                        "AND (:industry IS NULL OR j.industry = :industry) " +
                                        "AND (:jobType IS NULL OR j.job_type = :jobType) " +
                                        "AND (:location IS NULL OR j.location ILIKE CONCAT('%', :location, '%'))", nativeQuery = true)
        Page<Job> searchActiveJobs(
                        @Param("search") String search,
                        @Param("industry") String industry,
                        @Param("jobType") String jobType,
                        @Param("location") String location,
                        Pageable pageable);

        // system: auto expire job
        @Query("SELECT j FROM Job j WHERE j.status = :status AND j.deadline < :date AND j.deletedAt IS NULL")
        List<Job> findByStatusAndDeadlineBefore(@Param("status") StatusJob status,
                        @Param("date") LocalDate date);

        List<Job> findByCompanySubscriptionIdInAndStatusIn(List<String> subscriptionIds, List<StatusJob> statuses);

        long countByCompanySubscriptionIdAndStatus(String companySubscriptionId, StatusJob status);
}