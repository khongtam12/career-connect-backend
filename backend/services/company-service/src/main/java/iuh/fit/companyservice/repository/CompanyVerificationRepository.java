package iuh.fit.companyservice.repository;

import iuh.fit.companyservice.model.CompanyVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyVerificationRepository extends JpaRepository<CompanyVerification,String> {
}
