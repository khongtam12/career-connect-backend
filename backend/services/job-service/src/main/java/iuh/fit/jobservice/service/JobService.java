package iuh.fit.jobservice.service;

import iuh.fit.jobservice.dto.IndustrySummary;
import iuh.fit.jobservice.dto.JobFilterOptions;
import iuh.fit.jobservice.dto.JobStats;
import iuh.fit.jobservice.model.Job;
import iuh.fit.jobservice.model.JobType;
import iuh.fit.jobservice.model.StatusJob;
import iuh.fit.jobservice.repository.IndustryRepository;
import iuh.fit.jobservice.repository.JobRepository;
import iuh.fit.jobservice.specification.JobSpecifications;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Service
public class JobService {
    private final JobRepository jobRepository;
    private final IndustryRepository industryRepository;

    public JobService(JobRepository jobRepository, IndustryRepository industryRepository) {
        this.jobRepository = jobRepository;
        this.industryRepository = industryRepository;
    }

    public Page<Job> searchJobs(
        String keyword,
        String location,
        String industryId,
        String fieldId,
        JobType jobType,
        StatusJob status,
        Integer experienceMin,
        Integer experienceMax,
        Double salaryMin,
        Double salaryMax,
        int page,
        int size,
        String sortBy,
        String sortDir
    ) {
        Specification<Job> spec = Specification
            .where(JobSpecifications.keywordContains(keyword))
            .and(JobSpecifications.locationContains(location))
            .and(JobSpecifications.industryEquals(industryId))
            .and(JobSpecifications.fieldEquals(fieldId))
            .and(JobSpecifications.jobTypeEquals(jobType))
            .and(JobSpecifications.statusEquals(status == null ? StatusJob.OPEN : status))
            .and(JobSpecifications.experienceMin(experienceMin))
            .and(JobSpecifications.experienceMax(experienceMax))
            .and(JobSpecifications.salaryMin(salaryMin))
            .and(JobSpecifications.salaryMax(salaryMax));

        Pageable pageable = PageRequest.of(
            Math.max(page, 0),
            Math.max(size, 1),
            Sort.by(resolveSortDirection(sortDir), resolveSortField(sortBy))
        );

        return jobRepository.findAll(spec, pageable);
    }

    public JobFilterOptions getFilterOptions() {
        List<JobType> jobTypes = Arrays.asList(JobType.values());
        List<StatusJob> statuses = Arrays.asList(StatusJob.values());
        List<String> locations = jobRepository.findDistinctLocations();
        List<IndustrySummary> industries = industryRepository.findAll().stream()
            .map(industry -> new IndustrySummary(industry.getIndustryId(), industry.getName()))
            .toList();

        return new JobFilterOptions(jobTypes, statuses, locations, industries);
    }

    public JobStats getStats() {
        long totalJobs = jobRepository.count();
        long openJobs = jobRepository.countByStatus(StatusJob.OPEN);
        long newJobs24h = jobRepository.countByCreatedAtAfter(LocalDateTime.now().minusHours(24));
        return new JobStats(totalJobs, openJobs, newJobs24h);
    }

    private Sort.Direction resolveSortDirection(String sortDir) {
        if (sortDir == null) {
            return Sort.Direction.DESC;
        }
        return "asc".equalsIgnoreCase(sortDir) ? Sort.Direction.ASC : Sort.Direction.DESC;
    }

    private String resolveSortField(String sortBy) {
        if (sortBy == null) {
            return "createdAt";
        }
        return switch (sortBy) {
            case "salaryMax" -> "salaryMax";
            case "salaryMin" -> "salaryMin";
            case "deadline" -> "deadline";
            case "experienceRequired" -> "experienceRequired";
            default -> "createdAt";
        };
    }
}
