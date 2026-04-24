package iuh.fit.jobservice.specification;

import iuh.fit.jobservice.model.Job;
import iuh.fit.jobservice.model.JobType;
import iuh.fit.jobservice.model.StatusJob;
import org.springframework.data.jpa.domain.Specification;

public final class JobSpecifications {
    private JobSpecifications() {}

    public static Specification<Job> notDeleted() {
        return (root, query, cb) -> cb.isNull(root.get("deletedAt"));
    }

    public static Specification<Job> keywordContains(String keyword) {
        return (root, query, cb) -> {
            if (keyword == null || keyword.isBlank()) {
                return cb.conjunction();
            }
            String like = "%" + keyword.toLowerCase() + "%";
            return cb.or(
                cb.like(cb.lower(root.get("title")), like),
                cb.like(cb.lower(root.get("companyName")), like),
                cb.like(cb.lower(root.get("description")), like)
            );
        };
    }

    public static Specification<Job> locationContains(String location) {
        return (root, query, cb) -> {
            if (location == null || location.isBlank()) {
                return cb.conjunction();
            }
            String like = "%" + location.toLowerCase() + "%";
            return cb.like(cb.lower(root.get("location")), like);
        };
    }

    public static Specification<Job> industryEquals(String industryId) {
        return (root, query, cb) -> {
            if (industryId == null || industryId.isBlank()) {
                return cb.conjunction();
            }
            return cb.equal(root.get("industryId"), industryId);
        };
    }

    public static Specification<Job> fieldEquals(String fieldId) {
        return (root, query, cb) -> {
            if (fieldId == null || fieldId.isBlank()) {
                return cb.conjunction();
            }
            return cb.equal(root.get("fieldId"), fieldId);
        };
    }

    public static Specification<Job> jobTypeEquals(JobType jobType) {
        return (root, query, cb) -> {
            if (jobType == null) {
                return cb.conjunction();
            }
            return cb.equal(root.get("jobType"), jobType);
        };
    }

    public static Specification<Job> statusEquals(StatusJob status) {
        return (root, query, cb) -> {
            if (status == null) {
                return cb.conjunction();
            }
            return cb.equal(root.get("status"), status);
        };
    }

    public static Specification<Job> experienceMin(Integer min) {
        return (root, query, cb) -> {
            if (min == null) {
                return cb.conjunction();
            }
            return cb.greaterThanOrEqualTo(root.get("experienceRequired"), min);
        };
    }

    public static Specification<Job> experienceMax(Integer max) {
        return (root, query, cb) -> {
            if (max == null) {
                return cb.conjunction();
            }
            return cb.lessThanOrEqualTo(root.get("experienceRequired"), max);
        };
    }

    public static Specification<Job> salaryMin(Double min) {
        return (root, query, cb) -> {
            if (min == null) {
                return cb.conjunction();
            }
            return cb.greaterThanOrEqualTo(root.get("salaryMax"), min);
        };
    }

    public static Specification<Job> salaryMax(Double max) {
        return (root, query, cb) -> {
            if (max == null) {
                return cb.conjunction();
            }
            return cb.lessThanOrEqualTo(root.get("salaryMin"), max);
        };
    }
}
