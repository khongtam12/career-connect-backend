package iuh.fit.jobservice.repository;

import iuh.fit.jobservice.model.Job;
import iuh.fit.jobservice.model.StatusJob;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
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

        @Query("select distinct j.province from Job j where j.province is not null and j.province <> ''")
        List<String> findDistinctLocations();

        @Query("select distinct j.rank from Job j where j.rank is not null and j.rank <> ''")
        List<String> findDistinctRanks();

        @Query("select distinct j.education from Job j where j.education is not null and j.education <> ''")
        List<String> findDistinctEducations();

        long countByStatus(StatusJob status);

        long countByDeletedAtIsNullAndStatusNot(StatusJob status);

        long countByDeletedAtIsNullAndStatus(StatusJob status);

        long countByCreatedAtAfter(LocalDateTime createdAt);

        long countByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

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
                        "AND (:location IS NULL OR j.province LIKE CONCAT('%', :location, '%')) " +
                        "ORDER BY j.created_at DESC", countQuery = "SELECT count(*) FROM jobs j WHERE j.status = 'ACTIVE' "
                                        +
                                        "AND j.deleted_at IS NULL " +
                                        "AND (:search IS NULL OR j.title ILIKE CONCAT('%', :search, '%')) " +
                                        "AND (:industry IS NULL OR j.industry = :industry) " +
                                        "AND (:jobType IS NULL OR j.job_type = :jobType) " +
                                        "AND (:location IS NULL OR j.province ILIKE CONCAT('%', :location, '%'))", nativeQuery = true)
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

        List<Job> findByMarketingAssignmentIdIsNotNullAndStatusIn(List<StatusJob> statuses);

        long countByCompanySubscriptionIdAndStatus(String companySubscriptionId, StatusJob status);

        // Tăng lượt xem trực tiếp bằng query (hiệu quả hơn load + save toàn bộ entity)
        @Modifying
        @Query("UPDATE Job j SET j.views = j.views + 1 WHERE j.jobId = :jobId")
        void incrementViews(@Param("jobId") String jobId);

        // Tăng số lượng ứng viên trực tiếp bằng query
        @Modifying
        @Query("UPDATE Job j SET j.numberOfApplications = j.numberOfApplications + 1, j.updatedAt = :updatedAt WHERE j.jobId = :jobId")
        int incrementApplications(@Param("jobId") String jobId, @Param("updatedAt") LocalDateTime updatedAt);

        Page<Job> findByDeletedAtIsNullOrderByCreatedAtDesc(Pageable pageable);

        Page<Job> findByStatusInAndDeletedAtIsNullOrderByUpdatedAtDesc(List<StatusJob> statuses, Pageable pageable);

        @Query("SELECT j FROM Job j WHERE j.status IN :statuses AND j.deletedAt IS NULL ORDER BY COALESCE(j.updatedAt, j.createdAt) DESC")
        Page<Job> findRecentStatusActivities(@Param("statuses") List<StatusJob> statuses, Pageable pageable);

        Page<Job> findByNumberOfApplicationsGreaterThanAndDeletedAtIsNullOrderByUpdatedAtDesc(int min, Pageable pageable);

        // thống kê số tin và lượt xem theo employer
        @Query(value = "SELECT j.employer_id AS employerId, COUNT(*) AS jobCount, COALESCE(SUM(j.views), 0) AS viewCount " +
                        "FROM jobs j " +
                        "WHERE j.deleted_at IS NULL " +
                        "AND j.employer_id IN :employerIds " +
                        "AND j.status IN ('ACTIVE', 'CLOSED') " +
                        "AND j.created_at >= :startDate " +
                        "AND j.created_at < :endDate " +
                        "GROUP BY j.employer_id", nativeQuery = true)
        List<EmployerJobStatsView> findEmployerJobStats(
                @Param("employerIds") List<String> employerIds,
                @Param("startDate") java.time.LocalDateTime startDate,
                @Param("endDate") java.time.LocalDateTime endDate);

        // Đếm việc làm mới theo ngày trong khoảng thời gian (chỉ lấy ACTIVE hoặc CLOSED và chưa bị xóa)
        @Query(value = "SELECT DATE(j.created_at) as date, COUNT(j.job_id) as count " +
                       "FROM jobs j " +
                       "WHERE j.created_at >= :startDate " +
                       "AND j.deleted_at IS NULL " +
                       "AND j.status IN ('ACTIVE', 'CLOSED') " +
                       "GROUP BY DATE(j.created_at) " +
                       "ORDER BY DATE(j.created_at) ASC", nativeQuery = true)
        List<Object[]> countNewJobsByDay(@Param("startDate") java.time.LocalDateTime startDate);
}
