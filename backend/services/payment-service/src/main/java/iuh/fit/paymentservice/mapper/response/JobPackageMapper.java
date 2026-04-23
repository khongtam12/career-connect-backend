package iuh.fit.paymentservice.mapper.response;

import iuh.fit.paymentservice.dto.response.JobPackageResponseDTO;
import iuh.fit.paymentservice.model.JobPackage;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface JobPackageMapper {

    JobPackageResponseDTO toDTO(JobPackage entity);

    JobPackage toEntity(JobPackageResponseDTO dto);
}