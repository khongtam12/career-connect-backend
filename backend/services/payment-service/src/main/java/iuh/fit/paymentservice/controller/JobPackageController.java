package iuh.fit.paymentservice.controller;

import iuh.fit.paymentservice.dto.response.JobPackageResponseDTO;
import iuh.fit.paymentservice.service.JobPackageService;
import lombok.Getter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/package")
public class JobPackageController {
    private final JobPackageService jobPackageService;

    public JobPackageController(JobPackageService jobPackageService) {
        this.jobPackageService = jobPackageService;
    }

    @GetMapping
    public ResponseEntity<List<JobPackageResponseDTO>> getAllJobPackage(){
        List<JobPackageResponseDTO> list= jobPackageService.getAllJobPackage();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{packageId}")
    public ResponseEntity<JobPackageResponseDTO> getJobPackageById(@PathVariable String packageId) {
        return ResponseEntity.ok(jobPackageService.getJobPackageById(packageId));
    }
}
