package iuh.fit.userservice.controller;

import iuh.fit.userservice.dto.request.EmployerCompanyRequest;
import iuh.fit.userservice.dto.request.EmployerProfileUpdateRequest;
import iuh.fit.userservice.dto.response.ApiResponse;
import iuh.fit.userservice.dto.response.EmployerCompanyResponse;
import iuh.fit.userservice.dto.response.EmployerProfileResponse;
import iuh.fit.userservice.model.Employer;
import iuh.fit.userservice.service.EmployerService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user/employer")
public class EmployerController {
    private final EmployerService employerService;

    public EmployerController(EmployerService employerService) {
        this.employerService = employerService;
    }

    /** Link employer to company (existing) */
    @PostMapping("/save")
    public Employer saveCompanyForEmployer(@RequestBody EmployerCompanyRequest dto) {
        Employer employer = employerService.findById(dto.getEmployerId());
        if (employer == null) {
            throw new RuntimeException("Employer not found: " + dto.getEmployerId());
        }
        employer.setCompanyId(dto.getCompanyId());
        return employerService.save(employer);
    }

    /** Get employer by ID (inter-service / admin) */
    @GetMapping("/{employerId}")
    public EmployerCompanyResponse getEmployerById(@PathVariable String employerId) {
        Employer employer = employerService.findById(employerId);
        if (employer == null) {
            throw new RuntimeException("Employer not found: " + employerId);
        }
        return new EmployerCompanyResponse(
                employer.getEmployerId(),
                employer.getCompanyId()
        );
    }

    /** Get full profile by ID */
    @GetMapping("/{employerId}/profile")
    public ResponseEntity<ApiResponse<EmployerProfileResponse>> getProfileById(
            @PathVariable String employerId) {
        return ResponseEntity.ok(ApiResponse.success(employerService.getProfile(employerId)));
    }

    /** Get own profile (userId from API Gateway header) */
    @GetMapping("/profile/me")
    public ResponseEntity<ApiResponse<EmployerProfileResponse>> getMyProfile(
            @RequestHeader("X-User-Id") String userId) {
        return ResponseEntity.ok(ApiResponse.success(employerService.getProfile(userId)));
    }

    /** Update own profile */
    @PutMapping("/profile/me")
    public ResponseEntity<ApiResponse<EmployerProfileResponse>> updateMyProfile(
            @RequestHeader("X-User-Id") String userId,
            @RequestBody @Valid EmployerProfileUpdateRequest dto) {
        return ResponseEntity.ok(ApiResponse.success(employerService.updateProfile(userId, dto)));
    }
}
