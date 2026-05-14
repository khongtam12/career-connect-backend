package iuh.fit.userservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployerProfileResponse {
    private String employerId;
    private String email;
    private String fullName;
    private String phone;
    private String avatar;
    private String position;
    private String companyId;
    private String companyName;
    private String status;
    private LocalDate createdAt;
    private LocalDate updatedAt;
}
