package com.yorimichi.yorimichi.domain.user.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.mock.env.MockEnvironment;

class LocalAdminPolicyTest {

    @Test
    void grantsConfiguredEmailOnlyInLocalProfile() {
        MockEnvironment environment = new MockEnvironment();
        environment.setActiveProfiles("local");

        LocalAdminPolicy policy = new LocalAdminPolicy(
                environment,
                "gusals0908@gmail.com, another@example.com"
        );

        assertThat(policy.isAdmin("GUSALS0908@gmail.com")).isTrue();
        assertThat(policy.isAdmin("user@example.com")).isFalse();
    }

    @Test
    void neverGrantsLocalAdminOutsideLocalProfile() {
        MockEnvironment environment = new MockEnvironment();
        environment.setActiveProfiles("rds");

        LocalAdminPolicy policy = new LocalAdminPolicy(
                environment,
                "gusals0908@gmail.com"
        );

        assertThat(policy.isAdmin("gusals0908@gmail.com")).isFalse();
    }
}
