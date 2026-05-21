package iuh.fit.jobservice;

import iuh.fit.jobservice.dto.response.JobResponse;
import iuh.fit.jobservice.dto.response.JobCardResponse;
import iuh.fit.jobservice.dto.response.PageResponse;
import iuh.fit.jobservice.model.Job;
import iuh.fit.jobservice.model.JobType;
import iuh.fit.jobservice.model.StatusJob;
import iuh.fit.jobservice.repository.JobRepository;
import iuh.fit.jobservice.service.JobService;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.boot.test.mock.mockito.SpyBean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class JobSearchCacheIntegrationTest {

    @Autowired
    private JobService jobService;

    @SpyBean
    private JobRepository jobRepository;

    @Autowired
    private CacheManager cacheManager;

    private String jobId;

    @BeforeEach
    void setUp() {
        Cache cache = cacheManager.getCache("job-search");
        if (cache != null) {
            cache.clear();
        }

        jobId = UUID.randomUUID().toString();
        Job job = new Job();
        job.setJobId(jobId);
        job.setCompanyId("COMP-TEST");
        job.setCompanyName("Cache Demo Co");
        job.setEmployerId("EMP-TEST");
        job.setIndustryId("ind-1");
        job.setTitle("cache-demo-" + jobId);
        job.setDescription("cache demo");
        job.setCandidateRequirements("none");
        job.setBenefitsDetail("none");
        job.setSalaryMin(1000);
        job.setSalaryMax(2000);
        job.setSalaryNegotiable(false);
        job.setLocation("hcm");
        job.setExperience("1 year");
        job.setDeadline(LocalDate.now().plusDays(30));
        job.setCreatedAt(LocalDateTime.now());
        job.setUpdatedAt(LocalDateTime.now());
        job.setNumberOfApplications(0);
        job.setStatus(StatusJob.ACTIVE);
        job.setJobType(JobType.FULL_TIME);
        jobRepository.save(job);
    }

    @AfterEach
    void tearDown() {
        if (jobId != null) {
            jobRepository.deleteById(jobId);
        }
    }

    @Test
    void searchJobs_usesCacheOnSecondCall() {
        String keyword = "cache-demo-" + jobId;

        PageResponse<JobCardResponse> first = jobService.searchJobs(
                keyword,
                "ind-1",
                "FULL_TIME",
                null,
                null,
                "hcm",
                "ACTIVE",
                null,
                null,
                null,
                null,
                null,
                null,
                1,
                10
        );

        PageResponse<JobCardResponse> second = jobService.searchJobs(
                keyword,
                "ind-1",
                "FULL_TIME",
                null,
                null,
                "hcm",
                "ACTIVE",
                null,
                null,
                null,
                null,
                null,
                null,
                1,
                10
        );

        assertThat(first.getTotalElements()).isGreaterThanOrEqualTo(1);
        assertThat(second.getTotalElements()).isGreaterThanOrEqualTo(1);

        verify(jobRepository, times(1)).findAll(any(Specification.class), any(Pageable.class));
    }
}
