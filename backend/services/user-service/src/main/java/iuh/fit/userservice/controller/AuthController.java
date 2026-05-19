package iuh.fit.userservice.controller;

import com.nimbusds.jose.JOSEException;
import iuh.fit.userservice.dto.request.AuthenticationRequest;
import iuh.fit.userservice.dto.request.RegisterDTO;
import iuh.fit.userservice.dto.request.VerifyOtpRequest;
import iuh.fit.userservice.dto.response.ApiResponse;
import iuh.fit.userservice.dto.response.UserDTO;
import iuh.fit.userservice.service.AuthenticationService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/user/auth")
public class AuthController {
    private final AuthenticationService authService;

    public AuthController(AuthenticationService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthenticationRequest request, HttpServletResponse response) {

        var authRes = authService.authenticate(request);

        ResponseCookie cookie = ResponseCookie.from("access_token", authRes.getToken())
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .path("/")
                .maxAge(3600)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        return ResponseEntity.ok(
                Map.of(
                        "token", authRes.getToken(),
                        "userId", authRes.getUserId()));
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@RequestHeader(value = "Authorization", required = false) String authHeader)
            throws ParseException, JOSEException {
        if (authHeader == null || authHeader.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "No token provided."));
        }
        try {
            String token = authHeader.substring(7);
            var user = authService.getCurrentUser(token);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));

        }

    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from("access_token", "")
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .path("/")
                .maxAge(0)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        return ResponseEntity.ok(Map.of("message", "Logged out successfully"));
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserDTO>> register(@RequestBody RegisterDTO request) {
        return ResponseEntity.ok(ApiResponse.created(authService.register(request)));
    }

    @PostMapping("/send-otp")
    public ResponseEntity<ApiResponse<String>> sendOtp(@RequestBody SendOtpRequest request) {
        authService.sendOtp(request.getEmail(), request.getType());
        return ResponseEntity.ok(ApiResponse.success("Mã OTP đã được gửi đến email của bạn"));
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<ApiResponse<String>> verifyOtp(@RequestBody VerifyOtpRequest request) {
        authService.verifyOtp(request.getEmail(), request.getOtp());
        return ResponseEntity.ok(ApiResponse.success("Xác thực OTP thành công"));
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestBody ChangePasswordRequest request) throws ParseException, JOSEException {
        if (authHeader == null || authHeader.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                ApiResponse.builder()
                    .status(HttpStatus.UNAUTHORIZED.value())
                    .message("No token provided.")
                    .build()
            );
        }
        String token = authHeader.substring(7);
        authService.changePassword(token, request.getCurrentPassword(), request.getNewPassword());
        return ResponseEntity.ok(ApiResponse.success("Đổi mật khẩu thành công"));
    }

    @Getter
    @Setter
    public static class SendOtpRequest {
        private String email;
        private String type;
    }

    @Getter
    @Setter
    public static class ChangePasswordRequest {
        private String currentPassword;
        private String newPassword;
        private String confirmPassword;
    }
}
