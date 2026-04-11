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
@RequestMapping("/api/user/auth")
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
                .secure(false)
                .sameSite("Lax")
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
    public ResponseEntity<?> getCurrentUser(@CookieValue(name = "access_token",required = false) String token) throws ParseException, JOSEException {
       if(token==null){
           return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
       }
        var user=authService.getCurrentUser(token);
        return ResponseEntity.ok(user);


    }
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        ResponseCookie cookie=ResponseCookie.from("access_token","")
                .httpOnly(true)
                .secure(false)
                .sameSite("Lax")
                .path("/")
                .maxAge(0)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE,cookie.toString());
        return ResponseEntity.ok(Map.of("message", "Logged out successfully"));
    }


}
