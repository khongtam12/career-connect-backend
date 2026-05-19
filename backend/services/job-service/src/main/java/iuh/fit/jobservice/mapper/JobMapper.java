package iuh.fit.jobservice.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import iuh.fit.jobservice.dto.response.JobResponse;
import iuh.fit.jobservice.dto.response.JobCardResponse;
import iuh.fit.jobservice.model.Job;

import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

public class JobMapper {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public static JobCardResponse toCardResponse(Job job) {
        return JobCardResponse.builder()
                .jobId(job.getJobId())
                .companyName(job.getCompanyName())
                .logo(job.getCompanyLogoUrl())
                .title(job.getTitle())
                .location(job.getLocation())
                .salaryMin(job.getSalaryMin())
                .salaryMax(job.getSalaryMax())
                .salaryNegotiable(job.isSalaryNegotiable())
                .deadline(job.getDeadline() != null ? job.getDeadline().format(DATE_FMT) : null)
                .createdAt(job.getCreatedAt())
                .views(job.getViews())
                .isTop(job.isTop())
                .marketingPackageCategory(job.getMarketingPackageCategory())
                .marketingPackageType(job.getMarketingPackageType())
                .benefitTags(parseJsonList(job.getBenefitTags()))
                .build();
    }

    public static JobResponse toResponse(Job job) {
        return JobResponse.builder()
                .jobId(job.getJobId())
                .employerId(job.getEmployerId())
                .companyId(job.getCompanyId())
                .companyName(job.getCompanyName())
                .logo(job.getCompanyLogoUrl())
                .title(job.getTitle())
                .industry(job.getIndustry())
                .location(job.getLocation())
                .jobType(job.getJobType() != null ? job.getJobType().name() : null)
                .experience(job.getExperience())
                .salaryMin(job.getSalaryMin())
                .salaryMax(job.getSalaryMax())
                .salaryNegotiable(job.isSalaryNegotiable())
                .deadline(job.getDeadline() != null ? job.getDeadline().format(DATE_FMT) : null)
                .createdAt(job.getCreatedAt())
                .updatedAt(job.getUpdatedAt())
                .views(job.getViews())
                .applicants(job.getNumberOfApplications())
                .isTop(job.isTop())
                .status(job.getStatus() != null ? job.getStatus().name().toLowerCase() : null)
                .packageId(job.getPackageId())
                .packageLabel(job.getPackageLabel())
                .companySubscriptionId(job.getCompanySubscriptionId())
                .marketingAssignmentId(job.getMarketingAssignmentId())
                .marketingPackageCategory(job.getMarketingPackageCategory())
                .marketingPackageType(job.getMarketingPackageType())
                .marketingPackageLabel(job.getMarketingPackageLabel())
                .rank(job.getRank())
                .education(job.getEducation())
                .quantity(job.getQuantity())
                .ageRange(job.getAgeRange())
                .requirementTags(parseJsonList(job.getRequirementTags()))
                .benefitTags(parseJsonList(job.getBenefitTags()))
                .specialties(parseJsonList(job.getSpecialties()))
                .description(job.getDescription())
                .candidateRequirements(job.getCandidateRequirements())
                .salaryDetail(job.getSalaryDetail())
                .benefitsDetail(job.getBenefitsDetail())
                .workSchedule(job.getWorkSchedule())
                .relatedCategories(parseJsonList(job.getRelatedCategories()))
                .skills(parseJsonList(job.getSkills()))
                .build();
    }

    public static String toJson(List<String> list) {
        if (list == null || list.isEmpty()) return "[]";
        try {
            return objectMapper.writeValueAsString(list);
        } catch (JsonProcessingException e) {
            return "[]";
        }
    }

    public static List<String> parseJsonList(String json) {
        if (json == null || json.isBlank()) return Collections.emptyList();
        try {
            return objectMapper.readValue(json, new TypeReference<List<String>>() {});
        } catch (JsonProcessingException e) {
            return Collections.emptyList();
        }
    }
}
