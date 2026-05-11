package iuh.fit.userservice.controller;

import iuh.fit.userservice.dto.request.EmployerRequestDTO;
import iuh.fit.userservice.dto.response.ApiResponse;
import iuh.fit.userservice.dto.response.EmployerPageDTO;
import iuh.fit.userservice.dto.response.EmployerResponseDTO;
import iuh.fit.userservice.model.Status;
import iuh.fit.userservice.service.EmployerService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/recruiters")
public class AdminEmployerController {

    private final EmployerService employerService;

    public AdminEmployerController(EmployerService employerService) {
        this.employerService = employerService;
    }

    @GetMapping
    public ApiResponse<EmployerPageDTO> list(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Status status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.success(employerService.getAllEmployers(keyword, status, page, size));
    }

    @PostMapping
    public ApiResponse<EmployerResponseDTO> create(@RequestBody @Valid EmployerRequestDTO dto) {
        return ApiResponse.created(employerService.createEmployer(dto));
    }

    @PutMapping("/{id}")
    public ApiResponse<EmployerResponseDTO> update(@PathVariable String id,
                                                    @RequestBody @Valid EmployerRequestDTO dto) {
        return ApiResponse.success(employerService.updateEmployer(id, dto));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable String id) {
        employerService.deleteEmployer(id);
        return ApiResponse.noContent();
    }

    @PatchMapping("/{id}/status")
    public ApiResponse<EmployerResponseDTO> patchStatus(@PathVariable String id,
                                                         @RequestBody Map<String, String> body) {
        Status status = Status.valueOf(body.get("status"));
        return ApiResponse.success(employerService.changeStatus(id, status));
    }

    @GetMapping("/stats")
    public ApiResponse<Map<String, Object>> getStats() {
        return ApiResponse.success(employerService.getEmployerStats());
    }
}
