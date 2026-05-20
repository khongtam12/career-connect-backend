package iuh.fit.userservice.repository;

import iuh.fit.userservice.model.Admin;
import iuh.fit.userservice.model.Candidate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.Optional;

@Repository
public interface CandidateRepository extends JpaRepository<Candidate, String> {

    Optional<Candidate> findByEmail(String email);

    @org.springframework.data.jpa.repository.Query(value = "SELECT * FROM candidates c WHERE " +
            "(:keyword IS NULL OR LOWER(c.full_name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(c.email) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
            "(:status IS NULL OR c.status = :status)",
            countQuery = "SELECT count(*) FROM candidates c WHERE " +
            "(:keyword IS NULL OR LOWER(c.full_name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(c.email) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
            "(:status IS NULL OR c.status = :status)",
            nativeQuery = true)
    org.springframework.data.domain.Page<Candidate> searchCandidates(@org.springframework.data.repository.query.Param("keyword") String keyword, @org.springframework.data.repository.query.Param("status") String status, org.springframework.data.domain.Pageable pageable);

    long countByStatus(iuh.fit.userservice.model.Status status);

    long countByCreatedAtBetween(java.time.LocalDate startDate, java.time.LocalDate endDate);

    boolean existsByEmail(String email);
}
