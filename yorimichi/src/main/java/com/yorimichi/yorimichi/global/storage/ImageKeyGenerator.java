package com.yorimichi.yorimichi.global.storage;

import com.yorimichi.yorimichi.global.error.CustomException;
import com.yorimichi.yorimichi.global.error.ErrorCode;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.UUID;

/**
 * 저장소 구현체(local/S3)가 공통으로 쓰는 키 생성 로직.
 * "yyyy-MM-dd/UUID.확장자" 형태로 날짜별로 분산 저장합니다.
 */
final class ImageKeyGenerator {

    private ImageKeyGenerator() {
    }

    static String generate(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
        }

        String dateDir = LocalDate.now().toString();

        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename != null && originalFilename.contains(".")
                ? originalFilename.substring(originalFilename.lastIndexOf("."))
                : "";

        return dateDir + "/" + UUID.randomUUID() + extension;
    }
}
