package iuh.fit.userservice.mapper;

import iuh.fit.userservice.dto.request.RecruiterRequestDTO;
import iuh.fit.userservice.dto.response.RecruiterResponseDTO;
import iuh.fit.userservice.model.Recruiter;
import iuh.fit.userservice.model.RecruiterStatus;
import iuh.fit.userservice.model.Role;
import org.springframework.stereotype.Component;

@Component
public class RecruiterMapper {

    public RecruiterResponseDTO toDto(Recruiter entity, String companyName) {
        RecruiterResponseDTO dto = new RecruiterResponseDTO();
        dto.setId(entity.getId());
        dto.setUsername(entity.getUsername());
        dto.setEmail(entity.getEmail());
        dto.setCompanyName(companyName);
        dto.setStatus(entity.getStatus());
        return dto;
    }

    public Recruiter toEntity(RecruiterRequestDTO dto) {
        return Recruiter.builder()
                .username(dto.getUsername())
                .email(dto.getEmail())
                .companyId(dto.getCompanyId())
                .status(dto.getStatus() != null ? dto.getStatus() : RecruiterStatus.ACTIVE)
                .role(Role.RECRUITER)
                .build();
    }
}
