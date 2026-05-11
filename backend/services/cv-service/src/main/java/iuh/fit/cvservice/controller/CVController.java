package iuh.fit.cvservice.controller;

import iuh.fit.cvservice.dto.AIReviewResponse;
import iuh.fit.cvservice.model.CV;
import iuh.fit.cvservice.service.AIService;
import iuh.fit.cvservice.service.CVService;
import iuh.fit.cvservice.service.PDFExportService;
import iuh.fit.cvservice.service.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/cvs")
@RequiredArgsConstructor
@Slf4j
public class CVController {

    private final CVService cvService;
    private final S3Service s3Service;
    private final PDFExportService pdfExportService;
    private final AIService aiService;

    @GetMapping("/my-cvs")
    public ResponseEntity<?> getMyCVs(@RequestHeader("X-User-Id") String userId) {
        log.info("Fetching CVs for user: {}", userId);
        return ResponseEntity.ok(cvService.getCVsByUserId(userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CV> getCVById(@PathVariable("id") UUID id) {
        try {
            return ResponseEntity.ok(cvService.getCVById(id));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<CV> saveCV(@RequestBody CV cv, @RequestHeader("X-User-Id") String userId) {
        log.info("Saving CV for user: {}", userId);
        cv.setUserId(userId);
        return ResponseEntity.ok(cvService.saveCV(cv));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CV> updateCV(@PathVariable("id") UUID id, @RequestBody CV cv, @RequestHeader("X-User-Id") String userId) {
        log.info("Updating CV {} for user: {}", id, userId);
        cv.setId(id);
        cv.setUserId(userId);
        return ResponseEntity.ok(cvService.saveCV(cv));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCV(@PathVariable("id") UUID id) {
        cvService.deleteCV(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/upload-avatar")
    public ResponseEntity<String> uploadAvatar(@RequestParam("file") MultipartFile file) {
        try {
            String url = s3Service.uploadFile(file);
            return ResponseEntity.ok(url);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Upload failed: " + e.getMessage());
        }
    }

    @PostMapping("/{id}/upload-pdf")
    public ResponseEntity<CV> uploadPDF(@PathVariable("id") UUID id, @RequestParam("file") MultipartFile file) {
        try {
            return ResponseEntity.ok(cvService.uploadCVFile(id, file));
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/upload/presigned-url")
    public ResponseEntity<String> getPresignedUrl(@RequestParam String key, @RequestParam String contentType) {
        return ResponseEntity.ok(s3Service.generatePresignedUrl(key, contentType));
    }

    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> exportPDF(@PathVariable("id") UUID id, @CookieValue(value = "access_token", required = false) String token) {
        try {
            log.info("Request to export CV {} to PDF", id);
            byte[] pdf = pdfExportService.exportCVToPDF(id.toString(), token);
            
            return ResponseEntity.ok()
                    .header("Content-Type", "application/pdf")
                    .header("Content-Disposition", "attachment; filename=CV_" + id + ".pdf")
                    .body(pdf);
        } catch (Exception e) {
            log.error("Error exporting PDF: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/{id}/ai-review")
    public ResponseEntity<AIReviewResponse> reviewCV(@PathVariable("id") UUID id) {
        log.info("Request AI review for CV: {}", id);
        CV cv = cvService.getCVById(id);
        return ResponseEntity.ok(aiService.reviewCV(cv));
    }
}
