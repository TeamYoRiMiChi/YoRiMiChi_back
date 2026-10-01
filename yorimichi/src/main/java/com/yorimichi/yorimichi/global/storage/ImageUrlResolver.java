package com.yorimichi.yorimichi.global.storage;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;

/**
 * DB에 저장된 이미지 값을 화면에서 바로 쓸 수 있는 전체 URL로 바꿉니다.
 *
 * PRODUCT_IMAGE.image_url / PRODUCT.thumbnail_url 에는 두 가지가 섞여 있을 수 있습니다.
 *   - 이미 완성된 주소 (http://, https://, / 로 시작) → 그대로 사용
 *   - 저장소 키 (예: 2026-10-01/uuid.jpg)           → 현재 프로필의 ImageStorageService.getUrl()로 변환
 *                                                     (local: 이 서버의 /images/**, rds: S3 주소)
 *
 * DTO 생성자처럼 스프링 빈을 주입받기 어려운 곳에서도 쓸 수 있게 static 메서드로 열어뒀습니다.
 * ImageStorageService 빈이 없는 프로필이면 값을 그대로 돌려줍니다.
 */
@Component
public class ImageUrlResolver {

    private static volatile ImageStorageService storage;

    public ImageUrlResolver(ObjectProvider<ImageStorageService> storageProvider) {
        storage = storageProvider.getIfAvailable();
    }

    public static String resolve(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        String v = value.trim();
        if (v.startsWith("http://") || v.startsWith("https://")
                || v.startsWith("//") || v.startsWith("/") || v.startsWith("data:")) {
            return v;
        }

        ImageStorageService current = storage;
        return current == null ? v : current.getUrl(v);
    }
}
