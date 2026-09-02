package com.yorimichi.yorimichi.global.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
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

    private static final String CLAIM_EMAIL = "email";
    private static final String CLAIM_TYPE = "type";

    private final JwtProperties jwtProperties;
    private Algorithm algorithm;

    @PostConstruct
    protected void init() {
        this.algorithm = Algorithm.HMAC256(jwtProperties.getSecret());
    }

    public String createAccessToken(Long memberId, String email) {
        return createToken(memberId, email, "access", jwtProperties.getAccessTokenValidity());
    }

    public String createRefreshToken(Long memberId, String email) {
        return createToken(memberId, email, "refresh", jwtProperties.getRefreshTokenValidity());
    }

    private String createToken(Long memberId, String email, String type, long validityMs) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + validityMs);

        return JWT.create()
                .withSubject(String.valueOf(memberId))
                .withClaim(CLAIM_EMAIL, email)
                .withClaim(CLAIM_TYPE, type)
                .withIssuedAt(now)
                .withExpiresAt(expiry)
                .sign(algorithm);
    }

    public Long getUserId(String token) {
        return Long.parseLong(verify(token).getSubject());
    }

    public String getEmail(String token) {
        return verify(token).getClaim(CLAIM_EMAIL).asString();
    }

    /** 예외를 던지지 않고 유효 여부만 알려줍니다 */
    public boolean validateToken(String token) {
        try {
            JWT.require(algorithm).build().verify(token);
            return true;
        } catch (JWTVerificationException e) {
            return false;
        }
    }

    private DecodedJWT verify(String token) {
        try {
            return JWT.require(algorithm).build().verify(token);
        } catch (TokenExpiredException e) {
            throw new CustomException(ErrorCode.EXPIRED_TOKEN);
        } catch (JWTVerificationException e) {
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }
    }
}
