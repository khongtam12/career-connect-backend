package iuh.fit.cvservice.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import java.net.URL;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final AmazonS3 amazonS3;

    @Value("${AWS_BUCKET}")
    private String bucketName;

    public String generatePresignedUrl(String key, String contentType) {
        Date expiration = new Date();
        long expTimeMillis = expiration.getTime();
        expTimeMillis += 1000 * 60 * 10; // 10 minutes
        expiration.setTime(expTimeMillis);

        GeneratePresignedUrlRequest generatePresignedUrlRequest =
                new GeneratePresignedUrlRequest(bucketName, key)
                        .withMethod(HttpMethod.PUT)
                        .withExpiration(expiration);
        
        if (contentType != null) {
            generatePresignedUrlRequest.setContentType(contentType);
        }

        URL url = amazonS3.generatePresignedUrl(generatePresignedUrlRequest);
        return url.toString();
    }

    public String uploadFile(MultipartFile file) throws IOException {
        String fileName = "avatars/" + UUID.randomUUID() + "_" + file.getOriginalFilename();
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        metadata.setContentType(file.getContentType());

        amazonS3.putObject(new PutObjectRequest(bucketName, fileName, file.getInputStream(), metadata));
        return amazonS3.getUrl(bucketName, fileName).toString();
    }

    public String uploadCVFile(MultipartFile file) throws IOException {
        String fileName = "cvs/" + UUID.randomUUID() + "_" + file.getOriginalFilename();
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        metadata.setContentType("application/pdf");

        amazonS3.putObject(new PutObjectRequest(bucketName, fileName, file.getInputStream(), metadata));
        return amazonS3.getUrl(bucketName, fileName).toString();
    }

    public void deleteFile(String fileUrl) {
        if (fileUrl == null || fileUrl.isEmpty()) return;
        try {
            // Extract key from URL: https://bucket.s3.region.amazonaws.com/folder/filename
            // The key is the part after the bucket name or the first single slash after domain
            String key = fileUrl.substring(fileUrl.indexOf(".com/") + 5);
            amazonS3.deleteObject(bucketName, key);
        } catch (Exception e) {
            // Log error but don't fail the save process
            System.err.println("Failed to delete S3 object: " + e.getMessage());
        }
    }
}
