package iuh.fit.jobservice.service.impl;

import iuh.fit.jobservice.dto.JobFilterOptions;
import iuh.fit.jobservice.dto.JobStats;
import iuh.fit.jobservice.dto.request.CreateJobRequest;
import iuh.fit.jobservice.dto.request.UpdateJobRequest;
import iuh.fit.jobservice.dto.response.JobResponse;
import iuh.fit.jobservice.dto.response.JobStatsResponse;
import iuh.fit.jobservice.dto.response.PageResponse;
import iuh.fit.jobservice.exception.JobStatusException;
import iuh.fit.jobservice.mapper.JobMapper;
import iuh.fit.jobservice.model.Job;
import iuh.fit.jobservice.model.JobType;
import iuh.fit.jobservice.model.StatusJob;
import iuh.fit.jobservice.repository.JobRepository;
import iuh.fit.jobservice.service.JobService;
import iuh.fit.jobservice.service.JobStatusTransition;
import iuh.fit.jobservice.service.JobStatusTransition.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Normalizer;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
public class JobServiceImpl implements JobService {

	private final JobRepository jobRepository;

	public JobServiceImpl(JobRepository jobRepository) {
		this.jobRepository = jobRepository;
	}

	@Override
	@Transactional
	public JobResponse createJob(String employerId, CreateJobRequest request) {
		validateEmployerId(employerId);
		validateCreateRequest(request);

		LocalDateTime now = LocalDateTime.now();

		Job job = new Job();
		job.setJobId(UUID.randomUUID().toString());
		job.setEmployerId(employerId);
		job.setCompanyId(resolveCompanyIdByEmployerId(employerId));
		job.setTitle(request.getTitle().trim());
		job.setIndustry(normalize(request.getIndustry()));
		job.setLocation(normalize(request.getAddress()));
		job.setJobType(parseJobType(request.getJobType()));
		job.setExperience(normalizeOrDefault(request.getExperience(), "No experience required"));

		job.setSalaryMin(request.getSalaryMin() != null ? request.getSalaryMin() : 0D);
		job.setSalaryMax(request.getSalaryMax() != null ? request.getSalaryMax() : 0D);
		job.setSalaryNegotiable(request.isSalaryNegotiable());

		job.setDeadline(parseDeadline(request.getDeadline()));

		job.setRank(normalize(request.getRank()));
		job.setEducation(normalize(request.getEducation()));
		job.setQuantity(request.getQuantity() != null ? request.getQuantity() : 1);
		job.setAgeRange(normalize(request.getAgeRange()));

		job.setRequirementTags(JobMapper.toJson(request.getRequirementTags()));
		job.setBenefitTags(JobMapper.toJson(request.getBenefitTags()));
		job.setSpecialties(JobMapper.toJson(request.getSpecialties()));

		job.setDescription(normalize(request.getDescription()));
		job.setCandidateRequirements(normalize(request.getCandidateRequirements()));
		job.setSalaryDetail(normalize(request.getSalaryDetail()));
		job.setBenefitsDetail(normalize(request.getBenefitsDetail()));
		job.setWorkSchedule(normalize(request.getWorkSchedule()));

		job.setRelatedCategories(JobMapper.toJson(request.getRelatedCategories()));
		job.setSkills(JobMapper.toJson(request.getSkills()));

		job.setCreatedAt(now);
		job.setUpdatedAt(now);
		job.setViews(0);
		job.setNumberOfApplications(0);
		job.setTop(false);
		job.setStatus(request.isSaveAsDraft() ? StatusJob.DRAFT : StatusJob.ACTIVE);

		Job saved = jobRepository.save(job);
		return JobMapper.toResponse(saved);
	}

