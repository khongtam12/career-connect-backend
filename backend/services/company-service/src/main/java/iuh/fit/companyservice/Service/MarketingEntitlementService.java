package iuh.fit.companyservice.Service;

import iuh.fit.companyservice.dto.request.CompanyMarketingEntitlementRequest;
import iuh.fit.companyservice.dto.request.CompanyMarketingAssignmentRequest;
import iuh.fit.companyservice.dto.response.CompanyMarketingAssignmentResponse;
import iuh.fit.companyservice.dto.response.CompanyMarketingEntitlementResponse;
import iuh.fit.companyservice.model.Company;
import iuh.fit.companyservice.model.CompanyMarketingAssignment;
import iuh.fit.companyservice.model.CompanyMarketingEntitlement;
import iuh.fit.companyservice.model.MarketingTargetScope;
import iuh.fit.companyservice.model.StatusMarketingAssignment;
import iuh.fit.companyservice.model.StatusMarketingEntitlement;
import iuh.fit.companyservice.repository.CompanyMarketingAssignmentRepository;
import iuh.fit.companyservice.repository.CompanyMarketingEntitlementRepository;
import iuh.fit.companyservice.util.IdGenerator;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MarketingEntitlementService {
    private final CompanyMarketingEntitlementRepository companyMarketingEntitlementRepository;
    private final CompanyMarketingAssignmentRepository companyMarketingAssignmentRepository;

    public MarketingEntitlementService(
            CompanyMarketingEntitlementRepository companyMarketingEntitlementRepository,
            CompanyMarketingAssignmentRepository companyMarketingAssignmentRepository
    ) {
        this.companyMarketingEntitlementRepository = companyMarketingEntitlementRepository;
        this.companyMarketingAssignmentRepository = companyMarketingAssignmentRepository;
    }

    public CompanyMarketingEntitlement save(CompanyMarketingEntitlementRequest request) {
        int quantity = Math.max(request.getQuantity(), 1);
        int usageLimit = Math.max(request.getJobLimit(), 0) * quantity;
        LocalDateTime startDate = LocalDateTime.now();

        CompanyMarketingEntitlement entitlement = CompanyMarketingEntitlement.builder()
                .id(IdGenerator.generatorIdCompanyMarketingEntitlement())
                .company(new Company(request.getCompanyId()))
                .paymentId(request.getPaymentId())
                .packageId(request.getPackageId())
                .packageLabel(request.getPackageLabel())
                .packageCategory(request.getPackageCategory())
                .packageType(request.getPackageType())
                .targetScope(resolveTargetScope(request.getPackageCategory()))
                .usageLimit(usageLimit)
                .usedCount(0)
                .quantity(quantity)
                .durationDays(request.getDurationDays())
                .startDate(startDate)
                .endDate(startDate.plusDays(request.getDurationDays()))
                .status(StatusMarketingEntitlement.ACTIVE)
                .build();

        return companyMarketingEntitlementRepository.save(entitlement);
    }

    public List<CompanyMarketingEntitlementResponse> getByCompanyId(String companyId, String category) {
        List<CompanyMarketingEntitlement> entitlements =
                category == null || category.isBlank()
                        ? companyMarketingEntitlementRepository.findByCompany_CompanyId(companyId)
                        : companyMarketingEntitlementRepository.findByCompany_CompanyIdAndPackageCategory(companyId, category.trim().toUpperCase());

        return entitlements.stream()
                .map(this::toResponse)
                .toList();
    }

    public List<String> expireEntitlements() {
        LocalDateTime now = LocalDateTime.now();
        List<CompanyMarketingEntitlement> expired = companyMarketingEntitlementRepository
                .findByStatusAndEndDateBefore(StatusMarketingEntitlement.ACTIVE, now);

        if (expired.isEmpty()) {
            return List.of();
        }

        expired.forEach(entitlement -> entitlement.setStatus(StatusMarketingEntitlement.EXPIRED));
        companyMarketingEntitlementRepository.saveAll(expired);
        expireAssignments();
        return expired.stream().map(CompanyMarketingEntitlement::getId).toList();
    }

    public CompanyMarketingAssignmentResponse assignEntitlement(String entitlementId, CompanyMarketingAssignmentRequest request) {
        CompanyMarketingEntitlement entitlement = companyMarketingEntitlementRepository.findById(entitlementId)
                .orElseThrow(() -> new RuntimeException("Marketing entitlement not found"));

        if (!entitlement.getCompany().getCompanyId().equals(request.getCompanyId())) {
            throw new RuntimeException("Marketing entitlement does not belong to company");
        }
        if (entitlement.getStatus() != StatusMarketingEntitlement.ACTIVE) {
            throw new RuntimeException("Marketing entitlement is not active");
        }
        if (entitlement.getEndDate() != null && entitlement.getEndDate().isBefore(LocalDateTime.now())) {
            entitlement.setStatus(StatusMarketingEntitlement.EXPIRED);
            companyMarketingEntitlementRepository.save(entitlement);
            throw new RuntimeException("Marketing entitlement expired");
        }

        MarketingTargetScope targetScope = parseTargetScope(request.getTargetScope());
        if (entitlement.getTargetScope() != targetScope) {
            throw new RuntimeException("Marketing entitlement target scope mismatch");
        }
        if (request.getTargetId() == null || request.getTargetId().isBlank()) {
            throw new RuntimeException("Target id is required");
        }
        if (entitlement.getUsedCount() >= entitlement.getUsageLimit()) {
            throw new RuntimeException("Marketing entitlement usage limit reached");
        }

        companyMarketingAssignmentRepository.findByEntitlementIdAndTargetIdAndStatus(
                        entitlementId,
                        request.getTargetId(),
                        StatusMarketingAssignment.ACTIVE
                )
                .ifPresent(existing -> {
                    throw new RuntimeException("Marketing entitlement already applied to target");
                });

        LocalDateTime now = LocalDateTime.now();
        CompanyMarketingAssignment assignment = CompanyMarketingAssignment.builder()
                .id(IdGenerator.generatorIdCompanyMarketingAssignment())
                .entitlementId(entitlement.getId())
                .company(entitlement.getCompany())
                .targetId(request.getTargetId())
                .placement(request.getPlacement())
                .packageId(entitlement.getPackageId())
                .packageLabel(entitlement.getPackageLabel())
                .packageCategory(entitlement.getPackageCategory())
                .packageType(entitlement.getPackageType())
                .targetScope(targetScope)
                .assignedAt(now)
                .expiresAt(entitlement.getEndDate())
                .status(StatusMarketingAssignment.ACTIVE)
                .build();

        CompanyMarketingAssignment saved = companyMarketingAssignmentRepository.save(assignment);
        entitlement.setUsedCount(entitlement.getUsedCount() + 1);
        companyMarketingEntitlementRepository.save(entitlement);
        return toAssignmentResponse(saved);
    }

    public CompanyMarketingAssignmentResponse removeAssignment(String assignmentId, String companyId) {
        CompanyMarketingAssignment assignment = companyMarketingAssignmentRepository.findByIdAndCompany_CompanyId(assignmentId, companyId)
                .orElseThrow(() -> new RuntimeException("Marketing assignment not found"));

        if (assignment.getStatus() != StatusMarketingAssignment.ACTIVE) {
            throw new RuntimeException("Marketing assignment is not active");
        }

        assignment.setStatus(StatusMarketingAssignment.REMOVED);
        assignment.setExpiresAt(LocalDateTime.now());
        CompanyMarketingAssignment saved = companyMarketingAssignmentRepository.save(assignment);

        companyMarketingEntitlementRepository.findById(assignment.getEntitlementId())
                .ifPresent(entitlement -> {
                    entitlement.setUsedCount(Math.max(entitlement.getUsedCount() - 1, 0));
                    companyMarketingEntitlementRepository.save(entitlement);
                });

        return toAssignmentResponse(saved);
    }

    public List<String> getFeaturedCompanyIds() {
        return companyMarketingAssignmentRepository
                .findByTargetScopeAndStatus(MarketingTargetScope.COMPANY, StatusMarketingAssignment.ACTIVE)
                .stream()
                .map(CompanyMarketingAssignment::getTargetId)
                .distinct()
                .toList();
    }

    public List<CompanyMarketingAssignmentResponse> getAssignments(String companyId, String targetScope, String targetId) {
        List<CompanyMarketingAssignment> assignments = targetScope == null || targetScope.isBlank()
                ? companyMarketingAssignmentRepository.findByCompany_CompanyId(companyId)
                : companyMarketingAssignmentRepository.findByCompany_CompanyIdAndTargetScope(companyId, parseTargetScope(targetScope));

        return assignments.stream()
                .filter(assignment -> targetId == null || targetId.isBlank() || targetId.equals(assignment.getTargetId()))
                .map(this::toAssignmentResponse)
                .toList();
    }

    public CompanyMarketingAssignmentResponse getActiveAssignmentForTarget(String companyId, String targetScope, String targetId) {
        if (targetId == null || targetId.isBlank()) {
            return null;
        }

        return companyMarketingAssignmentRepository.findByTargetIdAndTargetScopeAndStatus(
                        targetId,
                        parseTargetScope(targetScope),
                        StatusMarketingAssignment.ACTIVE
                )
                .filter(assignment -> assignment.getCompany().getCompanyId().equals(companyId))
                .map(this::toAssignmentResponse)
                .orElse(null);
    }

    private CompanyMarketingEntitlementResponse toResponse(CompanyMarketingEntitlement entitlement) {
        return CompanyMarketingEntitlementResponse.builder()
                .id(entitlement.getId())
                .companyId(entitlement.getCompany() != null ? entitlement.getCompany().getCompanyId() : null)
                .paymentId(entitlement.getPaymentId())
                .packageId(entitlement.getPackageId())
                .packageLabel(entitlement.getPackageLabel())
                .packageCategory(entitlement.getPackageCategory())
                .packageType(entitlement.getPackageType())
                .targetScope(entitlement.getTargetScope())
                .usageLimit(entitlement.getUsageLimit())
                .usedCount(entitlement.getUsedCount())
                .remainingCount(Math.max(entitlement.getUsageLimit() - entitlement.getUsedCount(), 0))
                .quantity(entitlement.getQuantity())
                .durationDays(entitlement.getDurationDays())
                .startDate(entitlement.getStartDate())
                .endDate(entitlement.getEndDate())
                .status(entitlement.getStatus())
                .build();
    }

    private CompanyMarketingAssignmentResponse toAssignmentResponse(CompanyMarketingAssignment assignment) {
        return CompanyMarketingAssignmentResponse.builder()
                .id(assignment.getId())
                .entitlementId(assignment.getEntitlementId())
                .companyId(assignment.getCompany() != null ? assignment.getCompany().getCompanyId() : null)
                .targetId(assignment.getTargetId())
                .placement(assignment.getPlacement())
                .packageId(assignment.getPackageId())
                .packageLabel(assignment.getPackageLabel())
                .packageCategory(assignment.getPackageCategory())
                .packageType(assignment.getPackageType())
                .targetScope(assignment.getTargetScope())
                .assignedAt(assignment.getAssignedAt())
                .expiresAt(assignment.getExpiresAt())
                .status(assignment.getStatus())
                .build();
    }

    private MarketingTargetScope resolveTargetScope(String packageCategory) {
        if ("BRANDING".equalsIgnoreCase(packageCategory)) {
            return MarketingTargetScope.COMPANY;
        }
        return MarketingTargetScope.JOB;
    }

    private MarketingTargetScope parseTargetScope(String targetScope) {
        if (targetScope == null || targetScope.isBlank()) {
            throw new RuntimeException("Target scope is required");
        }
        try {
            return MarketingTargetScope.valueOf(targetScope.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new RuntimeException("Invalid marketing target scope");
        }
    }

    private void expireAssignments() {
        LocalDateTime now = LocalDateTime.now();
        List<CompanyMarketingAssignment> expiredAssignments = companyMarketingAssignmentRepository
                .findByStatusAndExpiresAtBefore(StatusMarketingAssignment.ACTIVE, now);

        if (expiredAssignments.isEmpty()) {
            return;
        }

        expiredAssignments.forEach(assignment -> assignment.setStatus(StatusMarketingAssignment.EXPIRED));
        companyMarketingAssignmentRepository.saveAll(expiredAssignments);

        expiredAssignments.stream()
                .map(CompanyMarketingAssignment::getEntitlementId)
                .distinct()
                .forEach(entitlementId -> companyMarketingEntitlementRepository.findById(entitlementId)
                        .ifPresent(entitlement -> {
                            long activeCount = companyMarketingAssignmentRepository.findByCompany_CompanyId(entitlement.getCompany().getCompanyId()).stream()
                                    .filter(assignment -> entitlementId.equals(assignment.getEntitlementId()))
                                    .filter(assignment -> assignment.getStatus() == StatusMarketingAssignment.ACTIVE)
                                    .count();
                            entitlement.setUsedCount((int) activeCount);
                            companyMarketingEntitlementRepository.save(entitlement);
                        }));
    }
}
