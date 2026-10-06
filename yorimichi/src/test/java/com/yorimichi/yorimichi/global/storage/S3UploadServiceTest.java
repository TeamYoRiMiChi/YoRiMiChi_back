package com.yorimichi.yorimichi.global.storage;

import com.yorimichi.yorimichi.global.error.CustomException;
import com.yorimichi.yorimichi.global.error.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.core.ResponseInputStream;
import java.io.ByteArrayInputStream;
import java.util.Base64;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class S3UploadServiceTest {

    private static final String KEY = "products/15/12345678-1234-1234-1234-123456789abc.gif";
    private S3Client s3Client;
    private S3UploadService service;

    @BeforeEach
    void setUp() {
        s3Client = mock(S3Client.class);
        service = new S3UploadService(mock(S3Presigner.class), s3Client);
        ReflectionTestUtils.setField(service, "bucket", "test-images");
    }

    @Test
    void signsGifUploadForCurrentMemberWithoutCallingS3() {
        // 실제 서명 생성은 테스트 자격증명으로 로컬에서 실행합니다.
        try (S3Presigner presigner = S3Presigner.builder()
                .region(Region.AP_NORTHEAST_2)
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create("test-access-key", "test-secret-key")))
                .build()) {
            S3UploadService signingService = new S3UploadService(presigner, s3Client);
            ReflectionTestUtils.setField(signingService, "bucket", "test-images");
            var response = signingService.createUploadUrl(15L, "image/gif");
            assertThat(response.imageKey()).matches("products/15/[0-9a-f-]{36}\\.gif");
            assertThat(response.uploadUrl()).contains(response.imageKey(), "X-Amz-Signature=", "X-Amz-Expires=300");
            verifyNoInteractions(s3Client);
        }
    }

    @Test
    void rejectsAnonymousUpload() {
        assertError(() -> service.createUploadUrl(null, "image/gif"), ErrorCode.UNAUTHORIZED);
    }

    @Test
    void rejectsAnotherMembersKeyBeforeAccessingS3() {
        assertError(() -> service.validateUploadedImage(20L, KEY), ErrorCode.INVALID_INPUT_VALUE);
        verifyNoInteractions(s3Client);
    }

    @Test
    void acceptsUploadedGif() {
        byte[] gif = Base64.getDecoder().decode("R0lGODlhAQABAIAAAAAAAP///yH5BAEAAAAALAAAAAABAAEAAAIBRAA7");
        when(s3Client.headObject(any(HeadObjectRequest.class)))
                .thenReturn(HeadObjectResponse.builder().contentType("image/gif").contentLength((long) gif.length)
                        .eTag("etag").build());
        when(s3Client.getObject(any(GetObjectRequest.class))).thenReturn(new ResponseInputStream<>(
                GetObjectResponse.builder().build(), new ByteArrayInputStream(gif)));
        assertThatCode(() -> service.validateUploadedImage(15L, KEY)).doesNotThrowAnyException();
        verify(s3Client, never()).deleteObject(any(DeleteObjectRequest.class));
        verify(s3Client).getObject(argThat((GetObjectRequest request) -> "etag".equals(request.ifMatch())));
    }

    @Test
    void rejectsOversizedObjectWithoutDownloadingAndDeletesIt() {
        when(s3Client.headObject(any(HeadObjectRequest.class))).thenReturn(HeadObjectResponse.builder()
                .contentType("image/gif").contentLength(10 * 1024 * 1024 + 1L).build());
        assertError(() -> service.validateUploadedImage(15L, KEY), ErrorCode.INVALID_IMAGE_FILE);
        verify(s3Client, never()).getObject(any(GetObjectRequest.class));
        verify(s3Client).deleteObject(any(DeleteObjectRequest.class));
    }

    @Test
    void rejectsAndDeletesTextDisguisedAsGif() {
        byte[] text = "not an image".getBytes(java.nio.charset.StandardCharsets.UTF_8);
        when(s3Client.headObject(any(HeadObjectRequest.class))).thenReturn(HeadObjectResponse.builder()
                .contentType("image/gif").contentLength((long) text.length).build());
        when(s3Client.getObject(any(GetObjectRequest.class))).thenReturn(new ResponseInputStream<>(
                GetObjectResponse.builder().build(), new ByteArrayInputStream(text)));
        assertError(() -> service.validateUploadedImage(15L, KEY), ErrorCode.INVALID_IMAGE_FILE);
        verify(s3Client).deleteObject(any(DeleteObjectRequest.class));
    }

    @Test
    void doesNotDeleteImageWhenS3ReadFails() {
        when(s3Client.headObject(any(HeadObjectRequest.class))).thenReturn(HeadObjectResponse.builder()
                .contentType("image/gif").contentLength(42L).build());
        when(s3Client.getObject(any(GetObjectRequest.class)))
                .thenThrow(software.amazon.awssdk.services.s3.model.S3Exception.builder().statusCode(503).build());
        assertError(() -> service.validateUploadedImage(15L, KEY), ErrorCode.INTERNAL_SERVER_ERROR);
        verify(s3Client, never()).deleteObject(any(DeleteObjectRequest.class));
    }

    @Test
    void rejectsMissingImage() {
        when(s3Client.headObject(any(HeadObjectRequest.class)))
                .thenThrow(NoSuchKeyException.builder().statusCode(404).message("missing").build());
        assertError(() -> service.validateUploadedImage(15L, KEY), ErrorCode.PRODUCT_IMAGE_NOT_FOUND);
    }

    @Test
    void rejectsMismatchedContentType() {
        when(s3Client.headObject(any(HeadObjectRequest.class)))
                .thenReturn(HeadObjectResponse.builder().contentType("image/png").contentLength(42L).build());
        assertError(() -> service.validateUploadedImage(15L, KEY), ErrorCode.INVALID_IMAGE_FILE);
    }

    private void assertError(Runnable action, ErrorCode error) {
        assertThatThrownBy(action::run).isInstanceOfSatisfying(CustomException.class,
                exception -> assertThat(exception.getErrorCode()).isEqualTo(error));
    }
}
