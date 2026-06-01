package iuh.fit.userservice.controller;

import iuh.fit.userservice.model.Candidate;
import iuh.fit.userservice.model.Status;
import iuh.fit.userservice.service.AdminCandidateService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user/admin/candidates")
public class AdminCandidateController {

    private final AdminCandidateService adminCandidateService;
    public AdminCandidateController(AdminCandidateService adminCandidateService) {
        this.adminCandidateService = adminCandidateService;
    }
    @GetMapping
    public ResponseEntity<Page<Candidate>> getCandidates(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Status status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        // Pageable in Spring is 0-indexed, so we subtract 1 from the requested page number
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Candidate> candidates = adminCandidateService.getCandidates(keyword, status, pageable);
        return ResponseEntity.ok(candidates);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Candidate> updateStatus(
            @PathVariable String id,
            @RequestBody UpdateStatusRequest request) {
        Candidate updatedCandidate = adminCandidateService.updateStatus(id, request.getStatus());
        return ResponseEntity.ok(updatedCandidate);
    }

    @PostMapping
    public ResponseEntity<Candidate> createCandidate(@RequestBody Candidate request) {
        Candidate createdCandidate = adminCandidateService.createCandidate(request);
        return ResponseEntity.ok(createdCandidate);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Candidate> updateCandidate(
            @PathVariable String id,
            @RequestBody Candidate request) {
        Candidate updatedCandidate = adminCandidateService.updateCandidate(id, request);
        return ResponseEntity.ok(updatedCandidate);
    }

    @PatchMapping("/{id}/reset-password")
    public ResponseEntity<Candidate> resetPassword(
            @PathVariable String id,
            @RequestBody ResetPasswordRequest request) {
        Candidate updatedCandidate = adminCandidateService.resetPassword(id, request.getNewPassword());
        return ResponseEntity.ok(updatedCandidate);
    }

    @GetMapping("/stats")
    public ResponseEntity<java.util.Map<String, Object>> getStats() {
        return ResponseEntity.ok(adminCandidateService.getCandidateStats());
    }


    static class ResetPasswordRequest {
        private String newPassword;

        public String getNewPassword() {
            return newPassword;
        }

        public void setNewPassword(String newPassword) {
            this.newPassword = newPassword;
        }
    }
//
    static class UpdateStatusRequest {
        private Status status;
        public Status getStatus() {
            return status;
        }
        public void setStatus(Status status) {
            this.status = status;
        }
    }
}
