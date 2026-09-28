package com.yorimichi.yorimichi.global.auth;

import com.yorimichi.yorimichi.domain.user.entity.User;
import com.yorimichi.yorimichi.domain.user.repository.UserMapper;
import com.yorimichi.yorimichi.global.error.CustomException;
import com.yorimichi.yorimichi.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
@RequiredArgsConstructor
public class CurrentMemberIdArgumentResolver
        implements HandlerMethodArgumentResolver {

    private final UserMapper userMapper;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        boolean hasAnnotation =
                parameter.hasParameterAnnotation(CurrentMemberId.class);

        boolean isLongType =
                parameter.getParameterType().equals(Long.class)
                        || parameter.getParameterType().equals(long.class);

        return hasAnnotation && isLongType;
    }

    @Override
    public Object resolveArgument(
            MethodParameter parameter,
            ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory
    ) {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null
                || !(authentication.getPrincipal() instanceof Jwt jwt)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        User user = userMapper.findByCognitoSub(jwt.getSubject())
                .orElseThrow(() ->
                        new CustomException(ErrorCode.USER_NOT_FOUND));

        if (!user.isActive()) {
            throw new CustomException(
                    "INACTIVE".equals(user.getStatus())
                            ? ErrorCode.WITHDRAWN_MEMBER
                            : ErrorCode.SUSPENDED_MEMBER
            );
        }

        return user.getMemberId();
    }
}
