package com.yorimichi.yorimichi.domain.user.service;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.stereotype.Component;

/**
 * Grants a database admin role only while the local Spring profile is active.
 * Cognito group membership is still required by SecurityConfig for admin APIs.
 */
@Component
public class LocalAdminPolicy {

    private final boolean enabled;
    private final Set<String> adminEmails;

    public LocalAdminPolicy(
            Environment environment,
            @Value("${app.local-admin-emails:}") String configuredEmails
    ) {
        this.enabled = environment.acceptsProfiles(Profiles.of("local"));
        this.adminEmails = Arrays.stream(configuredEmails.split(","))
                .map(String::trim)
                .filter(email -> !email.isEmpty())
                .map(String::toLowerCase)
                .collect(Collectors.toUnmodifiableSet());
    }

    public boolean isAdmin(String email) {
        return enabled
                && email != null
                && adminEmails.contains(email.trim().toLowerCase());
    }
}
