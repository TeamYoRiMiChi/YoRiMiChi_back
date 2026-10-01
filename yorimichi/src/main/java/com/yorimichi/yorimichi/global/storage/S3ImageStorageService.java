package com.yorimichi.yorimichi.global.storage;

import com.yorimichi.yorimichi.global.error.CustomException;
import com.yorimichi.yorimichi.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;

/**
 * rds(AWS) 프로필 전용 이미지 저장소. S3 버킷에 저장합니다.
 *
 * S3Client 빈은 S3ClientConfig에서 rds 프로필일 때만 만들어지므로,
 * local 프로필로 띄울 때는 AWS 자격증명/네트워크가 없어도 서버 구동에 영향이 없습니다.
 *
 * app.image.s3.bucket은 실제 S3 버킷을 만든 뒤 환경변수(S3_IMAGE_BUCKET)로 넣어주세요.
 * (이 버킷 자체는 아직 Terraform에 없어서, 배포 전에 별도로 만들어야 합니다.)
 */
@Slf4j
@Service
@Profile("rds")
@RequiredArgsConstructor
public class S3ImageStorageService implements ImageStorageService {

    private final S3Client s3Client;

    @Value("${app.image.s3.bucket}")
    private String bucket;

    @Value("${app.image.s3.region}")
    private String region;

    @Value("${app.image.s3.public-base-url:}")
    private String publicBaseUrl;

    @Override
    public String upload(MultipartFile file) {
        String key = ImageKeyGenerator.generate(file);

        try {
            s3Client.putObject(
                    PutObjectRequest.builder()
                            .bucket(bucket)
                            .key(key)
                            .contentType(file.getContentType())
                            .build(),
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize())
            );
            return key;
        } catch (IOException | SdkException e) {
            log.error("Failed to upload image to S3", e);
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public void delete(String key) {
        try {
            s3Client.deleteObject(DeleteObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .build());
        } catch (SdkException e) {
            log.error("Failed to delete image from S3", e);
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public String getUrl(String key) {
        String base = (publicBaseUrl == null || publicBaseUrl.isBlank())
                ? String.format("https://%s.s3.%s.amazonaws.com", bucket, region)
                : publicBaseUrl;

        return base + "/" + key;
    }
}
