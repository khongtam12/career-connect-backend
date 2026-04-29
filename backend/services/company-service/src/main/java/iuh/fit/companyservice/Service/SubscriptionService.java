package iuh.fit.companyservice.Service;

import iuh.fit.companyservice.dto.request.CompanySubscriptionRequest;
import iuh.fit.companyservice.model.Company;
import iuh.fit.companyservice.model.CompanySubscription;
import iuh.fit.companyservice.model.StatusPackage;
import iuh.fit.companyservice.repository.CompanySubscriptionRepository;
import iuh.fit.companyservice.util.IdGenerator;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class SubscriptionService {
    private final CompanySubscriptionRepository companySubscriptionRepository;

    public SubscriptionService(CompanySubscriptionRepository companySubscriptionRepository) {
        this.companySubscriptionRepository = companySubscriptionRepository;
    }

    public CompanySubscription save(CompanySubscriptionRequest cmp){
        CompanySubscription sub = new CompanySubscription();
        sub.setCompany(new Company(cmp.getCompanyId()));
        sub.setId(IdGenerator.generatorIdCompannySubscription());
        sub.setPackageId(cmp.getPackageId());
        sub.setJobPostedCount(0);
        sub.setJobPostLimit(cmp.getJobPostLimit());
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusDays(cmp.getDurationDays());
        sub.setStartDate(start);
        sub.setEndDate(end);
        sub.setStatus(StatusPackage.ACTIVE);
        return companySubscriptionRepository.save(sub);

    }
}
