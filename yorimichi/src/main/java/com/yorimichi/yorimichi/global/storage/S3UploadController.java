package com.yorimichi.yorimichi.global.storage;

import com.yorimichi.yorimichi.global.auth.CurrentMemberId;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Profile("rds")
@RequestMapping("/api/admin/images")
@RequiredArgsConstructor
public class S3UploadController {

    private final S3UploadService s3UploadService;

    @PostMapping("/presigned-url")
    public S3UploadService.UploadResponse createUploadUrl(
            @CurrentMemberId Long memberId,
            @RequestBody UploadRequest request
    ) {
        return s3UploadService.createUploadUrl(
                memberId, request.contentType()
        );
    }

    public record UploadRequest(String contentType) {
    }
}
