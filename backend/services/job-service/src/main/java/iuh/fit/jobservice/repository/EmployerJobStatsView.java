package iuh.fit.jobservice.repository;

public interface EmployerJobStatsView {
    String getEmployerId();

    Long getJobCount();

    Long getViewCount();
}
