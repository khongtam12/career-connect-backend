package iuh.fit.companyservice.repository;

import iuh.fit.companyservice.model.CompanyMarketingAssignment;
import iuh.fit.companyservice.model.MarketingTargetScope;
import iuh.fit.companyservice.model.StatusMarketingAssignment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CompanyMarketingAssignmentRepository extends JpaRepository<CompanyMarketingAssignment, String> {
    List<CompanyMarketingAssignment> findByCompanyId(String companyId);
    List<CompanyMarketingAssignment> findByCompanyIdAndTargetScope(String companyId, MarketingTargetScope targetScope);
    List<CompanyMarketingAssignment> findByStatusAndExpiresAtBefore(StatusMarketingAssignment status, LocalDateTime expiresAt);
    Optional<CompanyMarketingAssignment> findByIdAndCompanyId(String id, String companyId);
    Optional<CompanyMarketingAssignment> findByEntitlementIdAndTargetIdAndStatus(String entitlementId, String targetId, StatusMarketingAssignment status);
    Optional<CompanyMarketingAssignment> findByTargetIdAndTargetScopeAndStatus(String targetId, MarketingTargetScope targetScope, StatusMarketingAssignment status);
}
