package com.yorimichi.yorimichi.global.auth;

import org.junit.jupiter.api.Test;
import org.springframework.mock.env.MockEnvironment;

import static org.assertj.core.api.Assertions.assertThat;

class AuthModeControllerTest {

    @Test
    void advertisesS3OnlyWithRdsProfile() {
        var environment = new MockEnvironment();
        environment.setActiveProfiles("rds");
        var mode = new AuthModeController(environment).config().getData();
        assertThat(mode.mode()).isEqualTo("cognito");
        assertThat(mode.imageUploadMode()).isEqualTo("s3");
    }

    @Test
    void keepsLocalMultipartUploads() {
        var environment = new MockEnvironment();
        environment.setActiveProfiles("local");
        var mode = new AuthModeController(environment).config().getData();
        assertThat(mode.mode()).isEqualTo("local");
        assertThat(mode.imageUploadMode()).isEqualTo("multipart");
    }
}
