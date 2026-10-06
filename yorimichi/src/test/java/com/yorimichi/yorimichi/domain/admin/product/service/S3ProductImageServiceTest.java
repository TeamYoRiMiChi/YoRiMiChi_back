package com.yorimichi.yorimichi.domain.admin.product.service;

import com.yorimichi.yorimichi.domain.admin.product.repository.AdminProductImageMapper;
import com.yorimichi.yorimichi.global.error.CustomException;
import com.yorimichi.yorimichi.global.error.ErrorCode;
import com.yorimichi.yorimichi.global.storage.S3UploadService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class S3ProductImageServiceTest {

    private AdminProductImageMapper mapper;
    private S3UploadService uploads;
    private S3ProductImageService service;

    @BeforeEach
    void setUp() {
        mapper = mock(AdminProductImageMapper.class);
        uploads = mock(S3UploadService.class);
        service = new S3ProductImageService(mapper, uploads);
    }

    @Test
    void registersKeysWithFirstImageAsThumbnail() {
        when(mapper.lockProduct(7L)).thenReturn(7L);
        when(mapper.findMaxOrder(7L)).thenReturn(-1);
        service.registerImages(15L, 7L, List.of("first.gif", "second.png"));
        verify(uploads).validateUploadedImage(15L, "first.gif");
        verify(mapper).insert(7L, "first.gif", 0, true);
        verify(mapper).insert(7L, "second.png", 1, false);
    }

    @Test
    void preservesExistingThumbnailAndImageOrder() {
        when(mapper.lockProduct(7L)).thenReturn(7L);
        when(mapper.countThumbnail(7L)).thenReturn(1);
        when(mapper.findMaxOrder(7L)).thenReturn(3);
        service.registerImages(15L, 7L, List.of("next.gif"));
        verify(mapper).insert(7L, "next.gif", 4, false);
    }

    @Test
    void doesNotInsertAnyKeyIfLaterValidationFails() {
        when(mapper.lockProduct(7L)).thenReturn(7L);
        doThrow(new CustomException(ErrorCode.PRODUCT_IMAGE_NOT_FOUND))
                .when(uploads).validateUploadedImage(15L, "missing.gif");
        assertError(() -> service.registerImages(15L, 7L, List.of("first.gif", "missing.gif")),
                ErrorCode.PRODUCT_IMAGE_NOT_FOUND);
        verify(mapper, never()).insert(anyLong(), anyString(), anyInt(), anyBoolean());
    }

    @Test
    void rejectsImageLimitBeforeAccessingS3() {
        when(mapper.lockProduct(7L)).thenReturn(7L);
        when(mapper.countByProductId(7L)).thenReturn(10);
        assertError(() -> service.registerImages(15L, 7L, List.of("next.gif")),
                ErrorCode.PRODUCT_IMAGE_LIMIT_EXCEEDED);
        verifyNoInteractions(uploads);
    }

    @Test
    void rejectsRepeatedKeysInSameRequest() {
        assertError(() -> service.registerImages(15L, 7L, List.of("same.gif", "same.gif")),
                ErrorCode.INVALID_INPUT_VALUE);
        verifyNoInteractions(mapper, uploads);
    }

    @Test
    void rejectsAlreadyRegisteredImage() {
        when(mapper.lockProduct(7L)).thenReturn(7L);
        when(mapper.countByImageKey("same.gif")).thenReturn(1);
        assertError(() -> service.registerImages(15L, 7L, List.of("same.gif")), ErrorCode.INVALID_INPUT_VALUE);
        verifyNoInteractions(uploads);
        verify(mapper, never()).insert(anyLong(), anyString(), anyInt(), anyBoolean());
    }

    @Test
    void rejectsMissingProduct() {
        when(mapper.lockProduct(7L)).thenReturn(null);
        assertError(() -> service.registerImages(15L, 7L, List.of("next.gif")), ErrorCode.PRODUCT_NOT_FOUND);
        verifyNoInteractions(uploads);
    }

    private void assertError(Runnable action, ErrorCode error) {
        assertThatThrownBy(action::run).isInstanceOfSatisfying(CustomException.class,
                exception -> assertThat(exception.getErrorCode()).isEqualTo(error));
    }
}
