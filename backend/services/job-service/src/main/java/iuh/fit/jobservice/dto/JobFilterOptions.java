package iuh.fit.jobservice.dto;

import iuh.fit.jobservice.model.JobType;
import iuh.fit.jobservice.model.StatusJob;
import java.util.List;

public class JobFilterOptions {
    private List<JobType> jobTypes;
    private List<StatusJob> statuses;
    private List<String> locations;
    private List<IndustrySummary> industries;

    public JobFilterOptions(
            List<JobType> jobTypes,
            List<StatusJob> statuses,
            List<String> locations,
            List<IndustrySummary> industries) {
        this.jobTypes = jobTypes;
        this.statuses = statuses;
        this.locations = locations;
        this.industries = industries;
    }

    public List<JobType> getJobTypes() {
        return jobTypes;
    }

    public List<StatusJob> getStatuses() {
        return statuses;
    }

    public List<String> getLocations() {
        return locations;
    }

    public List<IndustrySummary> getIndustries() {
        return industries;
    }
}
