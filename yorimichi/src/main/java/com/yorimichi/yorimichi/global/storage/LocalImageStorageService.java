package com.yorimichi.yorimichi.global.storage;

import com.yorimichi.yorimichi.global.error.CustomException;
import com.yorimichi.yorimichi.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * local 프로필 전용 이미지 저장소.
 *
 * 디스크(file.upload.windows / file.upload.linux)에 직접 저장하고,
 * WebConfig에 등록된 /images/** 핸들러가 이 서버 자신을 통해 직접 서빙합니다.
 * (기존 FileUploadUtil과 동일한 저장 방식이며, 저장 로직만 이 클래스로 옮겼습니다.)
 */
@Service
@Profile("local")
@RequiredArgsConstructor
public class LocalImageStorageService implements ImageStorageService {

    private final LocalUploadPathResolver pathResolver;

    @Value("${app.image.local-base-url:http://localhost:9000/images}")
    private String localBaseUrl;

    @Override
    public String upload(MultipartFile file) {
        String key = ImageKeyGenerator.generate(file);

        try {
            Path targetPath = Paths.get(pathResolver.resolveBasePath(), key);
            Files.createDirectories(targetPath.getParent());
            file.transferTo(targetPath);
            return key;
        } catch (IOException e) {
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public void delete(String key) {
        try {
            Path target = Paths.get(pathResolver.resolveBasePath(), key);
            Files.deleteIfExists(target);
        } catch (IOException e) {
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public String getUrl(String key) {
        return localBaseUrl + "/" + key;
    }
}
