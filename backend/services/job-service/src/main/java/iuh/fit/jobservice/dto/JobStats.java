package iuh.fit.jobservice.dto;

public class JobStats {
    private long totalJobs;
    private long openJobs;
    private long newJobs24h;

    public JobStats(long totalJobs, long openJobs, long newJobs24h) {
        this.totalJobs = totalJobs;
        this.openJobs = openJobs;
        this.newJobs24h = newJobs24h;
    }

    public long getTotalJobs() {
        return totalJobs;
    }

    public long getOpenJobs() {
        return openJobs;
    }

    public long getNewJobs24h() {
        return newJobs24h;
    }
}
