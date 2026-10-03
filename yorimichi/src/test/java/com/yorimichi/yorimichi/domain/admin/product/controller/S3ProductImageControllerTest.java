package com.yorimichi.yorimichi.domain.admin.product.controller;

import com.yorimichi.yorimichi.domain.admin.product.service.AdminProductImageService;
import com.yorimichi.yorimichi.domain.admin.product.service.AdminProductService;
import com.yorimichi.yorimichi.domain.admin.product.service.S3ProductImageService;
import com.yorimichi.yorimichi.global.auth.CurrentMemberId;
import com.yorimichi.yorimichi.global.error.GlobalExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class S3ProductImageControllerTest {

    @Test
    void routesJsonKeysAndMultipartFilesToTheirOwnServices() throws Exception {
        var registration = mock(S3ProductImageService.class);
        var legacyImages = mock(AdminProductImageService.class);
        when(registration.registerImages(anyLong(), anyLong(), anyList())).thenReturn(List.of());
        when(legacyImages.addImages(anyLong(), anyList())).thenReturn(List.of());
        var mvc = MockMvcBuilders.standaloneSetup(
                        new S3ProductImageController(registration),
                        new AdminProductController(mock(AdminProductService.class), legacyImages))
                .setControllerAdvice(new GlobalExceptionHandler())
                .setCustomArgumentResolvers(new HandlerMethodArgumentResolver() {
                    public boolean supportsParameter(MethodParameter parameter) {
                        return parameter.hasParameterAnnotation(CurrentMemberId.class);
                    }
                    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer container,
                            NativeWebRequest request, WebDataBinderFactory binder) {
                        return 15L;
                    }
                }).build();

        mvc.perform(post("/api/admin/products/7/images")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"imageKeys\":[\"uploaded.gif\"]}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.success").value(true));
        verify(registration).registerImages(15L, 7L, List.of("uploaded.gif"));

        mvc.perform(multipart("/api/admin/products/7/images")
                        .file(new MockMultipartFile("files", "image.gif", "image/gif", new byte[] {1})))
                .andExpect(status().isOk());
        verify(legacyImages).addImages(eq(7L), anyList());

        mvc.perform(post("/api/admin/products/7/images")
                        .contentType(MediaType.APPLICATION_JSON).content("{\"imageKeys\":[]}"))
                .andExpect(status().isBadRequest());
        verifyNoMoreInteractions(registration);
    }
}
