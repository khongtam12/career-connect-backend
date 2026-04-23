package iuh.fit.jobservice.controller;

import iuh.fit.jobservice.dto.JobFilterOptions;
import iuh.fit.jobservice.dto.JobStats;
import iuh.fit.jobservice.model.Job;
import iuh.fit.jobservice.model.JobType;
import iuh.fit.jobservice.model.StatusJob;
import iuh.fit.jobservice.service.JobService;
import org.springframework.data.domain.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/job")
public class JobController {
    @Autowired
    private JobService jobService;

    @GetMapping
    public Page<Job> getJobs(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(defaultValue = "createdAt") String sortBy,
        @RequestParam(defaultValue = "desc") String sortDir
    ) {
        return jobService.searchJobs(
            null,
            null,
            null,
            null,
            null,
            StatusJob.OPEN,
            null,
            null,
            null,
            null,
            page,
            size,
            sortBy,
            sortDir
        );
    }

    @GetMapping("/search")
    public Page<Job> searchJobs(
        @RequestParam(required = false) String keyword,
        @RequestParam(required = false) String location,
        @RequestParam(required = false) String industryId,
        @RequestParam(required = false) String fieldId,
        @RequestParam(required = false) JobType jobType,
        @RequestParam(required = false) StatusJob status,
        @RequestParam(required = false) Integer experienceMin,
        @RequestParam(required = false) Integer experienceMax,
        @RequestParam(required = false) Double salaryMin,
        @RequestParam(required = false) Double salaryMax,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(defaultValue = "createdAt") String sortBy,
        @RequestParam(defaultValue = "desc") String sortDir
    ) {
        return jobService.searchJobs(
            keyword,
            location,
            industryId,
            fieldId,
            jobType,
            status,
            experienceMin,
            experienceMax,
            salaryMin,
            salaryMax,
            page,
            size,
            sortBy,
            sortDir
        );
    }

    @GetMapping("/filters")
    public JobFilterOptions getFilterOptions() {
        return jobService.getFilterOptions();
    }

    @GetMapping("/stats")
    public JobStats getStats() {
        return jobService.getStats();
    }
}
