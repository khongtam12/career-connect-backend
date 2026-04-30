package iuh.fit.userservice.controller;

import iuh.fit.userservice.dto.request.RecruiterRequestDTO;
import iuh.fit.userservice.dto.response.ApiResponse;
import iuh.fit.userservice.dto.response.RecruiterPageDTO;
import iuh.fit.userservice.dto.response.RecruiterResponseDTO;
import iuh.fit.userservice.model.RecruiterStatus;
import iuh.fit.userservice.service.RecruiterService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/recruiters")
public class RecruiterController {

    private final RecruiterService recruiterService;

    public RecruiterController(RecruiterService recruiterService) {
        this.recruiterService = recruiterService;
    }

    @GetMapping
    public ApiResponse<RecruiterPageDTO> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.success(recruiterService.getAllRecruiters(page, size));
    }

    @PostMapping
    public ApiResponse<RecruiterResponseDTO> create(@RequestBody @Valid RecruiterRequestDTO dto) {
        return ApiResponse.created(recruiterService.createRecruiter(dto));
    }

    @PutMapping("/{id}")
    public ApiResponse<RecruiterResponseDTO> update(@PathVariable String id,
                                                    @RequestBody @Valid RecruiterRequestDTO dto) {
        return ApiResponse.success(recruiterService.updateRecruiter(id, dto));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable String id) {
        recruiterService.deleteRecruiter(id);
        return ApiResponse.noContent();
    }

    @PatchMapping("/{id}/status")
    public ApiResponse<RecruiterResponseDTO> patchStatus(@PathVariable String id,
                                                         @RequestBody Map<String, String> body) {
        RecruiterStatus status = RecruiterStatus.valueOf(body.get("status"));
        return ApiResponse.success(recruiterService.changeStatus(id, status));
    }
}
