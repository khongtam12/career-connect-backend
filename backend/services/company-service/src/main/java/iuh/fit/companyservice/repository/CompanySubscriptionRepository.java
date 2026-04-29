package iuh.fit.companyservice.repository;

import iuh.fit.companyservice.model.CompanySubscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanySubscriptionRepository extends JpaRepository<CompanySubscription,String> {
}
