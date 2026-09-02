package com.yorimichi.yorimichi.domain.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yorimichi.yorimichi.domain.user.dto.LoginRequestDto;
import com.yorimichi.yorimichi.domain.user.dto.LoginResponseDto;
import com.yorimichi.yorimichi.domain.user.dto.UserResponseDto;
import com.yorimichi.yorimichi.domain.user.dto.UserSignUpRequestDto;
import com.yorimichi.yorimichi.domain.user.entity.User;
import com.yorimichi.yorimichi.domain.user.repository.UserMapper;
import com.yorimichi.yorimichi.global.error.CustomException;
import com.yorimichi.yorimichi.global.error.ErrorCode;
import com.yorimichi.yorimichi.global.jwt.JwtProvider;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    /**
     * 회원가입
     *
     * 비밀번호는 BCrypt로 해시해서 저장합니다.
     * 평문은 DB에 절대 남기지 않습니다.
     */
    @Transactional
    public UserResponseDto signup(UserSignUpRequestDto request) {

        if (userMapper.existsByEmail(request.getEmail())) {
            throw new CustomException(ErrorCode.DUPLICATE_EMAIL);
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .name(request.getName())
                .phone(request.getPhone())
                .role("USER")
                .status("ACTIVE")
                .build();

        userMapper.save(user);

        // save 후 DB가 채운 기본값(createdAt 등)까지 담아서 응답
        User saved = userMapper.findById(user.getMemberId())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        return new UserResponseDto(saved);
    }

    /**
     * 로그인
     *
     * 이메일이 없든 비밀번호가 틀리든 같은 예외를 던집니다.
     * 서로 다른 메시지를 주면 "이 이메일은 가입되어 있다"는 정보가
     * 공격자에게 노출되기 때문입니다.
     */
    @Transactional(readOnly = true)
    public LoginResponseDto login(LoginRequestDto request) {

        User user = userMapper.findByEmail(request.getEmail())
                .orElseThrow(() -> new CustomException(ErrorCode.LOGIN_FAILED));

        // 소셜 로그인 전용 계정은 비밀번호가 없음
        if (user.isSocialOnly()) {
            throw new CustomException(ErrorCode.SOCIAL_ACCOUNT_ONLY);
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new CustomException(ErrorCode.LOGIN_FAILED);
        }

        // 탈퇴·정지 회원 차단
        if (!user.isActive()) {
            throw new CustomException(
                    "WITHDRAWN".equals(user.getStatus())
                            ? ErrorCode.WITHDRAWN_MEMBER
                            : ErrorCode.SUSPENDED_MEMBER
            );
        }

        String accessToken = jwtProvider.createAccessToken(user.getMemberId(), user.getEmail());
        String refreshToken = jwtProvider.createRefreshToken(user.getMemberId(), user.getEmail());

        log.info("login success - memberId={}", user.getMemberId());

        return new LoginResponseDto(accessToken, refreshToken, new UserResponseDto(user));
    }

    /**
     * 회원 단건 조회
     */
    @Transactional(readOnly = true)
    public UserResponseDto getUser(Long memberId) {
        User user = userMapper.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        return new UserResponseDto(user);
    }

    /**
     * 내 정보 조회 (토큰의 memberId 사용)
     */
    @Transactional(readOnly = true)
    public UserResponseDto getMyInfo(Long memberId) {
        return getUser(memberId);
    }

    /**
     * 이메일 중복 확인
     */
    @Transactional(readOnly = true)
    public boolean isEmailDuplicated(String email) {
        return userMapper.existsByEmail(email);
    }
}
