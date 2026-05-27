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
import iuh.fit.jobservice.dto.request.EmployerStatsRequest;
import iuh.fit.jobservice.dto.request.RenewJobRequest;
import iuh.fit.jobservice.dto.request.UpdateJobRequest;
import iuh.fit.jobservice.dto.response.*;
import iuh.fit.jobservice.event.EmployerJobStatusChangedEvent;
import iuh.fit.jobservice.exception.JobStatusException;
import iuh.fit.jobservice.mapper.JobMapper;
import iuh.fit.jobservice.model.Industry;
import iuh.fit.jobservice.model.Job;
import iuh.fit.jobservice.model.JobType;
import iuh.fit.jobservice.model.StatusJob;
import iuh.fit.jobservice.repository.IndustryRepository;
import iuh.fit.jobservice.repository.EmployerJobStatsView;
import iuh.fit.jobservice.repository.JobRepository;
import iuh.fit.jobservice.service.JobService;
import iuh.fit.jobservice.service.JobStatusTransition;
import iuh.fit.jobservice.service.JobStatusTransition.Role;
import iuh.fit.jobservice.specification.JobSpecifications;
import iuh.fit.jobservice.tools.LocationNormalizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.domain.Specification;

import java.text.Normalizer;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
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
	private final KafkaTemplate<String, Object> kafkaTemplate;
	private final CacheManager cacheManager;

	public JobServiceImpl(
			JobRepository jobRepository,
			IndustryRepository industryRepository,
			CompanyServiceClient companyServiceClient,
			UserServiceClient userServiceClient,
			KafkaTemplate<String, Object> kafkaTemplate,
			CacheManager cacheManager) {
		this.jobRepository = jobRepository;
		this.industryRepository = industryRepository;
		this.companyServiceClient = companyServiceClient;
		this.userServiceClient = userServiceClient;
		this.kafkaTemplate = kafkaTemplate;
		this.cacheManager = cacheManager;
	}

	@Override
	@Transactional
	public void incrementApplications(String jobId) {
		if (jobId == null || jobId.isBlank()) {
			throw new IllegalArgumentException("jobId is required");
		}
		int updated = jobRepository.incrementApplications(jobId, LocalDateTime.now());
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
		job.setIndustry(resolveIndustryId(request.getIndustry()));
		String province = normalizeProvince(request.getProvince());
		String ward = normalize(request.getWard());
		String addressDetail = normalize(request.getAddressDetail());
		String rawAddress = normalizeProvince(request.getAddress());
		job.setProvince(province != null ? province : rawAddress);
		job.setWard(ward);
		job.setAddressDetail(addressDetail);
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
			job.setIndustry(resolveIndustryId(request.getIndustry()));
		}
		String provinceUpdate = request.getProvince() != null ? normalizeProvince(request.getProvince()) : null;
		String wardUpdate = request.getWard() != null ? normalize(request.getWard()) : null;
		String addressDetailUpdate = request.getAddressDetail() != null ? normalize(request.getAddressDetail()) : null;
		String rawAddressUpdate = request.getAddress() != null ? normalizeProvince(request.getAddress()) : null;
		boolean hasLocationUpdate = provinceUpdate != null || wardUpdate != null || addressDetailUpdate != null || rawAddressUpdate != null;
		if (hasLocationUpdate) {
			String nextProvince = provinceUpdate != null ? provinceUpdate : (rawAddressUpdate != null ? rawAddressUpdate : job.getProvince());
			String nextWard = wardUpdate != null ? wardUpdate : job.getWard();
			String nextAddressDetail = addressDetailUpdate != null ? addressDetailUpdate : job.getAddressDetail();
			job.setProvince(nextProvince);
			job.setWard(nextWard);
			job.setAddressDetail(nextAddressDetail);
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
		Job saved = jobRepository.save(job);
		if (updated == StatusJob.ACTIVE || updated == StatusJob.REJECTED) {
			notifyEmployerAboutStatusChange(saved, updated);
		}
		return JobMapper.toResponse(saved);
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
		Job saved = jobRepository.save(job);
		if (updated == StatusJob.ACTIVE || updated == StatusJob.REJECTED) {
			notifyEmployerAboutStatusChange(saved, updated);
		}
		return JobMapper.toResponse(saved);
	}

	// ── SYSTEM: tự động expire khi hết hạn ───────────────────────────────────
	@Override
	public int expireOverdueJobs() {
		LocalDate today = LocalDate.now();
		List<Job> overdueJobs = jobRepository.findByStatusAndDeadlineBefore(
				StatusJob.ACTIVE, today);

		LocalDateTime now = LocalDateTime.now();
		for (Job job : overdueJobs) {
			job.setStatus(StatusJob.EXPIRED);
			job.setUpdatedAt(now);
		}
		if (!overdueJobs.isEmpty()) {
			jobRepository.saveAll(overdueJobs);
			overdueJobs.forEach(job -> notifyEmployerAboutStatusChange(job, StatusJob.EXPIRED));
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

	// ── SYSTEM: sync marketing assignments with company-service (expire/remove) ─────────────────
	@Scheduled(cron = "0 0/15 * * * *") // every 15 minutes
	@Transactional
	@CacheEvict(cacheNames = "job-search", allEntries = true)
	public int syncMarketingAssignments() {
		List<StatusJob> statuses = List.of(StatusJob.ACTIVE, StatusJob.PENDING, StatusJob.PAUSED);
		List<Job> jobsWithAssignment = jobRepository.findByMarketingAssignmentIdIsNotNullAndStatusIn(statuses);
		if (jobsWithAssignment == null || jobsWithAssignment.isEmpty()) {
			return 0;
		}

		List<Job> changed = new java.util.ArrayList<>();
		for (Job job : jobsWithAssignment) {
			try {
				CompanyMarketingAssignmentDTO active = companyServiceClient.getActiveMarketingAssignment(
						job.getCompanyId(), "JOB", job.getJobId());
				boolean mismatch = (active == null) || (active.getId() == null) || !active.getId().equals(job.getMarketingAssignmentId());
				if (mismatch) {
					clearMarketingAssignment(job);
					job.setUpdatedAt(LocalDateTime.now());
					changed.add(job);
				}
			} catch (Exception ex) {
				log.warn("Failed to verify marketing assignment for job {}: {}", job.getJobId(), ex.getMessage());
			}
		}

		if (!changed.isEmpty()) {
			jobRepository.saveAll(changed);
			return changed.size();
		}
		return 0;
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
				job.getSalaryDetail(), job.getBenefitsDetail(), job.getWorkSchedule(), job.getProvince(),
				job.getProvince(), job.getWard(), job.getAddressDetail(),
				job.getSalaryMin(), job.getSalaryMax(), job.isSalaryNegotiable(), job.getExperience(),
				job.getDeadline(), isDeadlineExpired(job.getDeadline()), job.getCreatedAt(), job.getUpdatedAt(), job.getViews(),
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
			String marketingPackageCategory,
			String marketingPackageType,
			String location,
			String status,
			Integer experienceMin,
			Integer experienceMax,
			Double salaryMin,
			Double salaryMax,
			String rank,
			String education,
			Boolean salaryNegotiable,
			String sortBy,
			String sortDir,
			int page,
			int size) {
		String normalizedLocation = normalizeProvince(location);
		String cacheKey = buildSearchCacheKey(
				keyword,
				industryId,
				jobType,
					marketingPackageCategory,
					marketingPackageType,
				normalizedLocation,
				status,
				experienceMin,
				experienceMax,
				salaryMin,
				salaryMax,
				rank,
				education,
				salaryNegotiable,
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
			Industry industry = industryRepository.getIndustryByIndustryId(industryId.trim());
			spec = spec.and(JobSpecifications.industryMatches(
					industryId.trim(),
					industry != null ? industry.getName() : null));
		}
		if (jobType != null && !jobType.isBlank()) {
			spec = spec.and(JobSpecifications.jobTypeEquals(parseJobType(jobType)));
		}
		if (marketingPackageCategory != null && !marketingPackageCategory.isBlank()) {
			spec = spec.and(JobSpecifications.marketingPackageCategoryEquals(marketingPackageCategory));
		}
		if (marketingPackageType != null && !marketingPackageType.isBlank()) {
			spec = spec.and(JobSpecifications.marketingPackageTypeEquals(marketingPackageType));
		}
		if (salaryMin != null) {
			spec = spec.and(JobSpecifications.salaryMin(salaryMin));
		}
		if (salaryMax != null) {
			spec = spec.and(JobSpecifications.salaryMax(salaryMax));
		}
		if (rank != null && !rank.isBlank()) {
			spec = spec.and(JobSpecifications.rankEquals(rank));
		}
		if (education != null && !education.isBlank()) {
			spec = spec.and(JobSpecifications.educationEquals(education));
		}
		if (salaryNegotiable != null) {
			spec = spec.and(JobSpecifications.salaryNegotiableEquals(salaryNegotiable));
		}
		if (experienceMin != null) {
			spec = spec.and(JobSpecifications.experienceMin(experienceMin));
		}
		if (experienceMax != null) {
			spec = spec.and(JobSpecifications.experienceMax(experienceMax));
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

	@Override
	@Transactional
	@CacheEvict(cacheNames = "job-search", allEntries = true)
	public CompanyMarketingAssignmentDTO applyCompanyMarketingPackage(String employerId, ApplyMarketingPackageRequest request) {
		validateEmployerId(employerId);
		if (request == null || request.getEntitlementId() == null || request.getEntitlementId().isBlank()) {
			throw new RuntimeException("Marketing entitlement is required");
		}

		String companyId = fetchCompanyIdByEmployerId(employerId);

		return companyServiceClient.assignMarketingEntitlement(
				request.getEntitlementId(),
				CompanyMarketingAssignmentRequest.builder()
						.companyId(companyId)
						.targetId(companyId) // Target is the company itself
						.targetScope("COMPANY")
						.placement(request.getPlacement() != null ? request.getPlacement() : "HOME_FEATURED_COMPANY")
						.build());
	}

	@Override
	@Transactional
	@CacheEvict(cacheNames = "job-search", allEntries = true)
	public void removeCompanyMarketingPackage(String employerId, String assignmentId) {
		validateEmployerId(employerId);
		if (assignmentId == null || assignmentId.isBlank()) {
			throw new RuntimeException("Assignment id is required");
		}

		String companyId = fetchCompanyIdByEmployerId(employerId);
		companyServiceClient.removeMarketingAssignment(assignmentId, companyId);
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

	private boolean isDeadlineExpired(LocalDate deadline) {
		if (deadline == null) return false;
		return deadline.isBefore(LocalDate.now());
	}

	private String normalize(String value) {
		if (value == null) {
			return null;
		}
		String trimmed = value.trim();
		return trimmed.isEmpty() ? null : trimmed;
	}

	private String normalizeProvince(String value) {
		String normalized = LocationNormalizer.normalizeLocation(value);
		return normalize(normalized);
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
		job.setMarketingPackageCategory(normalize(assignment.getPackageCategory()));
		job.setMarketingPackageType(normalize(assignment.getPackageType()));
		job.setMarketingPackageLabel(normalize(assignment.getPackageLabel()));

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

	private void notifyEmployerAboutStatusChange(Job job, StatusJob status) {
		if (job == null || status == null || job.getCompanyId() == null || job.getCompanyId().isBlank()) {
			return;
		}

		try {
			CompanyDTO company = companyServiceClient.getCompanyById(job.getCompanyId());
			EmployerCompanyResponse employer = null;
			if (job.getEmployerId() != null && !job.getEmployerId().isBlank()) {
				employer = userServiceClient.getEmployerById(job.getEmployerId());
			}
			EmployerJobStatusChangedEvent event = EmployerJobStatusChangedEvent.builder()
					.companyId(job.getCompanyId())
					.companyName(company != null && company.getName() != null ? company.getName() : job.getCompanyName())
					.employerEmail(employer != null ? employer.getEmail() : null)
					.jobId(job.getJobId())
					.jobTitle(job.getTitle())
					.status(status.name())
					.build();
			log.info(
					"Publishing employer job status event for job {} with status {} to employerId {} and email {}",
					job.getJobId(),
					status,
					job.getEmployerId(),
					event.getEmployerEmail());
			kafkaTemplate.send("job-status-changed", event);
		} catch (Exception ex) {
			log.warn(
					"Failed to publish employer job status event for job {} with status {}: {}",
					job.getJobId(),
					status,
					ex.getMessage());
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

		List<String> ranks = jobRepository.findDistinctRanks().stream()
				.filter(value -> value != null && !value.isBlank())
				.distinct()
				.sorted(String.CASE_INSENSITIVE_ORDER)
				.toList();

		List<String> educations = jobRepository.findDistinctEducations().stream()
				.filter(value -> value != null && !value.isBlank())
				.distinct()
				.sorted(String.CASE_INSENSITIVE_ORDER)
				.toList();

		List<String> locations = jobRepository.findDistinctLocations().stream()
				.filter(LocationNormalizer::isRecognizedProvince)
				.map(LocationNormalizer::toDisplayLabel)
				.filter(value -> value != null && !value.isBlank())
				.distinct()
				.sorted(String.CASE_INSENSITIVE_ORDER)
				.toList();

		return new JobFilterOptions(
				List.of(JobType.values()),
				List.of(StatusJob.values()),
				locations,
				industries,
				ranks,
				educations);
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

		Sort sort = Sort.by(Sort.Order.desc("top"));
		sort = sort.and(Sort.by(new Sort.Order(direction, sortField)));
		if (!"createdAt".equals(sortField)) {
			sort = sort.and(Sort.by(Sort.Order.desc("createdAt")));
		}
		return sort;
	}

	@Override
	public JobStats getStats() {
		long totalJobs = jobRepository.countByDeletedAtIsNullAndStatusNot(StatusJob.DRAFT);
		long activeJobs = jobRepository.countByDeletedAtIsNullAndStatus(StatusJob.ACTIVE);
		long newJobs24h = jobRepository.countByCreatedAtAfter(LocalDateTime.now().minusHours(24));
		long pendingJobs = jobRepository.countByDeletedAtIsNullAndStatus(StatusJob.PENDING);
		long rejectedJobs = jobRepository.countByDeletedAtIsNullAndStatus(StatusJob.REJECTED);

		return new JobStats(totalJobs, activeJobs, newJobs24h, pendingJobs, rejectedJobs);
	}

	@Override
	public List<EmployerStatsResponse> getEmployerJobStats(EmployerStatsRequest request) {
		if (request == null || request.getEmployerIds() == null || request.getEmployerIds().isEmpty()) {
			return List.of();
		}
		if (request.getStartDate() == null || request.getEndDate() == null) {
			return List.of();
		}
		List<EmployerJobStatsView> rows = jobRepository.findEmployerJobStats(
				request.getEmployerIds(),
				request.getStartDate(),
				request.getEndDate());
		List<EmployerStatsResponse> result = new ArrayList<>(rows.size());
		for (EmployerJobStatsView row : rows) {
			EmployerStatsResponse item = new EmployerStatsResponse();
			item.setEmployerId(row.getEmployerId());
			item.setJobCount(row.getJobCount() != null ? row.getJobCount() : 0L);
			item.setViewCount(row.getViewCount() != null ? row.getViewCount() : 0L);
			result.add(item);
		}
		return result;
	}

	@Override
	public MonthlyJobStatsResponse getMonthlyJobStats() {
		LocalDate now = LocalDate.now();
		LocalDate currentStart = now.withDayOfMonth(1);
		LocalDate currentEnd = now.with(TemporalAdjusters.lastDayOfMonth());
		LocalDate previousStart = currentStart.minusMonths(1);
		LocalDate previousEnd = previousStart.with(TemporalAdjusters.lastDayOfMonth());

		long currentCount = jobRepository.countByCreatedAtBetween(
				currentStart.atStartOfDay(),
				currentEnd.atTime(23, 59, 59));
		long previousCount = jobRepository.countByCreatedAtBetween(
				previousStart.atStartOfDay(),
				previousEnd.atTime(23, 59, 59));

		MonthlyJobStatsResponse response = new MonthlyJobStatsResponse();
		response.setCurrentMonthJobs(currentCount);
		response.setPreviousMonthJobs(previousCount);
		return response;
	}

	@Override
	public List<JobActivityDTO> getRecentActivities(int limit) {
		int size = limit <= 0 ? 8 : Math.min(limit, 20);
		List<JobActivityDTO> activities = new ArrayList<>();
		java.util.Set<String> activityKeys = new java.util.HashSet<>();
		int newJobLimit = Math.max(5, size / 2);
		int statusLimit = Math.max(4, size / 3);
		int applicationLimit = Math.max(3, size / 4);

		java.util.function.Consumer<JobActivityDTO> addActivity = (activity) -> {
			String key = String.format("%s|%s|%s",
					activity.getType(),
					activity.getJobId(),
					activity.getEventAt() != null ? activity.getEventAt().toString() : "");
			if (activityKeys.add(key)) {
				activities.add(activity);
			}
		};

		Pageable newJobPage = PageRequest.of(0, newJobLimit, Sort.by("createdAt").descending());
		List<Job> newJobs = jobRepository.findByDeletedAtIsNullOrderByCreatedAtDesc(newJobPage).getContent();
		for (Job job : newJobs) {
			addActivity.accept(JobActivityDTO.builder()
					.type("NEW_JOB")
					.jobId(job.getJobId())
					.title(job.getTitle())
					.companyName(job.getCompanyName())
					.status(job.getStatus())
					.eventAt(job.getCreatedAt())
					.build());
		}


		Pageable applicationPage = PageRequest.of(0, applicationLimit, Sort.by("updatedAt").descending());
		List<Job> applicationJobs = jobRepository
				.findByNumberOfApplicationsGreaterThanAndDeletedAtIsNullOrderByUpdatedAtDesc(0, applicationPage)
				.getContent();
		for (Job job : applicationJobs) {
			if (job.getUpdatedAt() == null) {
				continue;
			}
			addActivity.accept(JobActivityDTO.builder()
					.type("NEW_APPLICATION")
					.jobId(job.getJobId())
					.title(job.getTitle())
					.companyName(job.getCompanyName())
					.status(job.getStatus())
					.eventAt(job.getUpdatedAt())
					.numberOfApplications(job.getNumberOfApplications())
					.build());
		}

		List<StatusJob> statusFilters = List.of(
				StatusJob.ACTIVE,
				StatusJob.REJECTED,
				StatusJob.CLOSED,
				StatusJob.PAUSED
		);
		Pageable statusPage = PageRequest.of(0, statusLimit);
		List<Job> statusJobs = jobRepository
				.findRecentStatusActivities(statusFilters, statusPage)
				.getContent();
		for (Job job : statusJobs) {
			LocalDateTime updatedAt = job.getUpdatedAt() != null ? job.getUpdatedAt() : job.getCreatedAt();
			if (updatedAt == null) {
				continue;
			}
			addActivity.accept(JobActivityDTO.builder()
					.type("JOB_STATUS_CHANGED")
					.jobId(job.getJobId())
					.title(job.getTitle())
					.companyName(job.getCompanyName())
					.status(job.getStatus())
					.eventAt(updatedAt)
					.build());
		}

		activities.sort((a, b) -> {
			LocalDateTime dateA = a.getEventAt();
			LocalDateTime dateB = b.getEventAt();
			if (dateA == null && dateB == null) return 0;
			if (dateA == null) return 1;
			if (dateB == null) return -1;
			return dateB.compareTo(dateA);
		});
		return activities.size() > size ? activities.subList(0, size) : activities;
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
						.location(job.getProvince())
						.salaryMin(job.getSalaryMin())
						.salaryMax(job.getSalaryMax())
						.status(job.getStatus())
						.createdAt(job.getCreatedAt())
						.deadlineExpired(isDeadlineExpired(job.getDeadline()))
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
	public List<java.util.Map<String, Object>> getWeeklyNewJobs() {
		LocalDateTime startDate = LocalDateTime.now().minusDays(6).with(java.time.LocalTime.MIN);
		List<Object[]> rawData = jobRepository.countNewJobsByDay(startDate);
		List<java.util.Map<String, Object>> result = new ArrayList<>();
		for (Object[] row : rawData) {
			java.util.Map<String, Object> map = new java.util.HashMap<>();
			map.put("date", row[0].toString());
			map.put("count", ((Number) row[1]).longValue());
			result.add(map);
		}
		return result;
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
			String marketingPackageCategory,
			String marketingPackageType,
			String location,
			String status,
			Integer experienceMin,
			Integer experienceMax,
			Double salaryMin,
			Double salaryMax,
			String rank,
			String education,
			Boolean salaryNegotiable,
			String sortBy,
			String sortDir,
			int page,
			int size) {
		StringBuilder key = new StringBuilder();
		key.append("keyword=").append(normalizeForKey(keyword))
				.append("|industryId=").append(normalizeForKey(industryId))
				.append("|jobType=").append(normalizeForKey(jobType))
				.append("|marketingPackageCategory=").append(normalizeForKey(marketingPackageCategory))
				.append("|marketingPackageType=").append(normalizeForKey(marketingPackageType))
				.append("|location=").append(normalizeForKey(location))
				.append("|status=").append(normalizeForKey(status))
				.append("|experienceMin=").append(experienceMin != null ? experienceMin : "")
				.append("|experienceMax=").append(experienceMax != null ? experienceMax : "")
				.append("|salaryMin=").append(salaryMin != null ? salaryMin : "")
				.append("|salaryMax=").append(salaryMax != null ? salaryMax : "")
				.append("|rank=").append(normalizeForKey(rank))
				.append("|education=").append(normalizeForKey(education))
				.append("|salaryNegotiable=").append(salaryNegotiable != null ? salaryNegotiable : "")
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
			response.setIndustry(resolveIndustryDisplayName(job.getIndustry()));
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
			response.setIndustryId(resolveIndustryId(job.getIndustry()));
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

	private String resolveIndustryId(String industryValue) {
		String normalizedIndustry = normalize(industryValue);
		if (normalizedIndustry == null) {
			return null;
		}

		Industry byId = industryRepository.getIndustryByIndustryId(normalizedIndustry);
		if (byId != null) {
			return byId.getIndustryId();
		}

		Industry byName = industryRepository.findByNameIgnoreCase(normalizedIndustry);
		if (byName != null) {
			return byName.getIndustryId();
		}

		return normalizedIndustry;
	}

	private String resolveIndustryDisplayName(String industryValue) {
		String resolvedIndustryId = resolveIndustryId(industryValue);
		if (resolvedIndustryId == null) {
			return null;
		}

		Industry industry = industryRepository.getIndustryByIndustryId(resolvedIndustryId);
		return industry != null ? industry.getName() : industryValue;
	}

}
