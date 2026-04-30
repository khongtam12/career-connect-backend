package iuh.fit.companyservice.repository;

import iuh.fit.companyservice.model.CompanyApprovalLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyApprovalLogRepository extends JpaRepository<CompanyApprovalLog, String> {
}
