package com.yorimichi.yorimichi.global.config;

import java.security.SecureRandom;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import com.nimbusds.jose.jwk.source.ImmutableSecret;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;

/** Local-only signing key: a server restart requires signing in again. */
@Configuration
@Profile("local")
public class LocalAuthenticationConfig {
    public static final String ISSUER = "http://yorimichi.local";

    @Bean
    public SecretKey localSigningKey() {
        byte[] key = new byte[32];
        new SecureRandom().nextBytes(key);
        return new SecretKeySpec(key, "HmacSHA256");
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JwtEncoder localJwtEncoder(SecretKey localSigningKey) {
        return new NimbusJwtEncoder(new ImmutableSecret<>(localSigningKey));
    }

    @Bean
    public JwtDecoder jwtDecoder(SecretKey localSigningKey) {
        NimbusJwtDecoder decoder = NimbusJwtDecoder.withSecretKey(localSigningKey)
                .macAlgorithm(MacAlgorithm.HS256).build();
        decoder.setJwtValidator(JwtValidators.createDefaultWithIssuer(ISSUER));
        return decoder;
    }
}
