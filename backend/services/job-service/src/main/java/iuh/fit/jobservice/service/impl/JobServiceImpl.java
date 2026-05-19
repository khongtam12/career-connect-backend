package iuh.fit.jobservice.service.impl;

import iuh.fit.jobservice.client.CompanyServiceClient;
import iuh.fit.jobservice.client.UserServiceClient;
import iuh.fit.jobservice.dto.CompanyDTO;
import iuh.fit.jobservice.dto.CompanyMarketingAssignmentDTO;
import iuh.fit.jobservice.dto.CompanyMarketingAssignmentRequest;
import iuh.fit.jobservice.dto.CompanySubscriptionDTO;
import iuh.fit.jobservice.dto.IndustryDTO;
import iuh.fit.jobservice.dto.IndustrySummary;
import iuh.fit.jobservice.dto.JobFilterOptions;
import iuh.fit.jobservice.dto.JobStats;
import iuh.fit.jobservice.dto.request.ApplyMarketingPackageRequest;
import iuh.fit.jobservice.dto.request.CreateJobRequest;
import iuh.fit.jobservice.dto.request.RenewJobRequest;
import iuh.fit.jobservice.dto.request.UpdateJobRequest;
import iuh.fit.jobservice.dto.response.*;
import iuh.fit.jobservice.exception.JobStatusException;
import iuh.fit.jobservice.mapper.JobMapper;
import iuh.fit.jobservice.model.Industry;
import iuh.fit.jobservice.model.Job;
import iuh.fit.jobservice.model.JobType;
import iuh.fit.jobservice.model.StatusJob;
import iuh.fit.jobservice.repository.IndustryRepository;
import iuh.fit.jobservice.repository.JobRepository;
import iuh.fit.jobservice.service.JobService;
import iuh.fit.jobservice.service.JobStatusTransition;
import iuh.fit.jobservice.service.JobStatusTransition.Role;
import iuh.fit.jobservice.specification.JobSpecifications;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.domain.Specification;

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

	private static final Logger log = LoggerFactory.getLogger(JobServiceImpl.class);
	private static final String JOB_SEARCH_CACHE = "job-search";
	private static final long ADMIN_APPROVAL_GRACE_HOURS = 24;

	private final JobRepository jobRepository;
	private final IndustryRepository industryRepository;
	private final CompanyServiceClient companyServiceClient;
	private final UserServiceClient userServiceClient;
	private final CacheManager cacheManager;

	public JobServiceImpl(
			JobRepository jobRepository,
			IndustryRepository industryRepository,
			CompanyServiceClient companyServiceClient,
			UserServiceClient userServiceClient,
			CacheManager cacheManager) {
		this.jobRepository = jobRepository;
		this.industryRepository = industryRepository;
		this.companyServiceClient = companyServiceClient;
		this.userServiceClient = userServiceClient;
		this.cacheManager = cacheManager;
	}

	@Override
	@Transactional
	public void incrementApplications(String jobId) {
		if (jobId == null || jobId.isBlank()) {
			throw new IllegalArgumentException("jobId is required");
		}
		int updated = jobRepository.incrementApplications(jobId);
		if (updated == 0) {
			throw new RuntimeException("Job not found");
		}
	}

	@Override
	@Transactional
	@CacheEvict(cacheNames = "job-search", allEntries = true)
	public JobResponse createJob(String employerId, CreateJobRequest request) {
		validateEmployerId(employerId);

		// Kiểm tra công ty đã được phê duyệt chưa
		String companyIdForCheck = fetchCompanyIdByEmployerId(employerId);
		CompanyDTO company = companyServiceClient.getCompanyById(companyIdForCheck);
		if (company == null || !"VERIFIED".equalsIgnoreCase(company.getStatusCompany())) {
			throw new RuntimeException(
					"Công ty của bạn chưa được phê duyệt. Vui lòng chờ admin xác minh công ty trước khi đăng tin tuyển dụng.");
		}

		LocalDateTime now = LocalDateTime.now();
		String companyId = companyIdForCheck;
		CompanySubscriptionDTO subscription = null;

		if (!request.isSaveAsDraft()) {
			String subscriptionId = normalize(request.getCompanySubscriptionId());
			if (subscriptionId == null) {
				throw new RuntimeException("Company subscription is required");
			}
			subscription = companyServiceClient.getSubscriptionById(subscriptionId);
			validateSubscription(subscription, companyId);
			long pendingCount = jobRepository.countByCompanySubscriptionIdAndStatus(subscriptionId, StatusJob.PENDING);
			if ((long) subscription.getJobPostedCount() + pendingCount >= subscription.getJobPostLimit()) {
				throw new RuntimeException("Subscription job post limit reached");
			}
		}

		Job job = new Job();
		job.setJobId(UUID.randomUUID().toString());
		job.setEmployerId(employerId);
		job.setCompanyId(companyId);
		job.setCompanyName(company.getName());
		job.setCompanyLogoUrl(company.getLogo());
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
		job.setStatus(request.isSaveAsDraft() ? StatusJob.DRAFT : StatusJob.PENDING);
		if (subscription != null) {
			job.setCompanySubscriptionId(subscription.getId());
			job.setPackageId(subscription.getPackageId());
			job.setPackageLabel(subscription.getPackageLabel());
		}

		Job saved = jobRepository.save(job);
		return JobMapper.toResponse(saved);
	}

	@Override
	@Transactional
	@CacheEvict(cacheNames = "job-search", allEntries = true)
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
			LocalDate newDeadline = parseDeadline(request.getDeadline());
			job.setDeadline(newDeadline);
			if (job.getStatus() == StatusJob.EXPIRED && newDeadline.isAfter(LocalDate.now())) {
				job.setStatus(StatusJob.ACTIVE);
			}
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

		String newSubscriptionId = normalize(request.getCompanySubscriptionId());
		if (newSubscriptionId != null) {
			StatusJob currentStatus = job.getStatus();
			if (currentStatus != StatusJob.DRAFT && currentStatus != StatusJob.PENDING
					&& currentStatus != StatusJob.REJECTED) {
				throw new JobStatusException("Only DRAFT, PENDING, or REJECTED jobs can change subscription");
			}
			String currentSubscriptionId = normalize(job.getCompanySubscriptionId());
			if (!newSubscriptionId.equals(currentSubscriptionId)) {
				String companyId = fetchCompanyIdByEmployerId(employerId);
				CompanySubscriptionDTO subscription = companyServiceClient.getSubscriptionById(newSubscriptionId);
				validateSubscription(subscription, companyId);
				if (currentStatus == StatusJob.PENDING) {
					long pendingCount = jobRepository.countByCompanySubscriptionIdAndStatus(newSubscriptionId,
							StatusJob.PENDING);
					if ((long) subscription.getJobPostedCount() + pendingCount >= subscription.getJobPostLimit()) {
						throw new RuntimeException("Subscription job post limit reached");
					}
				}
				job.setCompanySubscriptionId(subscription.getId());
				job.setPackageId(subscription.getPackageId());
				job.setPackageLabel(subscription.getPackageLabel());
			}
		}

		job.setUpdatedAt(LocalDateTime.now());

		Job saved = jobRepository.save(job);
		return JobMapper.toResponse(saved);
	}

	@Override
	@Transactional
	@CacheEvict(cacheNames = "job-search", allEntries = true)
	public void deleteJob(String employerId, String jobId) {
		validateEmployerId(employerId);
		Job job = getJobOrThrow(jobId);
		ensureOwner(employerId, job);
		if (job.getNumberOfApplications() > 0) {
			throw new RuntimeException("Cannot delete job with applicants");
		}
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
				pageable);

		return PageResponse.<JobResponse>builder()
				.content(mapJobsToResponseWithCompanyData(jobsPage.getContent()))
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
	@CacheEvict(cacheNames = "job-search", allEntries = true)
	public JobResponse renewJob(String employerId, String jobId, RenewJobRequest request) {
		validateEmployerId(employerId);
		Job job = getJobOrThrow(jobId);
		ensureOwner(employerId, job);

		StatusJob currentStatus = job.getStatus();
		if (currentStatus == StatusJob.DRAFT || currentStatus == StatusJob.PENDING
				|| currentStatus == StatusJob.REJECTED) {
			throw new IllegalArgumentException(
					"Không thể gia hạn tin tuyển dụng chưa được duyệt (Trạng thái hiện tại: " + currentStatus + ").");
		}

		String newSubscriptionId = normalize(request.getCompanySubscriptionId());
		if (newSubscriptionId == null) {
			throw new RuntimeException("Company subscription is required to renew job");
		}

		String companyId = fetchCompanyIdByEmployerId(employerId);
		CompanySubscriptionDTO subscription = companyServiceClient.getSubscriptionById(newSubscriptionId);
		validateSubscription(subscription, companyId);

		CompanySubscriptionDTO consumed = companyServiceClient.consumeSubscription(newSubscriptionId);
		if (consumed == null) {
			throw new RuntimeException("Failed to consume subscription");
		}

		job.setCompanySubscriptionId(consumed.getId());
		job.setPackageId(consumed.getPackageId());
		job.setPackageLabel(consumed.getPackageLabel());

		if (request.getDeadline() != null && !request.getDeadline().isBlank()) {
			job.setDeadline(parseDeadline(request.getDeadline()));
		}

		job.setStatus(StatusJob.ACTIVE);
		job.setUpdatedAt(LocalDateTime.now());

		Job saved = jobRepository.save(job);
		return JobMapper.toResponse(saved);
	}

	// ── EMPLOYER: chuyển trạng thái ──────────────────────────────────────────────
	@Override
	@Transactional
	@CacheEvict(cacheNames = "job-search", allEntries = true)
	public JobResponse employerChangeStatus(String employerId, String jobId, String newStatus) {
		validateEmployerId(employerId);
		Job job = getJobOrThrow(jobId);
		ensureOwner(employerId, job);

		StatusJob requested = parseStatus(newStatus);
		StatusJob updated = JobStatusTransition.transition(Role.EMPLOYER, job.getStatus(), requested);
		if (updated == StatusJob.PENDING) {
			String subscriptionId = normalize(job.getCompanySubscriptionId());
			if (subscriptionId == null) {
				throw new JobStatusException("Company subscription is required to submit for approval");
			}
			String companyId = fetchCompanyIdByEmployerId(employerId);
			CompanySubscriptionDTO subscription = companyServiceClient.getSubscriptionById(subscriptionId);
			validateSubscription(subscription, companyId);
			long pendingCount = jobRepository.countByCompanySubscriptionIdAndStatus(subscriptionId, StatusJob.PENDING);
			if ((long) subscription.getJobPostedCount() + pendingCount >= subscription.getJobPostLimit()) {
				throw new RuntimeException("Subscription job post limit reached");
			}
			if (job.getPackageLabel() == null) {
				job.setPackageId(subscription.getPackageId());
				job.setPackageLabel(subscription.getPackageLabel());
			}
		}

		job.setStatus(updated);
		job.setUpdatedAt(LocalDateTime.now());
		return JobMapper.toResponse(jobRepository.save(job));
	}

	// ── ADMIN: duyệt / từ chối ────────────────────────────────────────────────
	@Override
	@Transactional
	@CacheEvict(cacheNames = "job-search", allEntries = true)
	public JobResponse adminChangeStatus(String adminId, String jobId, String newStatus) {
		if (adminId == null || adminId.isBlank()) {
			throw new JobStatusException("Admin id is required");
		}
		Job job = getJobOrThrow(jobId);

		StatusJob requested = parseStatus(newStatus);
		StatusJob current = job.getStatus();
		StatusJob updated = JobStatusTransition.transition(Role.ADMIN, current, requested);

		if (current == StatusJob.PENDING && updated == StatusJob.ACTIVE) {
			validateAdminApprovalWindow(job);
			String subscriptionId = normalize(job.getCompanySubscriptionId());
			if (subscriptionId == null) {
				throw new JobStatusException("Company subscription is required to approve job");
			}
			CompanySubscriptionDTO consumed = companyServiceClient.consumeSubscription(subscriptionId);
			if (job.getPackageLabel() == null && consumed != null) {
				job.setPackageLabel(consumed.getPackageLabel());
			}
		}

		job.setStatus(updated);
		job.setUpdatedAt(LocalDateTime.now());
		return JobMapper.toResponse(jobRepository.save(job));
	}

	// ── SYSTEM: tự động expire khi hết hạn ───────────────────────────────────
	@Override
	@Scheduled(cron = "0 0 1 * * *") // mỗi ngày 01:00
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

	// ── SYSTEM: tự động đóng job khi gói tin hết hạn ──────────────────────────
	@Scheduled(cron = "0 30 1 * * *") // mỗi ngày 01:30
	@Transactional
	public int closeJobsByExpiredSubscriptions() {
		List<String> expiredSubscriptionIds = companyServiceClient.expireSubscriptions();
		if (expiredSubscriptionIds == null || expiredSubscriptionIds.isEmpty()) {
			return 0;
		}

		List<StatusJob> closableStatuses = List.of(StatusJob.ACTIVE, StatusJob.PENDING, StatusJob.PAUSED);
		List<Job> jobs = jobRepository.findByCompanySubscriptionIdInAndStatusIn(expiredSubscriptionIds,
				closableStatuses);
		if (jobs.isEmpty()) {
			return 0;
		}

		LocalDateTime now = LocalDateTime.now();
		jobs.forEach(job -> {
			job.setStatus(StatusJob.CLOSED);
			job.setUpdatedAt(now);
		});
		jobRepository.saveAll(jobs);
		return jobs.size();
	}

	@Override
	@Transactional
	public JobDetailResponse getJobDetail(String jobId) {
		// Tăng lượt xem trước, sau đó đọc lại để lấy số lượt xem mới
		jobRepository.incrementViews(jobId);
		Job job = jobRepository.findById(jobId)
				.orElseThrow(() -> new RuntimeException("Không tìm thấy công việc: " + jobId));
		IndustryDTO industryDTO = null;
		Industry industry = null;
		if (job.getIndustry() != null) {
			industry = industryRepository.findById(job.getIndustryId()).orElse(null);
		}
		if (industry != null) {
			industryDTO = new IndustryDTO(
					industry.getIndustryId(), industry.getName(), industry.getDescription());
		}
		CompanyDTO companyDTO = null;
		if (job.getCompanyId() != null) {
			try {
				companyDTO = companyServiceClient.getCompanyById(job.getCompanyId());
			} catch (Exception e) {
				System.err.println("Lỗi gọi company-service: " + e.getMessage());
			}
		}
		return new JobDetailResponse(job.getJobId(), job.getTitle(), job.getDescription(),
				job.getCandidateRequirements(),
				job.getSalaryDetail(), job.getBenefitsDetail(), job.getWorkSchedule(), job.getLocation(),
				job.getSalaryMin(), job.getSalaryMax(), job.isSalaryNegotiable(), job.getExperience(),
				job.getDeadline(), job.getCreatedAt(), job.getUpdatedAt(), job.getViews(),
				job.getNumberOfApplications(),
				job.isTop(), job.getPackageId(), job.getPackageLabel(), job.getDeletedAt(), job.getRank(),
				job.getEducation(), job.getQuantity(), job.getAgeRange(),
				job.getIndustry(), job.getStatus(), job.getJobType(), job.getRequirementTags(), job.getBenefitTags(),
				job.getSpecialties(), job.getRelatedCategories(), job.getSkills(), companyDTO, industryDTO);

	}

	@Override
	public PageResponse<JobCardResponse> searchJobs(
			String keyword,
			String industryId,
			String jobType,
			String location,
			String status,
			Integer experienceMin,
			Integer experienceMax,
			Double salaryMin,
			Double salaryMax,
			String sortBy,
			String sortDir,
			int page,
			int size) {
		String cacheKey = buildSearchCacheKey(
				keyword,
				industryId,
				jobType,
				location,
				status,
				experienceMin,
				experienceMax,
				salaryMin,
				salaryMax,
				sortBy,
				sortDir,
				page,
				size);

		Cache cache = cacheManager.getCache(JOB_SEARCH_CACHE);
		if (cache != null) {
			Cache.ValueWrapper wrapper = cache.get(cacheKey);
			if (wrapper != null) {
				Object cached = wrapper.get();
				if (cached instanceof PageResponse) {
					log.info("Job search cache HIT: key={}", cacheKey);
					@SuppressWarnings("unchecked")
					PageResponse<JobCardResponse> cachedResponse = (PageResponse<JobCardResponse>) cached;
					return cachedResponse;
				}
			}
		}

		Pageable pageable = PageRequest.of(
				safePage(page),
				safeSize(size),
				buildSort(sortBy, sortDir));

		String normalizedKeyword = normalizeForQuery(keyword);
		String normalizedLocation = normalizeForQuery(location);

		Specification<Job> spec = Specification.where(JobSpecifications.notDeleted());

		StatusJob statusFilter = normalizeStatusFilter(status) != null
				? parseStatus(status)
				: StatusJob.ACTIVE;
		spec = spec.and(JobSpecifications.statusEquals(statusFilter));

		if (normalizedKeyword != null) {
			spec = spec.and(JobSpecifications.keywordContains(normalizedKeyword));
		}
		if (normalizedLocation != null) {
			spec = spec.and(JobSpecifications.locationContains(normalizedLocation));
		}
		if (industryId != null && !industryId.isBlank()) {
			spec = spec.and(JobSpecifications.industryEquals(industryId.trim()));
		}
		if (jobType != null && !jobType.isBlank()) {
			spec = spec.and(JobSpecifications.jobTypeEquals(parseJobType(jobType)));
		}
		if (salaryMin != null) {
			spec = spec.and(JobSpecifications.salaryMin(salaryMin));
		}
		if (salaryMax != null) {
			spec = spec.and(JobSpecifications.salaryMax(salaryMax));
		}

		Page<Job> jobsPage = jobRepository.findAll(spec, pageable);

		PageResponse<JobCardResponse> response = PageResponse.<JobCardResponse>builder()
				.content(mapJobsToCardResponseWithCompanyData(jobsPage.getContent()))
				.page(jobsPage.getNumber() + 1)
				.size(jobsPage.getSize())
				.totalElements(jobsPage.getTotalElements())
				.totalPages(jobsPage.getTotalPages())
				.build();

		if (cache != null) {
			cache.put(cacheKey, response);
			log.info("Job search cache MISS: key={}", cacheKey);
		}

		return response;
	}

	@Override
	@Transactional
	@CacheEvict(cacheNames = "job-search", allEntries = true)
	public JobResponse applyMarketingPackage(String employerId, String jobId, ApplyMarketingPackageRequest request) {
		validateEmployerId(employerId);
		if (request == null || request.getEntitlementId() == null || request.getEntitlementId().isBlank()) {
			throw new RuntimeException("Marketing entitlement is required");
		}

		Job job = getJobOrThrow(jobId);
		ensureOwner(employerId, job);
		String companyId = fetchCompanyIdByEmployerId(employerId);
		if (!companyId.equals(job.getCompanyId())) {
			throw new RuntimeException("Job does not belong to employer company");
		}
		if (job.getMarketingAssignmentId() != null && !job.getMarketingAssignmentId().isBlank()) {
			throw new RuntimeException("Job already has an active marketing package");
		}

		CompanyMarketingAssignmentDTO assignment = companyServiceClient.assignMarketingEntitlement(
				request.getEntitlementId(),
				CompanyMarketingAssignmentRequest.builder()
						.companyId(companyId)
						.targetId(jobId)
						.targetScope("JOB")
						.placement(request.getPlacement())
						.build());

		applyMarketingAssignment(job, assignment);
		job.setUpdatedAt(LocalDateTime.now());
		return JobMapper.toResponse(jobRepository.save(job));
	}

	@Override
	@Transactional
	@CacheEvict(cacheNames = "job-search", allEntries = true)
	public JobResponse removeMarketingPackage(String employerId, String jobId) {
		validateEmployerId(employerId);
		Job job = getJobOrThrow(jobId);
		ensureOwner(employerId, job);

		String assignmentId = normalize(job.getMarketingAssignmentId());
		if (assignmentId == null) {
			throw new RuntimeException("Job does not have an active marketing package");
		}

		String companyId = fetchCompanyIdByEmployerId(employerId);
		companyServiceClient.removeMarketingAssignment(assignmentId, companyId);

		clearMarketingAssignment(job);
		job.setUpdatedAt(LocalDateTime.now());
		return JobMapper.toResponse(jobRepository.save(job));
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
		return normalize(value);
	}

	private String fetchCompanyIdByEmployerId(String employerId) {
		EmployerCompanyResponse employerCompany = userServiceClient.getEmployerById(employerId);
		if (employerCompany == null || employerCompany.getCompanyId() == null
				|| employerCompany.getCompanyId().isBlank()) {
			throw new RuntimeException("Employer company not found");
		}
		return employerCompany.getCompanyId().trim();
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

	private void validateSubscription(CompanySubscriptionDTO subscription, String companyId) {
		if (subscription == null) {
			throw new RuntimeException("Subscription not found");
		}
		if (subscription.getCompanyId() == null || !subscription.getCompanyId().equals(companyId)) {
			throw new RuntimeException("Subscription does not belong to company");
		}
		if (subscription.getStatus() == null || !"ACTIVE".equalsIgnoreCase(subscription.getStatus())) {
			throw new IllegalArgumentException("Subscription is not active");
		}
		boolean isJobPostingPackage = "JOB_POSTING".equalsIgnoreCase(subscription.getPackageCategory())
				|| (subscription.getPackageCategory() == null
						&& subscription.getPackageId() != null
						&& subscription.getPackageId().toUpperCase(Locale.ROOT).startsWith("JP"));
		if (!isJobPostingPackage) {
			throw new IllegalArgumentException("Subscription is not valid for job posting");
		}
		if (subscription.getEndDate() != null && subscription.getEndDate().isBefore(LocalDateTime.now())) {
			throw new IllegalArgumentException("Gói tin này đã hết hạn sử dụng.");
		}
		if (subscription.getJobPostedCount() >= subscription.getJobPostLimit()) {
			throw new IllegalArgumentException("Gói tin này đã hết lượt đăng.");
		}
	}

	private void applyMarketingAssignment(Job job, CompanyMarketingAssignmentDTO assignment) {
		if (assignment == null) {
			throw new RuntimeException("Marketing assignment failed");
		}

		job.setMarketingAssignmentId(assignment.getId());
		job.setMarketingEntitlementId(assignment.getEntitlementId());
		job.setMarketingPackageCategory(assignment.getPackageCategory());
		job.setMarketingPackageType(assignment.getPackageType());
		job.setMarketingPackageLabel(assignment.getPackageLabel());

		if ("HIGHLIGHT".equalsIgnoreCase(assignment.getPackageCategory())) {
			job.setTop(true);
		}
	}

	private void clearMarketingAssignment(Job job) {
		boolean wasHighlight = "HIGHLIGHT".equalsIgnoreCase(job.getMarketingPackageCategory());
		job.setMarketingAssignmentId(null);
		job.setMarketingEntitlementId(null);
		job.setMarketingPackageCategory(null);
		job.setMarketingPackageType(null);
		job.setMarketingPackageLabel(null);
		if (wasHighlight) {
			job.setTop(false);
		}
	}

	private void validateAdminApprovalWindow(Job job) {
		LocalDateTime submittedAt = job.getUpdatedAt() != null ? job.getUpdatedAt() : job.getCreatedAt();
		if (submittedAt == null) {
			return;
		}

		LocalDateTime earliestApprovalTime = submittedAt.plusHours(ADMIN_APPROVAL_GRACE_HOURS);
		if (LocalDateTime.now().isBefore(earliestApprovalTime)) {
			String formattedTime = earliestApprovalTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
			throw new JobStatusException(
					"Tin đang trong thời gian chờ duyệt 24 giờ. Admin chỉ có thể duyệt sau " + formattedTime
							+ " để nhà tuyển dụng còn thời gian báo sai gói hoặc yêu cầu từ chối tin.");
		}
	}

	private int safePage(int page) {
		return Math.max(page, 1) - 1;
	}

	private int safeSize(int size) {
		return size <= 0 ? 10 : Math.min(size, 100);
	}

	@Override
	public JobFilterOptions getFilterOptions() {
		List<IndustrySummary> industries = industryRepository.findAll().stream()
				.map(industry -> new IndustrySummary(industry.getIndustryId(), industry.getName()))
				.toList();

		return new JobFilterOptions(
				List.of(JobType.values()),
				List.of(StatusJob.values()),
				jobRepository.findDistinctLocations(),
				industries);
	}

	private Sort buildSort(String sortBy, String sortDir) {
		Sort.Direction direction = "asc".equalsIgnoreCase(sortDir) ? Sort.Direction.ASC : Sort.Direction.DESC;
		String sortField;
		switch (sortBy != null ? sortBy.trim() : "") {
			case "salaryMax":
				sortField = "salaryMax";
				break;
			case "salaryMin":
				sortField = "salaryMin";
				break;
			case "deadline":
				sortField = "deadline";
				break;
			case "createdAt":
			default:
				sortField = "createdAt";
				break;
		}

		Sort sort = Sort.by(Sort.Order.desc("isTop"));
		sort = sort.and(Sort.by(new Sort.Order(direction, sortField)));
		if (!"createdAt".equals(sortField)) {
			sort = sort.and(Sort.by(Sort.Order.desc("createdAt")));
		}
		return sort;
	}

	@Override
	public JobStats getStats() {
		long totalJobs = jobRepository.count();
		long activeJobs = jobRepository.countByStatus(StatusJob.ACTIVE);
		long newJobs24h = jobRepository.countByCreatedAtAfter(LocalDateTime.now().minusHours(24));
		long pendingJobs = jobRepository.countByStatus(StatusJob.PENDING);
		long rejectedJobs = jobRepository.countByStatus(StatusJob.REJECTED);

		return new JobStats(totalJobs, activeJobs, newJobs24h, pendingJobs, rejectedJobs);
	}

	public PageResponse<JobAdminResponse> getAllJobByAdmin(String search, String status, int page, int size) {
		if (search == null || search.isEmpty()) {
			search = "*";
		}
		if (status == null) {
			status = "all";
		}
		Pageable pageable = PageRequest.of(safePage(page), safeSize(size), Sort.by("createdAt").descending());
		Page<Job> jobPage = jobRepository.findAll(
				JobSpecifications.filter(search, status), pageable);
		List<JobAdminResponse> data = jobPage.getContent().stream()
				.map(job -> JobAdminResponse.builder()
						.jobId(job.getJobId())
						.title(job.getTitle())
						.companyName(job.getCompanyName())
						.companyLogoUrl(job.getCompanyLogoUrl())
						.location(job.getLocation())
						.salaryMin(job.getSalaryMin())
						.salaryMax(job.getSalaryMax())
						.status(job.getStatus())
						.createdAt(job.getCreatedAt())
						.views(job.getViews())
						.numberOfApplications(job.getNumberOfApplications())
						.build())
				.toList();
		return PageResponse.<JobAdminResponse>builder()
				.page(page)
				.size(size)
				.totalElements(jobPage.getTotalElements())
				.totalPages(jobPage.getTotalPages())
				.content(data)
				.build();

	}

	@Override
	@Transactional
	@CacheEvict(cacheNames = "job-search", allEntries = true)
	public void adminDeleteJob(String adminId, String jobId) {
		if (adminId == null || adminId.isBlank()) {
			throw new RuntimeException("Admin id is required");
		}

		Job job = jobRepository.findById(jobId)
				.orElseThrow(() -> new RuntimeException("Job not found"));

		if (job.getDeletedAt() != null) {
			throw new RuntimeException("Job already deleted");
		}

		job.setDeletedAt(LocalDateTime.now());
		job.setUpdatedAt(LocalDateTime.now());

		jobRepository.save(job);
	}

	public String buildSearchCacheKey(
			String keyword,
			String industryId,
			String jobType,
			String location,
			String status,
			Integer experienceMin,
			Integer experienceMax,
			Double salaryMin,
			Double salaryMax,
			String sortBy,
			String sortDir,
			int page,
			int size) {
		StringBuilder key = new StringBuilder();
		key.append("keyword=").append(normalizeForKey(keyword))
				.append("|industryId=").append(normalizeForKey(industryId))
				.append("|jobType=").append(normalizeForKey(jobType))
				.append("|location=").append(normalizeForKey(location))
				.append("|status=").append(normalizeForKey(status))
				.append("|experienceMin=").append(experienceMin != null ? experienceMin : "")
				.append("|experienceMax=").append(experienceMax != null ? experienceMax : "")
				.append("|salaryMin=").append(salaryMin != null ? salaryMin : "")
				.append("|salaryMax=").append(salaryMax != null ? salaryMax : "")
				.append("|sortBy=").append(normalizeForKey(sortBy))
				.append("|sortDir=").append(normalizeForKey(sortDir))
				.append("|page=").append(page)
				.append("|size=").append(size);
		return key.toString();
	}

	private String normalizeForKey(String value) {
		return value == null ? "" : value.trim().toLowerCase(Locale.ROOT);
	}

	private List<JobResponse> mapJobsToResponseWithCompanyData(List<Job> jobs) {
		java.util.Map<String, CompanyDTO> companyCache = new java.util.HashMap<>();
		return jobs.stream().map(job -> {
			JobResponse response = JobMapper.toResponse(job);
			if (job.getCompanyId() != null) {
				if (!companyCache.containsKey(job.getCompanyId())) {
					try {
						CompanyDTO companyDTO = companyServiceClient.getCompanyById(job.getCompanyId());
						companyCache.put(job.getCompanyId(), companyDTO);
					} catch (Exception e) {
						log.warn("Lỗi gọi company-service cho companyId: {}", job.getCompanyId());
						companyCache.put(job.getCompanyId(), null);
					}
				}
				CompanyDTO companyDTO = companyCache.get(job.getCompanyId());
				if (companyDTO != null) {
					response.setCompanyName(companyDTO.getName());
					response.setLogo(companyDTO.getLogo());
				}
			}
			return response;
		}).toList();
	}

	private List<JobCardResponse> mapJobsToCardResponseWithCompanyData(List<Job> jobs) {
		java.util.Map<String, CompanyDTO> companyCache = new java.util.HashMap<>();
		return jobs.stream().map(job -> {
			JobCardResponse response = JobMapper.toCardResponse(job);
			if (job.getCompanyId() != null) {
				if (!companyCache.containsKey(job.getCompanyId())) {
					try {
						CompanyDTO companyDTO = companyServiceClient.getCompanyById(job.getCompanyId());
						companyCache.put(job.getCompanyId(), companyDTO);
					} catch (Exception e) {
						log.warn("Lỗi gọi company-service cho companyId: {}", job.getCompanyId());
						companyCache.put(job.getCompanyId(), null);
					}
				}
				CompanyDTO companyDTO = companyCache.get(job.getCompanyId());
				if (companyDTO != null) {
					response.setCompanyName(companyDTO.getName());
					response.setLogo(companyDTO.getLogo());
				}
			}
			return response;
		}).toList();
	}

}
