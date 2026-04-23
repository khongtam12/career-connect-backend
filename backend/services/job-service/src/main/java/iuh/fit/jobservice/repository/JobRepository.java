package iuh.fit.jobservice.repository;

import iuh.fit.jobservice.model.Job;
import iuh.fit.jobservice.model.StatusJob;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface JobRepository extends JpaRepository<Job, String> {

    // tìm job chưa bị xóa mềm
    @Query("SELECT j FROM Job j WHERE j.jobId = :jobId AND j.deletedAt IS NULL")
    Optional<Job> findActiveById(@Param("jobId") String jobId);

    // đếm theo status (chưa xóa)
    @Query("SELECT COUNT(j) FROM Job j WHERE j.employerId = :employerId AND j.status = :status AND j.deletedAt IS NULL")
    long countByEmployerIdAndStatus(@Param("employerId") String employerId, @Param("status") StatusJob status);

    // danh sách tin employer có filter + phân trang (chưa xóa)
    @Query(value = "SELECT * FROM jobs j WHERE j.employer_id = :employerId " +
            "AND j.deleted_at IS NULL " +
            "AND (:status IS NULL OR j.status = :status) " +
            "AND (:search IS NULL OR j.title ILIKE CONCAT('%', :search, '%')) " +
            "ORDER BY j.is_top DESC, j.created_at DESC",
            countQuery = "SELECT count(*) FROM jobs j WHERE j.employer_id = :employerId " +
                    "AND j.deleted_at IS NULL " +
                    "AND (:status IS NULL OR j.status = :status) " +
                    "AND (:search IS NULL OR j.title ILIKE CONCAT('%', :search, '%'))",
            nativeQuery = true)
    Page<Job> findByEmployerWithFilter(
            @Param("employerId") String employerId,
            @Param("status") String status,
            @Param("search") String search,
            Pageable pageable
    );

    // tổng ứng viên của employer (chưa xóa)
    @Query("SELECT COALESCE(SUM(j.numberOfApplications), 0) FROM Job j WHERE j.employerId = :employerId AND j.deletedAt IS NULL")
    int sumApplicationsByEmployerId(@Param("employerId") String employerId);

    // tìm kiếm public — chỉ ACTIVE và chưa xóa
    @Query(value = "SELECT * FROM jobs j WHERE j.status = 'ACTIVE' " +
            "AND j.deleted_at IS NULL " +
            "AND (:search IS NULL OR j.title ILIKE CONCAT('%', :search, '%')) " +
            "AND (:industry IS NULL OR j.industry = :industry) " +
            "AND (:jobType IS NULL OR j.job_type = :jobType) " +
            "AND (:location IS NULL OR j.location ILIKE CONCAT('%', :location, '%')) " +
            "ORDER BY j.is_top DESC, j.created_at DESC",
            countQuery = "SELECT count(*) FROM jobs j WHERE j.status = 'ACTIVE' " +
                    "AND j.deleted_at IS NULL " +
                    "AND (:search IS NULL OR j.title ILIKE CONCAT('%', :search, '%')) " +
                    "AND (:industry IS NULL OR j.industry = :industry) " +
                    "AND (:jobType IS NULL OR j.job_type = :jobType) " +
                    "AND (:location IS NULL OR j.location ILIKE CONCAT('%', :location, '%'))",
            nativeQuery = true)
    Page<Job> searchActiveJobs(
            @Param("search") String search,
            @Param("industry") String industry,
            @Param("jobType") String jobType,
            @Param("location") String location,
            Pageable pageable
    );

    // system: tìm ACTIVE jobs hết deadline để tự expire (chưa xóa)
    @Query("SELECT j FROM Job j WHERE j.status = :status AND j.deadline < :date AND j.deletedAt IS NULL")
    List<Job> findByStatusAndDeadlineBefore(@Param("status") StatusJob status, @Param("date") LocalDate date);
}
