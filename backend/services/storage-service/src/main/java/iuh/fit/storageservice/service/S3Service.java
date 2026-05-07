package iuh.fit.storageservice.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;

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


}
