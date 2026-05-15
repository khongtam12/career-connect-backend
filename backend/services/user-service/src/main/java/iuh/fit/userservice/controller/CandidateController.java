package iuh.fit.userservice.controller;

import iuh.fit.userservice.dto.request.CandidateProfileUpdateRequest;
import iuh.fit.userservice.dto.response.ApiResponse;
import iuh.fit.userservice.dto.response.CandidateProfileResponse;
import iuh.fit.userservice.dto.response.CandidateSummaryResponse;
import iuh.fit.userservice.service.CandidateService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user/candidate")
public class CandidateController {
    private final CandidateService candidateService;

    public CandidateController(CandidateService candidateService) {
        this.candidateService = candidateService;
    }

    /** Get candidate summary by ID (inter-service / admin) */
    @GetMapping("/{candidateId}")
    public CandidateSummaryResponse getCandidateById(@PathVariable String candidateId) {
        CandidateSummaryResponse candidate = candidateService.findById(candidateId);
        return candidate;
    }

    /** Get full profile by ID */
    @GetMapping("/{candidateId}/profile")
    public ResponseEntity<ApiResponse<CandidateProfileResponse>> getProfileById(
            @PathVariable String candidateId) {
        return ResponseEntity.ok(ApiResponse.success(candidateService.getProfile(candidateId)));
    }

    /** Get own profile (userId from API Gateway header) */
    @GetMapping("/profile/me")
    public ResponseEntity<ApiResponse<CandidateProfileResponse>> getMyProfile(
            @RequestHeader("X-User-Id") String userId) {
        return ResponseEntity.ok(ApiResponse.success(candidateService.getProfile(userId)));
    }

    /** Update own profile */
    @PutMapping("/profile/me")
    public ResponseEntity<ApiResponse<CandidateProfileResponse>> updateMyProfile(
            @RequestHeader("X-User-Id") String userId,
            @RequestBody @Valid CandidateProfileUpdateRequest dto) {
        return ResponseEntity.ok(ApiResponse.success(candidateService.updateProfile(userId, dto)));
    }
}
