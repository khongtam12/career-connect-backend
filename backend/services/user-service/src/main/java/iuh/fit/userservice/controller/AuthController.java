package iuh.fit.userservice.controller;

import com.nimbusds.jose.JOSEException;
import iuh.fit.userservice.dto.request.AuthenticationRequest;
import iuh.fit.userservice.dto.request.RegisterDTO;
import iuh.fit.userservice.dto.request.SendOtpRequest;
import iuh.fit.userservice.dto.request.VerifyOtpRequest;
import iuh.fit.userservice.dto.request.ResetPasswordRequest;
import iuh.fit.userservice.dto.response.ApiResponse;
import iuh.fit.userservice.dto.response.AuthenticationResponse;
import iuh.fit.userservice.dto.response.UserDTO;
import iuh.fit.userservice.exception.AppException;
import iuh.fit.userservice.exception.ErrorCode;
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
        try {
            AuthenticationResponse authRes = authService.authenticate(request);

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
                            "userId", authRes.getUserId()
                    )
            );
        } catch (AppException e) {
            ErrorCode errorCode = e.getErrorCode();
            HttpStatus status = errorCode == ErrorCode.UNAUTHENTICATED || errorCode == ErrorCode.USER_NOT_EXISTED
                    ? HttpStatus.UNAUTHORIZED
                    : HttpStatus.BAD_REQUEST;

            return ResponseEntity.status(status)
                    .body(Map.of(
                            "status", errorCode.getCode(),
                            "message", errorCode.getMessage()
                    ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "status", ErrorCode.UNCATEGORIZED_EXCEPTION.getCode(),
                            "message", "Đăng nhập thất bại, vui lòng thử lại sau"
                    ));
        }
    }

    @PostMapping("/outbound/authentication")
    public ResponseEntity<?> outboundAuthenticate(@RequestParam("code") String code, @RequestParam("type") String type, HttpServletResponse response) {
        try {
            AuthenticationResponse authRes = authService.outboundAuthenticate(code, type);

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
                            "userId", authRes.getUserId()
                    )
            );
        } catch (AppException e) {
            ErrorCode errorCode = e.getErrorCode();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(
                            "status", errorCode.getCode(),
                            "message", errorCode.getMessage()
                    ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "status", ErrorCode.UNCATEGORIZED_EXCEPTION.getCode(),
                            "message", "Đăng nhập Google thất bại, vui lòng thử lại sau"
                    ));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@RequestHeader(value = "Authorization", required = false) String authHeader) throws ParseException, JOSEException {
        if (authHeader == null || authHeader.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "No token provided."));
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

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<String>> forgotPassword(@RequestBody Map<String, String> request, @RequestParam("type") String type) {
        authService.sendOtpForgotPassword(request.get("email"), type);
        return ResponseEntity.ok(ApiResponse.success("Mã OTP đã được gửi tới email của bạn"));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<String>> resetPassword(@RequestBody ResetPasswordRequest request, @RequestParam("type") String type) {
        authService.resetPassword(request, type);
        return ResponseEntity.ok(ApiResponse.success("Đặt lại mật khẩu thành công"));
    }

    @Getter
    @Setter
    public static class ChangePasswordRequest {
        private String currentPassword;
        private String newPassword;
        private String confirmPassword;
    }
}
