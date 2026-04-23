package iuh.fit.cvservice.controller;

import iuh.fit.cvservice.model.CV;
import iuh.fit.cvservice.service.CVService;
import iuh.fit.cvservice.service.S3Service;
import iuh.fit.cvservice.service.PDFExportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/cvs")
@RequiredArgsConstructor
@Slf4j
public class CVController {

    private final CVService cvService;
    private final S3Service s3Service;
    private final PDFExportService pdfExportService;

    @GetMapping("/my-cvs")
    public ResponseEntity<?> getMyCVs(@RequestHeader("X-User-Id") String userId) {
        try {
            log.info("Fetching CVs for user: {}", userId);
            List<CV> cvs = cvService.getCVsByUserId(UUID.fromString(userId));
            log.info("Found {} CVs for user {}", cvs.size(), userId);
            return ResponseEntity.ok(cvs);
        } catch (Exception e) {
            log.error("Error fetching CVs for user {}: ", userId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCVById(@PathVariable UUID id) {
        try {
            log.info("Fetching CV by ID: {}", id);
            CV cv = cvService.getCVById(id);
            log.info("Returning CV details: {}", cv);
            return ResponseEntity.ok(cv);
        } catch (Exception e) {
            log.error("Error fetching CV {}: ", id, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<?> createCV(@RequestBody CV cv, @RequestHeader("X-User-Id") String userId) {
        try {
            log.info("Receiving new CV for user {}: {}", userId, cv);
            cv.setUserId(UUID.fromString(userId));
            CV savedCV = cvService.saveCV(cv);
            log.info("Successfully saved CV with ID: {}", savedCV.getId());
            return ResponseEntity.ok(savedCV);
        } catch (Exception e) {
            log.error("Error creating CV: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCV(@PathVariable UUID id, @RequestBody CV cv, @RequestHeader("X-User-Id") String userId) {
        try {
            log.info("Receiving update for CV {} (user {}): {}", id, userId, cv);
            cv.setId(id);
            cv.setUserId(UUID.fromString(userId));
            CV updatedCV = cvService.saveCV(cv);
            log.info("Successfully updated CV: {}", updatedCV.getId());
            return ResponseEntity.ok(updatedCV);
        } catch (Exception e) {
            log.error("Error updating CV: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCV(@PathVariable UUID id) {
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

    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> exportPDF(@PathVariable UUID id, @CookieValue(value = "access_token", required = false) String token) {
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
}
