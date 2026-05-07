package iuh.fit.userservice.mapper;

import iuh.fit.userservice.dto.request.EmployerRequestDTO;
import iuh.fit.userservice.dto.response.EmployerResponseDTO;
import iuh.fit.userservice.model.Employer;
import iuh.fit.userservice.model.Status;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class EmployerMapper {

    public EmployerResponseDTO toDto(Employer entity, String companyName) {
        EmployerResponseDTO dto = new EmployerResponseDTO();
        dto.setId(entity.getEmployerId());
        dto.setFullName(entity.getFullName());
        dto.setEmail(entity.getEmail());
        dto.setCompanyName(companyName);
        dto.setStatus(entity.getStatus());
        dto.setAvatar(entity.getAvatar());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setPosition(entity.getPosition());
        return dto;
    }

    public Employer toEntity(EmployerRequestDTO dto) {
        Employer employer = new Employer();
        employer.setFullName(dto.getFullName());
        employer.setEmail(dto.getEmail());
        employer.setCompanyId(dto.getCompanyId());
        employer.setStatus(dto.getStatus() != null ? dto.getStatus() : Status.ACTIVE);
        employer.setCreatedAt(LocalDate.now());
        employer.setUpdatedAt(LocalDate.now());
        return employer;
    }
}
