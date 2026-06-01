package iuh.fit.paymentservice.mapper.response;

import iuh.fit.paymentservice.dto.response.JobPackageResponseDTO;
import iuh.fit.paymentservice.model.JobPackage;
import org.springframework.stereotype.Component;

@Component
public class JobPackageMapper {

    public JobPackageResponseDTO toDTO(JobPackage entity) {
        if (entity == null) {
            return null;
        }

        return JobPackageResponseDTO.builder()
                .packageId(entity.getPackageId())
                .name(entity.getName())
                .description(entity.getDescription())
                .price(entity.getPrice())
                .oldPrice(entity.getOldPrice())
                .durationDays(entity.getDurationDays())
                .jobPostLimit(entity.getJobPostLimit())
                .category(entity.getCategory())
                .type(entity.getType())
                .badge(entity.getBadge())
                .badgeColor(entity.getBadgeColor())
                .imageUrl(entity.getImageUrl())
                .showDetails(entity.isShowDetails())
                .active(entity.isActive())
                .allowedBoxTypes(entity.getAllowedBoxTypes())
                .build();
    }

    public JobPackage toEntity(JobPackageResponseDTO dto) {
        if (dto == null) {
            return null;
        }

        return JobPackage.builder()
                .packageId(dto.getPackageId())
                .name(dto.getName())
                .description(dto.getDescription())
                .price(dto.getPrice())
                .oldPrice(dto.getOldPrice())
                .durationDays(dto.getDurationDays())
                .jobPostLimit(dto.getJobPostLimit())
                .category(dto.getCategory())
                .type(dto.getType())
                .badge(dto.getBadge())
                .badgeColor(dto.getBadgeColor())
                .imageUrl(dto.getImageUrl())
                .showDetails(dto.isShowDetails())
                .isActive(dto.isActive())
                .allowedBoxTypes(dto.getAllowedBoxTypes())
                .build();
    }
}
