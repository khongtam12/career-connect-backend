package iuh.fit.userservice.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class EmployerProfileUpdateRequest {

    @NotNull
    @Size(min = 2, max = 100)
    private String fullName;

    @Pattern(regexp = "^\\+?[0-9]{9,15}$")
    private String phone;

    @Size(max = 500)
    private String avatar;

    @Size(max = 100)
    private String position;
}
