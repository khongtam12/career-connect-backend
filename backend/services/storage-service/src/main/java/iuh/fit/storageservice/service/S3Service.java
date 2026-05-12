package iuh.fit.storageservice.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.io.InputStream;
import java.net.URLEncoder;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.util.StringUtils.cleanPath;
import static org.springframework.util.StringUtils.hasText;

@Service
public class S3Service {
private final S3Presigner s3Presigner;
private final S3Client s3Client;
@Value("${AWS_BUCKET}")
private  String bucketName ;

    public S3Service(S3Presigner s3Presigner, S3Client s3Client) {
        this.s3Presigner = s3Presigner;
        this.s3Client = s3Client;
    }


    public String generatePresignedUrl(String key, String contentType) {
        PutObjectRequest objectRequest=PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType(contentType)
                .build();
        PutObjectPresignRequest presignRequest=PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(10))
                .putObjectRequest(objectRequest).build();
        PresignedPutObjectRequest presignedRequest =s3Presigner.presignPutObject(presignRequest);
        return  presignedRequest.url().toString();


    }
    public String getFileUrl(String key){
        return  "https://"+bucketName+".s3.amazonaws.com/"+key;
    }
    public void deleteFile(String key) {
        DeleteObjectRequest request = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        s3Client.deleteObject(request);
    }

    public boolean doesObjectExist(String key) {
        try {
            s3Client.headObject(HeadObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build());
            return true;
        } catch (NoSuchKeyException e) {
            return false;
        }
    }

    public Map<String, String> uploadFile(MultipartFile file) {
        String originalFilename = file.getOriginalFilename() != null ?
               cleanPath(file.getOriginalFilename()) : "file";
        String objectKey = "applications/" + originalFilename;

        if (doesObjectExist(objectKey)) {
            deleteFile(objectKey);
            System.out.println("Deleted existing file: " + objectKey);
        }

        String contentType = hasText(file.getContentType())
                ? file.getContentType()
                : "application/octet-stream";

        Map<String, String> metadata = new HashMap<>();
        metadata.put("original-filename", URLEncoder.encode(originalFilename, UTF_8));

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(objectKey)
                .contentType(contentType)
                .metadata(metadata)
                .build();

        try (InputStream inputStream = file.getInputStream()) {
            s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(inputStream, file.getSize()));
        } catch (java.io.IOException | S3Exception e) {
            e.printStackTrace();
            System.err.println("S3 Upload Failed: " + e.getMessage());
            throw new RuntimeException("FILE_UPLOAD_FAILED");
        }

        String fileUrl = s3Client.utilities()
                .getUrl(builder -> builder.bucket(bucketName).key(objectKey))
                .toExternalForm();

        return java.util.Map.of(
            "id", objectKey,
            "url", fileUrl,
            "fileName", originalFilename
        );
    }
}
