package com.yorimichi.yorimichi.global.storage;

import org.springframework.web.multipart.MultipartFile;

/**
 * 이미지 파일 저장소 추상화.
 *
 * local 프로필에서는 로컬 디스크에({@link LocalImageStorageService}),
 * rds(AWS) 프로필에서는 S3에({@link S3ImageStorageService}) 저장합니다.
 * 호출하는 쪽(서비스/컨트롤러)은 어떤 구현체가 활성화됐는지 몰라도 되고,
 * DB에는 항상 상대 키만 저장한 뒤 화면에 보여줄 때 getUrl()로 실제 주소를 조립합니다.
 */
public interface ImageStorageService {

    /**
     * 파일을 저장하고, DB에 저장할 상대 키를 돌려줍니다.
     * 예: "2026-10-01/3f1e2c7a-....jpg"
     */
    String upload(MultipartFile file);

    /** key에 해당하는 파일을 삭제합니다. */
    void delete(String key);

    /** key를 화면에서 바로 쓸 수 있는 전체 URL로 변환합니다. */
    String getUrl(String key);
}
