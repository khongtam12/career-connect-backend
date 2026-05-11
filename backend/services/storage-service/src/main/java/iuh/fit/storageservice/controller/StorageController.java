package iuh.fit.storageservice.controller;

import iuh.fit.storageservice.service.S3Service;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/storage")
public class StorageController {
    private final S3Service s3Service;

    public StorageController(S3Service s3Service) {
        this.s3Service = s3Service;
    }
    @GetMapping("/presigned-url")
    public ResponseEntity<?> getPresignedUrl(
            @RequestParam String key,
            @RequestParam String contentType
    ) {
        try {
            System.out.println("KEY = " + key);
            System.out.println("TYPE = " + contentType);

            String url = s3Service.generatePresignedUrl(key, contentType);
            String fileUrl = s3Service.getFileUrl(key);

            return ResponseEntity.ok(Map.of(
                    "uploadUrl", url,
                    "fileUrl", fileUrl
            ));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(e.getMessage());
        }

    }
    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteFile(@RequestParam String key) {
        s3Service.deleteFile(key);
        return ResponseEntity.ok("Deleted");
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            Map<String, String> res = s3Service.uploadFile(file);
            return ResponseEntity.ok(Map.of(
                    "status", 200,
                    "message", "Upload success",
                    "data", res
            ));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("message", e.getMessage()));
        }
    }
}
