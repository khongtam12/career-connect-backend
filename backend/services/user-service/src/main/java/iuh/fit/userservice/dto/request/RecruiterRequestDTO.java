package iuh.fit.userservice.dto.request;

import iuh.fit.userservice.model.RecruiterStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RecruiterRequestDTO {
    @NotBlank(message = "Username must not be blank")
    private String username;

    @NotBlank(message = "Email must not be blank")
    @Email(message = "Email is invalid")
    private String email;

    @NotBlank(message = "Company ID must not be blank")
    private String companyId;

    private RecruiterStatus status;
}
