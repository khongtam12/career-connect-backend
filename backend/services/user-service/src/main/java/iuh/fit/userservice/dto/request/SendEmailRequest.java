package iuh.fit.userservice.dto.request;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SendEmailRequest {
    private String to;
    private String type; // OTP
    private String otp;
}
