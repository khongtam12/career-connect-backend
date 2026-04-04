package iuh.fit.userservice.controller;

import com.nimbusds.jose.JOSEException;
import iuh.fit.userservice.dto.request.AuthenticationRequest;
import iuh.fit.userservice.service.AuthenticationService;
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
    public ResponseEntity<?> login(@RequestBody AuthenticationRequest request) {

        var authRes = authService.authenticate(request);

        return ResponseEntity.ok(
                Map.of(
                        "token", authRes.getToken(),
                        "userId", authRes.getUserId()
                )
        );
    }
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@RequestHeader("Authorization") String authHeader) throws ParseException, JOSEException {
        if(authHeader==null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body(Map.of("error","Missing token"));

        }
        String token=authHeader.substring(7);
        var user=authService.getCurrentUser(token);
        return ResponseEntity.ok(user);


    }
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            authService.logout(token);
        }
        return ResponseEntity.ok(Map.of("message", "Logged out successfully"));
    }


}
