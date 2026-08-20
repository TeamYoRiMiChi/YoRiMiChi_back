package com.yorimichi.yorimichi.global.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.yorimichi.yorimichi.global.error.CustomException;
import com.yorimichi.yorimichi.global.error.ErrorCode;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.UUID;

@Component
public class FileUploadUtil {

    @Value("${file.upload.windows}")
    private String windowsPath;

    @Value("${file.upload.linux}")
    private String linuxPath;

    private String resolveBasePath() {
        String os = System.getProperty("os.name").toLowerCase();
        return os.contains("win") ? windowsPath : linuxPath;
    }

    public String upload(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
        }

        try {
            String dateDir = LocalDate.now().toString();  // yyyy-MM-dd
            Path uploadDir = Paths.get(resolveBasePath(), dateDir);

            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }

            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename != null && originalFilename.contains(".")
                    ? originalFilename.substring(originalFilename.lastIndexOf("."))
                    : "";
            String savedFilename = UUID.randomUUID() + extension;

            Path targetPath = uploadDir.resolve(savedFilename);
            file.transferTo(targetPath);

            return dateDir + "/" + savedFilename;   // DB에 저장할 상대 경로

        } catch (IOException e) {
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    public void delete(String relativePath) {
        try {
            Path target = Paths.get(resolveBasePath(), relativePath);
            Files.deleteIfExists(target);
        } catch (IOException e) {
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}