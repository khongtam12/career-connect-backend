package iuh.fit.jobservice.repository;

import iuh.fit.jobservice.model.Industry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IndustryRepository extends JpaRepository<Industry, String> {
}
