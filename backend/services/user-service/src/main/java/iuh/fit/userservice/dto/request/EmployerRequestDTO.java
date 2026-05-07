package iuh.fit.userservice.dto.request;

import iuh.fit.userservice.model.Status;
import lombok.Data;

@Data
public class EmployerRequestDTO {
    private String fullName;
    private String email;
    private String companyId;
    private Status status;
}
