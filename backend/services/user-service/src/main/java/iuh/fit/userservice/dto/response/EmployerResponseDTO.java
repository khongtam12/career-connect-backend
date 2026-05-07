package iuh.fit.userservice.dto.response;

import iuh.fit.userservice.model.Status;
import lombok.Data;
import java.time.LocalDate;

@Data
public class EmployerResponseDTO {
    private String id;
    private String fullName;
    private String email;
    private String companyName;
    private Status status;
    private String avatar;
    private LocalDate createdAt;
    private String position;
}
