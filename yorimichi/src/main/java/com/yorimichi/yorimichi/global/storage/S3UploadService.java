package com.yorimichi.yorimichi.global.storage;

import com.yorimichi.yorimichi.global.error.CustomException;
import com.yorimichi.yorimichi.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;
import java.util.Map;
import java.util.UUID;

@Service
@Profile("rds")
@RequiredArgsConstructor
public class S3UploadService {

    private final S3Presigner s3Presigner;
    private final S3Client s3Client;

    @Value("${app.image.s3.bucket}")
    private String bucket;

    private static final Map<String, String> EXTENSIONS = Map.of(
            "image/jpeg", ".jpg",
            "image/png", ".png",
            "image/webp", ".webp",
            "image/gif", ".gif"
    );

    public UploadResponse createUploadUrl(Long memberId, String contentType) {
        requireMember(memberId);
        String extension = contentType == null
                ? null
                : EXTENSIONS.get(contentType);

        if (extension == null) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
        }

        String imageKey = "products/" + memberId + "/" + UUID.randomUUID() + extension;

        PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(imageKey)
                .contentType(contentType)
                .build();

        PutObjectPresignRequest presignRequest =
                PutObjectPresignRequest.builder()
                        .signatureDuration(Duration.ofMinutes(5))
                        .putObjectRequest(objectRequest)
                        .build();

        String uploadUrl = s3Presigner
                .presignPutObject(presignRequest)
                .url()
                .toString();

        return new UploadResponse(uploadUrl, imageKey);
    }

    /** 현재 회원의 키인지, S3에 허용된 타입의 파일이 업로드됐는지 확인합니다. */
    public void validateUploadedImage(Long memberId, String imageKey) {
        requireMember(memberId);
        String pattern = "^products/" + memberId
                + "/[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}"
                + "-[0-9a-f]{4}-[0-9a-f]{12}\\.(jpg|png|webp|gif)$";
        if (imageKey == null || !imageKey.matches(pattern)) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
        }

        HeadObjectResponse object;

        try {
            object = s3Client.headObject(
                    HeadObjectRequest.builder()
                            .bucket(bucket)
                            .key(imageKey)
                            .build()
            );
        } catch (S3Exception e) {
            throw new CustomException(e.statusCode() == 404
                    ? ErrorCode.PRODUCT_IMAGE_NOT_FOUND : ErrorCode.INTERNAL_SERVER_ERROR);
        } catch (SdkException e) {
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }

        String extension = object.contentType() == null
                ? null
                : EXTENSIONS.get(object.contentType());

        if (object.contentLength() == null
                || object.contentLength() <= 0
                || extension == null
                || !imageKey.endsWith(extension)) {
            throw new CustomException(ErrorCode.INVALID_IMAGE_FILE);
        }
    }

    private void requireMember(Long memberId) {
        if (memberId == null || memberId <= 0) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }
    }

    public record UploadResponse(String uploadUrl, String imageKey) {
    }
}
