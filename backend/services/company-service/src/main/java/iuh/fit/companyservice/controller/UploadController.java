package iuh.fit.companyservice.controller;

import iuh.fit.companyservice.client.StorageClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/company/upload")
public class UploadController {
    private final StorageClient storageClient;

    public UploadController(StorageClient storageClient) {
        this.storageClient = storageClient;
    }

    @GetMapping("/presigned-url")
    public ResponseEntity<?> getPresignedUrl(
            @RequestParam("key") String key,
            @RequestParam("contentType") String contentType
    ) {
        try {
            return ResponseEntity.ok(storageClient.getPresignedUrl(key, contentType));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteFile(@RequestParam("key") String key) {
        return ResponseEntity.ok(storageClient.deleteFile(key));
    }
}