	@Override
	@Transactional
	public JobResponse updateJob(String employerId, String jobId, UpdateJobRequest request) {
		validateEmployerId(employerId);
		Job job = getJobOrThrow(jobId);
		ensureOwner(employerId, job);

		if (request.getTitle() != null && !request.getTitle().isBlank()) {
			job.setTitle(request.getTitle().trim());
		}
		if (request.getIndustry() != null) {
			job.setIndustry(normalize(request.getIndustry()));
		}
		if (request.getAddress() != null) {
			job.setLocation(normalize(request.getAddress()));
		}
		if (request.getJobType() != null && !request.getJobType().isBlank()) {
			job.setJobType(parseJobType(request.getJobType()));
		}
		if (request.getExperience() != null) {
			job.setExperience(normalizeOrDefault(request.getExperience(), "No experience required"));
		}

		if (request.getSalaryMin() != null) {
			job.setSalaryMin(request.getSalaryMin());
		}
		if (request.getSalaryMax() != null) {
			job.setSalaryMax(request.getSalaryMax());
		}
		if (request.getSalaryNegotiable() != null) {
			job.setSalaryNegotiable(request.getSalaryNegotiable());
		}

		if (request.getDeadline() != null && !request.getDeadline().isBlank()) {
			job.setDeadline(parseDeadline(request.getDeadline()));
		}

		if (request.getRank() != null) {
			job.setRank(normalize(request.getRank()));
		}
		if (request.getEducation() != null) {
			job.setEducation(normalize(request.getEducation()));
		}
		if (request.getQuantity() != null) {
			job.setQuantity(request.getQuantity());
		}
		if (request.getAgeRange() != null) {
			job.setAgeRange(normalize(request.getAgeRange()));
		}

		if (request.getRequirementTags() != null) {
			job.setRequirementTags(JobMapper.toJson(request.getRequirementTags()));
		}
		if (request.getBenefitTags() != null) {
			job.setBenefitTags(JobMapper.toJson(request.getBenefitTags()));
		}
		if (request.getSpecialties() != null) {
			job.setSpecialties(JobMapper.toJson(request.getSpecialties()));
		}

		if (request.getDescription() != null) {
			job.setDescription(normalize(request.getDescription()));
		}
		if (request.getCandidateRequirements() != null) {
			job.setCandidateRequirements(normalize(request.getCandidateRequirements()));
		}
		if (request.getSalaryDetail() != null) {
			job.setSalaryDetail(normalize(request.getSalaryDetail()));
		}
		if (request.getBenefitsDetail() != null) {
			job.setBenefitsDetail(normalize(request.getBenefitsDetail()));
		}
		if (request.getWorkSchedule() != null) {
			job.setWorkSchedule(normalize(request.getWorkSchedule()));
		}

		if (request.getRelatedCategories() != null) {
			job.setRelatedCategories(JobMapper.toJson(request.getRelatedCategories()));
		}
		if (request.getSkills() != null) {
			job.setSkills(JobMapper.toJson(request.getSkills()));
		}

		job.setUpdatedAt(LocalDateTime.now());

		Job saved = jobRepository.save(job);
		return JobMapper.toResponse(saved);
	}

	@Override
	@Transactional
	public void deleteJob(String employerId, String jobId) {
		validateEmployerId(employerId);
		Job job = getJobOrThrow(jobId);
		ensureOwner(employerId, job);
		// xóa mềm: chỉ đánh dấu deletedAt, giữ nguyên dữ liệu
		job.setDeletedAt(LocalDateTime.now());
		jobRepository.save(job);
	}

	@Override
	public PageResponse<JobResponse> getMyJobs(String employerId, String search, String status, int page, int size) {
		validateEmployerId(employerId);
		Pageable pageable = PageRequest.of(safePage(page), safeSize(size));

		String normalizedStatus = normalizeStatusFilter(status);
		String normalizedSearch = normalizeForQuery(search);

		Page<Job> jobsPage = jobRepository.findByEmployerWithFilter(
				employerId,
				normalizedStatus,
				normalizedSearch,
				pageable
		);

		return PageResponse.<JobResponse>builder()
				.content(jobsPage.getContent().stream().map(JobMapper::toResponse).toList())
				.page(jobsPage.getNumber() + 1)
				.size(jobsPage.getSize())
				.totalElements(jobsPage.getTotalElements())
				.totalPages(jobsPage.getTotalPages())
				.build();
	}

	@Override
	public JobStatsResponse getMyStats(String employerId) {
		validateEmployerId(employerId);

		long active = jobRepository.countByEmployerIdAndStatus(employerId, StatusJob.ACTIVE);
		long paused = jobRepository.countByEmployerIdAndStatus(employerId, StatusJob.PAUSED);
		long closed = jobRepository.countByEmployerIdAndStatus(employerId, StatusJob.CLOSED);
		int totalApplicants = jobRepository.sumApplicationsByEmployerId(employerId);

		return JobStatsResponse.builder()
				.active(active)
				.paused(paused)
				.closed(closed)
				.totalApplicants(totalApplicants)
				.build();
	}

