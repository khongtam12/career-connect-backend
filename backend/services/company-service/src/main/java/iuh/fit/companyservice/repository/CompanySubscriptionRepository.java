package iuh.fit.companyservice.repository;

import iuh.fit.companyservice.model.CompanySubscription;
import iuh.fit.companyservice.model.StatusPackage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.time.LocalDateTime;

@Repository
public interface CompanySubscriptionRepository extends JpaRepository<CompanySubscription,String> {
	List<CompanySubscription> findByCompany_CompanyId(String companyId);
	List<CompanySubscription> findByStatusAndEndDateBefore(StatusPackage status, LocalDateTime endDate);
}
