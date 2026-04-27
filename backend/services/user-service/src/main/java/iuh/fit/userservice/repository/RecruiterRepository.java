package iuh.fit.userservice.repository;

import iuh.fit.userservice.model.Recruiter;
import iuh.fit.userservice.model.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecruiterRepository extends JpaRepository<Recruiter, String> {
    boolean existsByEmail(String email);
    Page<Recruiter> findAllByRole(Role role, Pageable pageable);
}
