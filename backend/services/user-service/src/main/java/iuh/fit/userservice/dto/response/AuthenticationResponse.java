package iuh.fit.userservice.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;
@Builder
@Data
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthenticationResponse {

 private    String userId;
  private   String token;
  private   boolean authenticated;

    public AuthenticationResponse(String userId, String token, boolean authenticated) {
        this.userId = userId;
        this.token = token;
        this.authenticated = authenticated;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public boolean isAuthenticated() {
        return authenticated;
    }

    public void setAuthenticated(boolean authenticated) {
        this.authenticated = authenticated;
    }
}
