package iuh.fit.jobservice.dto;

public class JobStats {
    private long totalJobs;
    private long openJobs;
    private long newJobs24h;
    private long pendingJobs;
    private long rejectedJobs;

    public JobStats(long totalJobs, long openJobs, long newJobs24h, long pendingJobs, long rejectedJobs) {
        this.totalJobs = totalJobs;
        this.openJobs = openJobs;
        this.newJobs24h = newJobs24h;
        this.pendingJobs = pendingJobs;
        this.rejectedJobs = rejectedJobs;
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

    public long getPendingJobs() {
        return pendingJobs;
    }

    public long getRejectedJobs() {
        return rejectedJobs;
    }
}
