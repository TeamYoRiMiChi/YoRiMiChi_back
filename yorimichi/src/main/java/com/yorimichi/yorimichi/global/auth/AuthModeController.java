package com.yorimichi.yorimichi.global.auth;

import com.yorimichi.yorimichi.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthModeController {
    private final Environment environment;

    @GetMapping("/api/auth/config")
    public ApiResponse<Mode> config() {
        return ApiResponse.success(new Mode(
                environment.acceptsProfiles(Profiles.of("local")) ? "local" : "cognito",
                environment.acceptsProfiles(Profiles.of("rds")) ? "s3" : "multipart"));
    }

    public record Mode(String mode, String imageUploadMode) {}
}
