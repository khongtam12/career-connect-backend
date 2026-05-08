package iuh.fit.applicationservice.service;

import iuh.fit.applicationservice.dto.response.FileUploadResponse;
import iuh.fit.applicationservice.exception.AppException;
import iuh.fit.applicationservice.exception.ErrorCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
public class ApplicationFileStorageService {
    private final S3Client s3Client;

    @Value("${AWS_BUCKET}")
    private String bucketName;

    public ApplicationFileStorageService(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    public FileUploadResponse uploadApplicationFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new AppException(ErrorCode.INVALID_FILE);
        }

        String originalFilename = file.getOriginalFilename() != null
                ? StringUtils.cleanPath(file.getOriginalFilename())
                : "file";
        String objectKey = buildObjectKey(originalFilename);
        String contentType = StringUtils.hasText(file.getContentType())
                ? file.getContentType()
                : "application/octet-stream";

        Map<String, String> metadata = new HashMap<>();
        metadata.put("original-filename", URLEncoder.encode(originalFilename, StandardCharsets.UTF_8));

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(objectKey)
                .contentType(contentType)
                .metadata(metadata)
                .build();

        try (InputStream inputStream = file.getInputStream()) {
            s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(inputStream, file.getSize()));
        } catch (IOException | S3Exception e) {
            e.printStackTrace();
            System.err.println("S3 Upload Failed: " + e.getMessage());
            throw new AppException(ErrorCode.FILE_UPLOAD_FAILED);
        }

        String fileUrl = s3Client.utilities()
                .getUrl(builder -> builder.bucket(bucketName).key(objectKey))
                .toExternalForm();

        return new FileUploadResponse(objectKey, fileUrl, originalFilename);
    }

    private String buildObjectKey(String originalFilename) {
        String extension = "";
        int lastDotIndex = originalFilename.lastIndexOf('.');
        if (lastDotIndex >= 0) {
            extension = originalFilename.substring(lastDotIndex);
        }
        return "applications/" + UUID.randomUUID() + extension;
    }
}
