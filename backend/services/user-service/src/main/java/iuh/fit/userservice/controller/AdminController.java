package iuh.fit.userservice.controller;

import iuh.fit.userservice.dto.request.AdminCreateRequest;
import iuh.fit.userservice.dto.request.AdminProfileUpdateRequest;
import iuh.fit.userservice.dto.response.AdminResponse;
import iuh.fit.userservice.dto.response.ApiResponse;
import iuh.fit.userservice.service.AdminService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/user/admin")
public class AdminController {
    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    /** List all admins */
    @GetMapping
    public ResponseEntity<ApiResponse<List<AdminResponse>>> findAll() {
        return ResponseEntity.ok(ApiResponse.success(adminService.findAllDto()));
    }

    /** Get single admin by ID */
    @GetMapping("/{adminId}")
    public ResponseEntity<ApiResponse<AdminResponse>> getById(@PathVariable String adminId) {
        return ResponseEntity.ok(ApiResponse.success(adminService.getById(adminId)));
    }

    /** Get own profile (userId from API Gateway header) */
    @GetMapping("/profile/me")
    public ResponseEntity<ApiResponse<AdminResponse>> getMyProfile(
            @RequestHeader("X-User-Id") String userId) {
        return ResponseEntity.ok(ApiResponse.success(adminService.getById(userId)));
    }

    /** Update own profile */
    @PutMapping("/profile/me")
    public ResponseEntity<ApiResponse<AdminResponse>> updateMyProfile(
            @RequestHeader("X-User-Id") String userId,
            @RequestBody @Valid AdminProfileUpdateRequest dto) {
        return ResponseEntity.ok(ApiResponse.success(adminService.updateProfile(userId, dto)));
    }

    /** Create a new admin */
    @PostMapping
    public ResponseEntity<ApiResponse<AdminResponse>> create(
            @RequestBody @Valid AdminCreateRequest dto) {
        return ResponseEntity.ok(ApiResponse.created(adminService.createAdmin(dto)));
    }
}
