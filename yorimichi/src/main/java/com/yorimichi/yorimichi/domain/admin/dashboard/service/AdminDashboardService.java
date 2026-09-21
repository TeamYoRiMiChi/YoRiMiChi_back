package com.yorimichi.yorimichi.domain.admin.dashboard.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import com.yorimichi.yorimichi.domain.admin.dashboard.dto.RecentOrderResponseDto;
import com.yorimichi.yorimichi.domain.admin.dashboard.dto.DashboardOrderStatusResponseDto;
import com.yorimichi.yorimichi.domain.admin.dashboard.dto.OrderStatusCountResponseDto;
import com.yorimichi.yorimichi.domain.admin.dashboard.dto.DailySalesResponseDto;
import com.yorimichi.yorimichi.domain.admin.dashboard.dto.DashboardSalesTrendResponseDto;
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

    public DashboardSalesTrendResponseDto getSalesTrend(Long memberId) {
        validateAdmin(memberId);

        LocalDate today = LocalDate.now(KOREA_ZONE);
        LocalDate startDate = today.minusDays(6);
        LocalDate endDate = today.plusDays(1);

        LocalDateTime startUtc =
                toUtcDateTime(startDate);

        LocalDateTime endUtc =
                toUtcDateTime(endDate);

        List<DailySalesResponseDto> queryResults =
                adminDashboardMapper.findDailySales(
                        startUtc,
                        endUtc
                );

        Map<LocalDate, BigDecimal> revenueByDate =
                new HashMap<>();

        for (DailySalesResponseDto dailySales : queryResults) {
            revenueByDate.put(
                    dailySales.getSalesDate(),
                    dailySales.getRevenue()
            );
        }

        List<DailySalesResponseDto> dailySales =
                new ArrayList<>();

        BigDecimal totalRevenue = BigDecimal.ZERO;

        for (int i = 0; i < 7; i++) {
            LocalDate date = startDate.plusDays(i);

            BigDecimal revenue = revenueByDate.getOrDefault(
                    date,
                    BigDecimal.ZERO
            );

            dailySales.add(
                    new DailySalesResponseDto(date, revenue)
            );

            totalRevenue = totalRevenue.add(revenue);
        }

        return new DashboardSalesTrendResponseDto(
                totalRevenue,
                dailySales
        );
    }

    public DashboardOrderStatusResponseDto getOrderStatus(Long memberId) {
        validateAdmin(memberId);

        LocalDate today = LocalDate.now(KOREA_ZONE);
        LocalDateTime startUtc = toUtcDateTime(today);
        LocalDateTime endUtc = toUtcDateTime(today.plusDays(1));

        List<OrderStatusCountResponseDto> queryResults =
                adminDashboardMapper.findOrderStatusCounts(startUtc, endUtc);

        Map<String, Long> counts = new LinkedHashMap<>();
        counts.put("PAID", 0L);
        counts.put("PREPARING", 0L);
        counts.put("SHIPPING", 0L);
        counts.put("DELIVERED", 0L);
        counts.put("CANCELLED", 0L);
        counts.put("REFUNDED", 0L);

        for (OrderStatusCountResponseDto row : queryResults) {
            if (counts.containsKey(row.getStatus())) {
                counts.merge(row.getStatus(), row.getCount(), Long::sum);
            }
        }

        List<OrderStatusCountResponseDto> statuses = new ArrayList<>();
        long totalCount = 0;

        for (Map.Entry<String, Long> entry : counts.entrySet()) {
            statuses.add(new OrderStatusCountResponseDto(
                    entry.getKey(),
                    entry.getValue()
            ));
            totalCount += entry.getValue();
        }

        return new DashboardOrderStatusResponseDto(totalCount, statuses);
    }

    public List<RecentOrderResponseDto> getRecentOrders(Long memberId) {
        validateAdmin(memberId);
        return adminDashboardMapper.findRecentOrders();
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
