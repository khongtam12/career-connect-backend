package iuh.fit.companyservice.repository;

import iuh.fit.companyservice.model.Company;
import iuh.fit.companyservice.model.StatusVerification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyRepository extends JpaRepository<Company, String> {

    @Query("SELECT c FROM Company c JOIN CompanyVerification v ON v.company.companyId = c.companyId WHERE v.status = :status")
    Page<Company> findByVerificationStatus(@Param("status") StatusVerification status, Pageable pageable);
}
