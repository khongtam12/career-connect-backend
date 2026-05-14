package iuh.fit.userservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CandidateProfileResponse {
    private String candidateId;
    private String email;
    private String fullName;
    private String phone;
    private String avatar;
    private LocalDate dateOfBirth;
    private String address;
    private int experienceYear;
    private String currentJobTitle;
    private double expectedSalary;
    private String status;
    private LocalDate createdAt;
    private LocalDate updatedAt;
}
