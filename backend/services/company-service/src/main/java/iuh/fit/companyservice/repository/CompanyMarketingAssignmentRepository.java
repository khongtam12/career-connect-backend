package iuh.fit.companyservice.repository;

import iuh.fit.companyservice.model.CompanyMarketingAssignment;
import iuh.fit.companyservice.model.MarketingTargetScope;
import iuh.fit.companyservice.model.StatusMarketingAssignment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CompanyMarketingAssignmentRepository extends JpaRepository<CompanyMarketingAssignment, String> {
    List<CompanyMarketingAssignment> findByCompany_CompanyId(String companyId);
    List<CompanyMarketingAssignment> findByCompany_CompanyIdAndTargetScope(String companyId, MarketingTargetScope targetScope);
    List<CompanyMarketingAssignment> findByStatusAndExpiresAtBefore(StatusMarketingAssignment status, LocalDateTime expiresAt);
    Optional<CompanyMarketingAssignment> findByIdAndCompany_CompanyId(String id, String companyId);
    Optional<CompanyMarketingAssignment> findByEntitlementIdAndTargetIdAndStatus(String entitlementId, String targetId, StatusMarketingAssignment status);
    Optional<CompanyMarketingAssignment> findByTargetIdAndTargetScopeAndStatus(String targetId, MarketingTargetScope targetScope, StatusMarketingAssignment status);
    List<CompanyMarketingAssignment> findByTargetScopeAndStatus(MarketingTargetScope targetScope, StatusMarketingAssignment status);
}
