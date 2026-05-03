package iuh.fit.companyservice.Service;

import iuh.fit.companyservice.dto.request.CompanySubscriptionRequest;
import iuh.fit.companyservice.dto.response.CompanySubscriptionResponse;
import iuh.fit.companyservice.model.Company;
import iuh.fit.companyservice.model.CompanySubscription;
import iuh.fit.companyservice.model.StatusPackage;
import iuh.fit.companyservice.repository.CompanySubscriptionRepository;
import iuh.fit.companyservice.util.IdGenerator;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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
        sub.setPackageLabel(cmp.getPackageLabel());
        sub.setJobPostedCount(0);
        sub.setJobPostLimit(cmp.getJobPostLimit());
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusDays(cmp.getDurationDays());
        sub.setStartDate(start);
        sub.setEndDate(end);
        sub.setStatus(StatusPackage.ACTIVE);
        return companySubscriptionRepository.save(sub);

    }

    public CompanySubscriptionResponse getById(String subscriptionId) {
        CompanySubscription subscription = companySubscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new RuntimeException("Subscription not found"));
        return toResponse(subscription);
    }

    public List<CompanySubscriptionResponse> getByCompanyId(String companyId) {
        return companySubscriptionRepository.findByCompany_CompanyId(companyId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public CompanySubscriptionResponse consumeJobPost(String subscriptionId) {
        CompanySubscription subscription = companySubscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new RuntimeException("Subscription not found"));

        if (subscription.getStatus() != StatusPackage.ACTIVE) {
            throw new RuntimeException("Subscription is not active");
        }

        LocalDateTime now = LocalDateTime.now();
        if (subscription.getEndDate() != null && subscription.getEndDate().isBefore(now)) {
            subscription.setStatus(StatusPackage.EXPIRED);
            companySubscriptionRepository.save(subscription);
            throw new RuntimeException("Subscription expired");
        }

        if (subscription.getJobPostedCount() >= subscription.getJobPostLimit()) {
            throw new RuntimeException("Subscription job post limit reached");
        }

        subscription.setJobPostedCount(subscription.getJobPostedCount() + 1);
        CompanySubscription saved = companySubscriptionRepository.save(subscription);
        return toResponse(saved);
    }

    public List<String> expireSubscriptions() {
        LocalDateTime now = LocalDateTime.now();
        List<CompanySubscription> expired = companySubscriptionRepository
                .findByStatusAndEndDateBefore(StatusPackage.ACTIVE, now);

        if (expired.isEmpty()) {
            return List.of();
        }

        expired.forEach(sub -> sub.setStatus(StatusPackage.EXPIRED));
        companySubscriptionRepository.saveAll(expired);

        return expired.stream()
                .map(CompanySubscription::getId)
                .collect(Collectors.toList());
    }

    private CompanySubscriptionResponse toResponse(CompanySubscription subscription) {
        return CompanySubscriptionResponse.builder()
                .id(subscription.getId())
                .companyId(subscription.getCompany() != null ? subscription.getCompany().getCompanyId() : null)
                .packageId(subscription.getPackageId())
            .packageLabel(subscription.getPackageLabel())
                .jobPostLimit(subscription.getJobPostLimit())
                .jobPostedCount(subscription.getJobPostedCount())
                .startDate(subscription.getStartDate())
                .endDate(subscription.getEndDate())
                .status(subscription.getStatus())
                .build();
    }
}
