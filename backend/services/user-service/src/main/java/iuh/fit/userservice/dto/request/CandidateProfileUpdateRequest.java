package iuh.fit.userservice.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CandidateProfileUpdateRequest {

    @NotNull
    @Size(min = 2, max = 100)
    private String fullName;

    @Pattern(regexp = "^\\+?[0-9]{9,15}$")
    private String phone;

    @Size(max = 500)
    private String avatar;

    private LocalDate dateOfBirth;

    @Size(max = 255)
    private String address;

    @Min(0)
    @Max(50)
    private int experienceYear;

    @Size(max = 100)
    private String currentJobTitle;

    @DecimalMin("0.0")
    private double expectedSalary;
}
