package iuh.fit.companyservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient(name = "storage-service", path = "/api/v1/storage")
public interface StorageClient {

    @GetMapping("/presigned-url")
    Map<String, String> getPresignedUrl(
            @RequestParam("key") String key,
            @RequestParam("contentType") String contentType
    );

    @DeleteMapping("/delete")
    String deleteFile(@RequestParam("key") String key);
}
