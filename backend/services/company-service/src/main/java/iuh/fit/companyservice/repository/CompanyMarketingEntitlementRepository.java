package iuh.fit.companyservice.repository;

import iuh.fit.companyservice.model.CompanyMarketingEntitlement;
import iuh.fit.companyservice.model.StatusMarketingEntitlement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface CompanyMarketingEntitlementRepository extends JpaRepository<CompanyMarketingEntitlement, String> {
    List<CompanyMarketingEntitlement> findByCompany_CompanyId(String companyId);
    List<CompanyMarketingEntitlement> findByCompany_CompanyIdAndPackageCategory(String companyId, String packageCategory);
    List<CompanyMarketingEntitlement> findByStatusAndEndDateBefore(StatusMarketingEntitlement status, LocalDateTime endDate);
}
