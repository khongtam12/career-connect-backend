package iuh.fit.userservice.repository;

import iuh.fit.userservice.model.Employer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmployerRepository extends JpaRepository<Employer,String> {
    Optional<Employer> findByEmail(String email);
}
