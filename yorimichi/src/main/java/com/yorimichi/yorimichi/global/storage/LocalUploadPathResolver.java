package com.yorimichi.yorimichi.global.storage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * local 프로필의 업로드 디렉터리 경로를 결정합니다.
 *
 * {@link LocalImageStorageService}(파일 쓰기/삭제)와
 * WebConfig(정적 리소스 서빙) 양쪽에서 같은 경로를 써야 하므로 공용으로 뽑아뒀습니다.
 * 프로필에 묶여있지 않은 평범한 빈이라, rds 프로필로 뜰 때도 문제없이 생성됩니다
 * (실제로 쓰이는 건 WebConfig의 /images/** 매핑 정도이며, 해당 디렉터리가
 * 없어도 서버 구동에는 영향이 없습니다).
 */
@Component
public class LocalUploadPathResolver {

    @Value("${file.upload.windows}")
    private String windowsPath;

    @Value("${file.upload.linux}")
    private String linuxPath;

    public String resolveBasePath() {
        String os = System.getProperty("os.name").toLowerCase();
        return os.contains("win") ? windowsPath : linuxPath;
    }
}
