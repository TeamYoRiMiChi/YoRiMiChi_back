package com.yorimichi.yorimichi.domain.admin.dashboard.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yorimichi.yorimichi.domain.admin.dashboard.dto.DashboardSummaryResponseDto;
import com.yorimichi.yorimichi.domain.admin.dashboard.repository.AdminDashboardMapper;
import com.yorimichi.yorimichi.domain.user.entity.User;
import com.yorimichi.yorimichi.domain.user.repository.UserMapper;
import com.yorimichi.yorimichi.global.error.CustomException;
import com.yorimichi.yorimichi.global.error.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminDashboardService {

    private static final ZoneId KOREA_ZONE =
            ZoneId.of("Asia/Seoul");

    private final AdminDashboardMapper adminDashboardMapper;
    private final UserMapper userMapper;

    public DashboardSummaryResponseDto getSummary(Long memberId) {
        validateAdmin(memberId);
        LocalDate today = LocalDate.now(KOREA_ZONE);

        LocalDateTime todayStartUtc =
                toUtcDateTime(today);

        LocalDateTime tomorrowStartUtc =
                toUtcDateTime(today.plusDays(1));

        return adminDashboardMapper.findSummary(
                todayStartUtc,
                tomorrowStartUtc
        );
    }

     private void validateAdmin(Long memberId) {
        if (memberId == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        User user = userMapper.findById(memberId)
                .orElseThrow(() ->
                        new CustomException(ErrorCode.USER_NOT_FOUND)
                );

        if (!"ADMIN".equals(user.getRole())) {
            throw new CustomException(
                    ErrorCode.ADMIN_ACCESS_DENIED
            );
        }
    }

    private LocalDateTime toUtcDateTime(LocalDate date) {
        return LocalDateTime.ofInstant(
                date.atStartOfDay(KOREA_ZONE).toInstant(),
                ZoneOffset.UTC
        );
    }
}