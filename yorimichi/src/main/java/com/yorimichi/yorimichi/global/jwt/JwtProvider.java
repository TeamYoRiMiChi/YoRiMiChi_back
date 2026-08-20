package com.yorimichi.yorimichi.global.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.yorimichi.yorimichi.global.error.CustomException;
import com.yorimichi.yorimichi.global.error.ErrorCode;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtProvider {

    private final JwtProperties jwtProperties;
    private Algorithm algorithm;

    @PostConstruct
    protected void init() {
        this.algorithm = Algorithm.HMAC256(jwtProperties.getSecret());
    }

    public String createAccessToken(Long userId, String email) {
        return createToken(userId, email, jwtProperties.getAccessTokenValidity());
    }

    public String createRefreshToken(Long userId, String email) {
        return createToken(userId, email, jwtProperties.getRefreshTokenValidity());
    }

    private String createToken(Long userId, String email, long validityMs) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + validityMs);

        return JWT.create()
                .withSubject(String.valueOf(userId))
                .withClaim("email", email)
                .withIssuedAt(now)
                .withExpiresAt(expiry)
                .sign(algorithm);
    }

    public Long getUserId(String token) {
        return Long.parseLong(verify(token).getSubject());
    }

    public String getEmail(String token) {
        return verify(token).getClaim("email").asString();
    }

    public boolean validateToken(String token) {
        try {
            verify(token);
            return true;
        } catch (JWTVerificationException e) {
            return false;
        }
    }

    private DecodedJWT verify(String token) {
        try {
            return JWT.require(algorithm).build().verify(token);
        } catch (JWTVerificationException e) {
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }
    }
}