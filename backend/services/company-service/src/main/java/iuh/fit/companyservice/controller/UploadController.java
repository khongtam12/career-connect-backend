package iuh.fit.companyservice.controller;

import iuh.fit.companyservice.Service.S3Service;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/company/upload")
public class UploadController {
    private final S3Service s3Service;

    public UploadController(S3Service s3Service) {
        this.s3Service = s3Service;
    }
    @GetMapping("/presigned-url")
    public ResponseEntity<?> getPresignedUrl(
            @RequestParam("key") String key,
            @RequestParam("contentType") String contentType
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
    public ResponseEntity<?> deleteFile(@RequestParam("key") String key) {
        s3Service.deleteFile(key);
        return ResponseEntity.ok("Deleted");
    }
}
