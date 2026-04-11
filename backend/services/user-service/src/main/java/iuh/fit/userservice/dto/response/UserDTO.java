package iuh.fit.userservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class UserDTO {
    private String userId;
    private String email;
    private String fullName;
    private String phone;
    private String avatar;
    private String role;
}
