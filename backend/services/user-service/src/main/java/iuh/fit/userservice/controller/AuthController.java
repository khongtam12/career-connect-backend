package iuh.fit.userservice.controller;

import com.nimbusds.jose.JOSEException;
import iuh.fit.userservice.dto.request.AuthenticationRequest;
import iuh.fit.userservice.service.AuthenticationService;
import jakarta.servlet.http.HttpServletResponse;
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
    public ResponseEntity<?> login(@RequestBody AuthenticationRequest request, HttpServletResponse response
                                   ) {

        var authRes = authService.authenticate(request);

        ResponseCookie cookie=ResponseCookie.from("access_token",authRes.getToken())
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .path("/")
                .maxAge(3600)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE,cookie.toString());
        return ResponseEntity.ok(
                Map.of(
                        "token", authRes.getToken(),
                        "userId", authRes.getUserId()
                )
        );
    }
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@RequestHeader(value = "Authorization", required = false) String authHeader) throws ParseException, JOSEException {
        if(authHeader == null || authHeader.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).
                    body(Map.of("error", "No token provided."));
        }
        try {
            String token = authHeader.substring(7);
            var user = authService.getCurrentUser(token);
            return ResponseEntity.ok(user);
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));

        }





    }
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        ResponseCookie cookie=ResponseCookie.from("access_token","")
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .path("/")
                .maxAge(0)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE,cookie.toString());
        return ResponseEntity.ok(Map.of("message", "Logged out successfully"));
    }


}
