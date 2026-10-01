package com.yorimichi.yorimichi.domain.user.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Locale;
import com.yorimichi.yorimichi.domain.user.dto.*;
import com.yorimichi.yorimichi.domain.user.entity.User;
import com.yorimichi.yorimichi.domain.user.repository.UserMapper;
import com.yorimichi.yorimichi.domain.mypage.dto.AddressRequestDto;
import com.yorimichi.yorimichi.domain.mypage.service.AddressService;
import com.yorimichi.yorimichi.global.config.LocalAuthenticationConfig;
import com.yorimichi.yorimichi.global.error.*;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@Profile("local")
@RequiredArgsConstructor
public class LocalAuthenticationService {
    private final UserMapper users;
    private final PasswordEncoder passwords;
    private final JwtEncoder encoder;
    private final AddressService addresses;
    private final LocalAdminPolicy adminPolicy;

    @Transactional
    public UserResponseDto signup(LocalSignupRequest request) {
        String email = normalizeEmail(request.email());
        if (users.existsByEmail(email)) throw new CustomException(ErrorCode.DUPLICATE_EMAIL);
        // BCrypt accepts at most 72 bytes, not 72 Unicode characters.
        if (request.password().getBytes(java.nio.charset.StandardCharsets.UTF_8).length > 72)
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
        User user = User.builder().email(email).password(passwords.encode(request.password()))
                .name(request.name().trim()).phone(request.phone())
                .role(adminPolicy.isAdmin(email) ? "ADMIN" : "USER").status("ACTIVE").build();
        try {
            users.save(user);
        } catch (DuplicateKeyException exception) {
            throw new CustomException(ErrorCode.DUPLICATE_EMAIL);
        }
        if (StringUtils.hasText(request.postalCode()) && StringUtils.hasText(request.address())) {
            AddressRequestDto address = new AddressRequestDto();
            address.setAddressName("自宅");
            address.setReceiverName(user.getName());
            address.setReceiverPhone(user.getPhone());
            address.setPostalCode(request.postalCode().trim());
            address.setAddress(request.address().trim());
            address.setAddressDetail(request.addressDetail());
            addresses.createAddress(user.getMemberId(), address);
        }
        return new UserResponseDto(users.findById(user.getMemberId()).orElseThrow());
    }

    @Transactional(readOnly = true)
    public LoginResult login(LocalLoginRequest request) {
        User user = users.findByEmail(normalizeEmail(request.email()))
                .orElseThrow(() -> new CustomException(ErrorCode.LOGIN_FAILED));
        if (!StringUtils.hasText(user.getPassword()) || !passwords.matches(request.password(), user.getPassword()))
            throw new CustomException(ErrorCode.LOGIN_FAILED);
        requireActive(user);
        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder().issuer(LocalAuthenticationConfig.ISSUER)
                .subject(user.getMemberId().toString()).issuedAt(now).expiresAt(now.plus(8, ChronoUnit.HOURS))
                .claim("roles", List.of(user.getRole())).build();
        String token = encoder.encode(JwtEncoderParameters.from(
                JwsHeader.with(MacAlgorithm.HS256).build(), claims)).getTokenValue();
        return new LoginResult(token, new UserResponseDto(user));
    }

    @Transactional(readOnly = true)
    public UserResponseDto me(long id) {
        User user = users.findById(id).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        requireActive(user);
        return new UserResponseDto(user);
    }

    private void requireActive(User user) {
        if (!user.isActive()) throw new CustomException(
                "INACTIVE".equals(user.getStatus()) || "WITHDRAWN".equals(user.getStatus())
                        ? ErrorCode.WITHDRAWN_MEMBER : ErrorCode.SUSPENDED_MEMBER);
    }

    private String normalizeEmail(String email) { return email.trim().toLowerCase(Locale.ROOT); }

    public record LoginResult(String accessToken, UserResponseDto user) {}
}
