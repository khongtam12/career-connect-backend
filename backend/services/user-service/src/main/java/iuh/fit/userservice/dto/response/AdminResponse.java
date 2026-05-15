package iuh.fit.userservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminResponse {
    private String adminId;
    private String email;
    private String fullName;
    private String phone;
    private String avatar;
    private String status;
    private LocalDate createdAt;
    private LocalDate updatedAt;
}
