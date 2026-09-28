package com.yorimichi.yorimichi.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.yorimichi.yorimichi.domain.user.dto.CognitoOnboardingRequestDto;
import com.yorimichi.yorimichi.domain.user.dto.UserResponseDto;
import com.yorimichi.yorimichi.domain.user.entity.User;
import com.yorimichi.yorimichi.domain.user.repository.UserMapper;
import com.yorimichi.yorimichi.global.error.CustomException;
import com.yorimichi.yorimichi.global.error.ErrorCode;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMapper userMapper;



    /**
 * Creates a member record for an authenticated Cognito user.
 *
 * cognitoSub and verifiedEmail must come from a verified Cognito identity,
 * not directly from request body values.
 */
@Transactional
public UserResponseDto onboardCognitoUser(
        String cognitoSub,
        String verifiedEmail,
        CognitoOnboardingRequestDto request
) {
    if (!StringUtils.hasText(cognitoSub)
            || !StringUtils.hasText(verifiedEmail)) {
        throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
    }

    var existingMember = userMapper.findByCognitoSub(cognitoSub);

    if (existingMember.isPresent()) {
        User user = existingMember.get();

        if (!user.isActive()) {
            throw new CustomException(
                    "INACTIVE".equals(user.getStatus())
                            ? ErrorCode.WITHDRAWN_MEMBER
                            : ErrorCode.SUSPENDED_MEMBER
            );
        }

        return new UserResponseDto(user);
    }

    /*
     * Do not automatically link an existing member by email.
     * Account linking must be handled through a separate verified flow.
     */
    if (userMapper.existsByEmail(verifiedEmail)) {
        throw new CustomException(ErrorCode.DUPLICATE_EMAIL);
    }

    String phone = StringUtils.hasText(request.getPhone())
            ? request.getPhone().trim()
            : null;

    User user = User.builder()
            .cognitoSub(cognitoSub)
            .email(verifiedEmail.trim())
            .name(request.getName().trim())
            .phone(phone)
            .role("USER")
            .status("ACTIVE")
            .build();

    userMapper.save(user);

    User saved = userMapper.findById(user.getMemberId())
            .orElseThrow(() ->
                    new CustomException(ErrorCode.USER_NOT_FOUND));

    return new UserResponseDto(saved);
}
    /**
     * Returns the member associated with the authenticated Cognito user.
     */
    @Transactional(readOnly = true)
    public UserResponseDto getMyInfo(String cognitoSub) {
        User user = userMapper.findByCognitoSub(cognitoSub)
                .orElseThrow(() ->
                        new CustomException(ErrorCode.USER_NOT_FOUND));

        if (!user.isActive()) {
            throw new CustomException(
                    "INACTIVE".equals(user.getStatus())
                            ? ErrorCode.WITHDRAWN_MEMBER
                            : ErrorCode.SUSPENDED_MEMBER
            );
        }

        return new UserResponseDto(user);
    }

}
