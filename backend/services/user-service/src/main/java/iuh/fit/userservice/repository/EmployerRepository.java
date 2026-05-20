package iuh.fit.userservice.repository;

import iuh.fit.userservice.model.Employer;
import iuh.fit.userservice.model.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.time.LocalDate;

@Repository
public interface EmployerRepository extends JpaRepository<Employer,String> {
    Optional<Employer> findByEmail(String email);
    boolean existsByEmail(String email);
    long countByStatus(Status status);

    @org.springframework.data.jpa.repository.Query("SELECT COUNT(e) FROM Employer e WHERE e.createdAt = :date")
    long countByCreatedAt(@org.springframework.data.repository.query.Param("date") LocalDate date);

        long countByCreatedAtBetween(LocalDate startDate, LocalDate endDate);

    @org.springframework.data.jpa.repository.Query("SELECT e FROM Employer e WHERE " +
            "(:keyword IS NULL OR :keyword = '' OR LOWER(e.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(e.email) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "AND (:status IS NULL OR e.status = :status)")
    org.springframework.data.domain.Page<Employer> findByKeywordAndStatus(
            @org.springframework.data.repository.query.Param("keyword") String keyword,
            @org.springframework.data.repository.query.Param("status") Status status,
            org.springframework.data.domain.Pageable pageable);
}