	@Override
	@Transactional
	public JobResponse pushToTop(String employerId, String jobId) {
		validateEmployerId(employerId);
		Job job = getJobOrThrow(jobId);
		ensureOwner(employerId, job);

		job.setTop(true);
		job.setUpdatedAt(LocalDateTime.now());

		Job saved = jobRepository.save(job);
		return JobMapper.toResponse(saved);
	}

	// ── EMPLOYER: chuyển trạng thái ──────────────────────────────────────────────
	@Override
	@Transactional
	public JobResponse employerChangeStatus(String employerId, String jobId, String newStatus) {
		validateEmployerId(employerId);
		Job job = getJobOrThrow(jobId);
		ensureOwner(employerId, job);

		StatusJob requested = parseStatus(newStatus);
		StatusJob updated = JobStatusTransition.transition(Role.EMPLOYER, job.getStatus(), requested);

		job.setStatus(updated);
		job.setUpdatedAt(LocalDateTime.now());
		return JobMapper.toResponse(jobRepository.save(job));
	}

	// ── ADMIN: duyệt / từ chối ────────────────────────────────────────────────
	@Override
	@Transactional
	public JobResponse adminChangeStatus(String adminId, String jobId, String newStatus) {
		if (adminId == null || adminId.isBlank()) {
			throw new JobStatusException("Admin id is required");
		}
		Job job = getJobOrThrow(jobId);

		StatusJob requested = parseStatus(newStatus);
		StatusJob updated = JobStatusTransition.transition(Role.ADMIN, job.getStatus(), requested);

		job.setStatus(updated);
		job.setUpdatedAt(LocalDateTime.now());
		return JobMapper.toResponse(jobRepository.save(job));
	}

	// ── SYSTEM: tự động expire khi hết hạn ───────────────────────────────────
	@Override
	@Scheduled(cron = "0 0 1 * * *")   // mỗi ngày 01:00
	@Transactional
	public int expireOverdueJobs() {
		LocalDate today = LocalDate.now();
		List<Job> overdueJobs = jobRepository.findByStatusAndDeadlineBefore(
				StatusJob.ACTIVE, today);

		for (Job job : overdueJobs) {
			job.setStatus(StatusJob.EXPIRED);
			job.setUpdatedAt(LocalDateTime.now());
		}
		if (!overdueJobs.isEmpty()) {
			jobRepository.saveAll(overdueJobs);
		}
		return overdueJobs.size();
	}

	@Override
	@Transactional
	public JobResponse getJobById(String jobId) {
		Job job = getJobOrThrow(jobId);
		job.setViews(job.getViews() + 1);
		job.setUpdatedAt(LocalDateTime.now());
		Job saved = jobRepository.save(job);
		return JobMapper.toResponse(saved);
	}

	@Override
	public PageResponse<JobResponse> searchJobs(String search, String industry, String jobType, String location, int page, int size) {
		Pageable pageable = PageRequest.of(safePage(page), safeSize(size));

		String normalizedSearch = normalizeForQuery(search);
		String normalizedIndustry = normalizeForQuery(industry);
		String normalizedJobType = normalizeJobTypeFilter(jobType);
		String normalizedLocation = normalizeForQuery(location);

		Page<Job> jobsPage = jobRepository.searchActiveJobs(
				normalizedSearch,
				normalizedIndustry,
				normalizedJobType,
				normalizedLocation,
				pageable
		);

		return PageResponse.<JobResponse>builder()
				.content(jobsPage.getContent().stream().map(JobMapper::toResponse).toList())
				.page(jobsPage.getNumber() + 1)
				.size(jobsPage.getSize())
				.totalElements(jobsPage.getTotalElements())
				.totalPages(jobsPage.getTotalPages())
				.build();
	}

	private Job getJobOrThrow(String jobId) {
		return jobRepository.findActiveById(jobId)
				.orElseThrow(() -> new RuntimeException("Job not found"));
	}

	private void ensureOwner(String employerId, Job job) {
		if (!employerId.equals(job.getEmployerId())) {
			throw new RuntimeException("You are not authorized to manage this job");
		}
	}

	private void validateEmployerId(String employerId) {
		if (employerId == null || employerId.isBlank()) {
			throw new RuntimeException("Employer id is required");
		}
	}

	private void validateCreateRequest(CreateJobRequest request) {
		if (request == null) {
			throw new RuntimeException("Request body is required");
		}
		if (request.getTitle() == null || request.getTitle().isBlank()) {
			throw new RuntimeException("Title is required");
		}
		if (request.getJobType() == null || request.getJobType().isBlank()) {
			throw new RuntimeException("Job type is required");
		}
	}

