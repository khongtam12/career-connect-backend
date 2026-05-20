package iuh.fit.companyservice.repository;

import iuh.fit.companyservice.model.CompanyVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import iuh.fit.companyservice.model.StatusVerification;

@Repository
public interface CompanyVerificationRepository extends JpaRepository<CompanyVerification,String> {
    Optional<CompanyVerification> findByCompanyCompanyId(String companyId);

    Page<CompanyVerification> findByStatusInOrderByVerifiedAtDesc(List<StatusVerification> statuses, Pageable pageable);
}
