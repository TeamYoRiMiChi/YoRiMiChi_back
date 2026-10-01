package com.yorimichi.yorimichi.global.config;

import com.yorimichi.yorimichi.global.auth.CurrentMemberIdArgumentResolver;
import com.yorimichi.yorimichi.global.storage.LocalUploadPathResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;
import java.util.List;

/**
 * MVC 관련 공통 설정
 *
 * CORS는 SecurityConfig에서 처리합니다.
 * Spring Security를 쓰면 Security 필터 체인이 먼저 요청을 가로채므로
 * 여기(WebConfig)에 CORS를 또 설정하면 두 군데를 관리해야 하고,
 * 값이 어긋났을 때 원인을 찾기 어려워집니다.
 *
 * 인터셉터, 정적 리소스 경로 등이 필요해지면 여기에 추가하세요.
 */
@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final CurrentMemberIdArgumentResolver currentMemberIdArgumentResolver;
    private final LocalUploadPathResolver localUploadPathResolver;

    /**
     * Registers custom controller argument resolvers.
     */
    @Override
    public void addArgumentResolvers(
            List<HandlerMethodArgumentResolver> resolvers
    ) {
        resolvers.add(currentMemberIdArgumentResolver);
    }

    /**
     * local 프로필에서 저장한 이미지를 /images/** 로 직접 서빙합니다.
     *
     * rds 프로필(S3 사용)에서는 이미지가 S3 URL로 바로 내려가므로 이 경로를 쓰지 않습니다.
     * 매핑 자체는 프로필과 무관하게 항상 등록되지만, rds 환경에서는 해당 디렉터리가
     * 없을 뿐이라 운영에는 영향이 없습니다.
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String basePath = localUploadPathResolver.resolveBasePath();
        String location = basePath.endsWith(File.separator) || basePath.endsWith("/")
                ? "file:" + basePath
                : "file:" + basePath + File.separator;

        registry.addResourceHandler("/images/**")
                .addResourceLocations(location);
    }
}
