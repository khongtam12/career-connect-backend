package iuh.fit.jobservice.repository;

import iuh.fit.jobservice.model.Job;
import iuh.fit.jobservice.model.StatusJob;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface JobRepository extends JpaRepository<Job, String>, JpaSpecificationExecutor<Job> {
	@Query("select distinct j.location from Job j where j.location is not null and j.location <> ''")
	List<String> findDistinctLocations();

	long countByStatus(StatusJob status);

	long countByCreatedAtAfter(LocalDateTime createdAt);
}