	private StatusJob parseStatus(String status) {
		if (status == null || status.isBlank()) {
			throw new RuntimeException("Status is required");
		}
		try {
			return StatusJob.valueOf(status.trim().toUpperCase(Locale.ROOT));
		} catch (IllegalArgumentException e) {
			throw new RuntimeException("Invalid status: " + status);
		}
	}

	private JobType parseJobType(String jobType) {
		if (jobType == null || jobType.isBlank()) {
			throw new RuntimeException("Job type is required");
		}

		String raw = jobType.trim();
		String enumLike = raw.toUpperCase(Locale.ROOT)
				.replace('-', '_')
				.replace(' ', '_');

		switch (enumLike) {
			case "FULL_TIME":
				return JobType.FULL_TIME;
			case "PART_TIME":
				return JobType.PART_TIME;
			case "INTERNSHIP":
				return JobType.INTERNSHIP;
			case "FREELANCE":
				return JobType.FREELANCE;
			case "REMOTE":
				return JobType.REMOTE;
			default:
				break;
		}

		String vnKey = Normalizer.normalize(raw, Normalizer.Form.NFD)
				.replaceAll("\\p{M}+", "")
				.toLowerCase(Locale.ROOT)
				.trim();

		switch (vnKey) {
			case "toan thoi gian":
				return JobType.FULL_TIME;
			case "ban thoi gian":
				return JobType.PART_TIME;
			case "thuc tap":
				return JobType.INTERNSHIP;
			default:
				throw new RuntimeException("Invalid job type: " + jobType);
		}
	}

	private LocalDate parseDeadline(String deadline) {
		if (deadline == null || deadline.isBlank()) {
			return null;
		}

		String raw = deadline.trim();
		try {
			return LocalDate.parse(raw);
		} catch (DateTimeParseException e) {
			try {
				return LocalDate.parse(raw, DateTimeFormatter.ofPattern("d/M/yyyy"));
			} catch (DateTimeParseException ex) {
				throw new RuntimeException("Invalid deadline format, expected yyyy-MM-dd");
			}
		}
	}

	private String normalize(String value) {
		if (value == null) {
			return null;
		}
		String trimmed = value.trim();
		return trimmed.isEmpty() ? null : trimmed;
	}

	private String normalizeForQuery(String value) {
		String normalized = normalize(value);
		return normalized == null ? null : normalized;
	}

	private String resolveCompanyIdByEmployerId(String employerId) {
		String normalizedEmployerId = employerId.trim().toUpperCase(Locale.ROOT);
		if (normalizedEmployerId.startsWith("COMP")) {
			return normalizedEmployerId;
		}
		if (normalizedEmployerId.startsWith("EMP") && normalizedEmployerId.length() > 3) {
			return "COMP" + normalizedEmployerId.substring(3);
		}
		return "COMP" + normalizedEmployerId;
	}

	private String normalizeOrDefault(String value, String defaultValue) {
		String normalized = normalize(value);
		return normalized == null ? defaultValue : normalized;
	}

	private String normalizeStatusFilter(String status) {
		if (status == null || status.isBlank() || "all".equalsIgnoreCase(status.trim())) {
			return null;
		}
		return parseStatus(status).name();
	}

	private String normalizeJobTypeFilter(String jobType) {
		if (jobType == null || jobType.isBlank()) {
			return null;
		}
		return parseJobType(jobType).name();
	}

	private int safePage(int page) {
		return Math.max(page, 1) - 1;
	}

	private int safeSize(int size) {
		return size <= 0 ? 10 : Math.min(size, 100);
	}

	@Override
	public JobFilterOptions getFilterOptions() {
		// nếu bạn chưa có IndustryRepository thì tạm thời trả rỗng
		return new JobFilterOptions(
				List.of(JobType.values()),
				List.of(StatusJob.values()),
				jobRepository.findDistinctLocations(),
				List.of() // industries (có thể thêm sau)
		);
	}

	@Override
	public JobStats getStats() {
		long totalJobs = jobRepository.count();
		long activeJobs = jobRepository.countByStatus(StatusJob.ACTIVE);
		long newJobs24h = jobRepository.countByCreatedAtAfter(LocalDateTime.now().minusHours(24));

		return new JobStats(totalJobs, activeJobs, newJobs24h);
	}
}
