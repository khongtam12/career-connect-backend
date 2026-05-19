package iuh.fit.userservice.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public  class SendOtpRequest {
    private String email;
    private String type;
}
