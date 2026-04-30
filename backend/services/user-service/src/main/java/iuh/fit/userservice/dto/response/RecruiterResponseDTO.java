package iuh.fit.userservice.dto.response;

import iuh.fit.userservice.model.RecruiterStatus;
import lombok.Data;

@Data
public class RecruiterResponseDTO {
    private String id;
    private String username;
    private String email;
    private String companyName;
    private RecruiterStatus status;
}
