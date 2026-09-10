package com.yorimichi.yorimichi.domain.GroupBuy.service;

import java.util.Optional;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yorimichi.yorimichi.domain.GroupBuy.dto.GroupBuyResponseDto;
import com.yorimichi.yorimichi.domain.GroupBuy.dto.GroupBuyParticipationResponseDto;
import com.yorimichi.yorimichi.domain.GroupBuy.entity.GroupBuyParticipant;
import com.yorimichi.yorimichi.domain.GroupBuy.repository.GroupBuyMapper;
import com.yorimichi.yorimichi.global.error.CustomException;
import com.yorimichi.yorimichi.global.error.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GroupBuyService {

    private final GroupBuyMapper groupBuyMapper;

    // 조회만 Controller에서 404로 응답
    @Transactional(readOnly = true)
    public Optional<GroupBuyResponseDto> getGroupBuyByProductId(Long productId) {
        // 상품 번호는 1 이상의 값으로
        if (productId == null || productId <= 0) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
        }

        return groupBuyMapper.findByProductId(productId);
    }

    @Transactional(readOnly = true)
    public GroupBuyParticipationResponseDto getMyParticipation(Long memberId, Long productId) {
        if (productId == null || productId <= 0) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
        }

        GroupBuyResponseDto groupBuy = groupBuyMapper.findByProductId(productId)
                .orElseThrow(() -> new CustomException(ErrorCode.GROUP_BUY_NOT_FOUND));
        GroupBuyParticipant participant = groupBuyMapper
                .findParticipation(groupBuy.getGroupBuyId(), memberId)
                .orElse(null);

        boolean joined = participant != null
                && !"CANCELLED".equals(participant.getParticipationStatus());
        int memberQuantity = joined && participant.getQuantity() != null ? participant.getQuantity() : 0;

        return new GroupBuyParticipationResponseDto(
                joined ? participant.getParticipantId() : null,
                groupBuy.getGroupBuyId(),
                productId,
                memberQuantity,
                groupBuy.getCurrentQuantity() == null ? 0 : groupBuy.getCurrentQuantity(),
                groupBuy.getTargetQuantity() == null ? 0 : groupBuy.getTargetQuantity(),
                joined ? participant.getParticipationStatus() : "NOT_JOINED"
        );
    }

    @Transactional
    public GroupBuyParticipationResponseDto participate(Long memberId, Long productId, Integer quantity) {
        if (productId == null || productId <= 0 || quantity == null || quantity <= 0) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
        }

        GroupBuyResponseDto groupBuy = groupBuyMapper.findByProductIdForUpdate(productId)
                .orElseThrow(() -> new CustomException(ErrorCode.GROUP_BUY_NOT_FOUND));

        LocalDateTime now = LocalDateTime.now();
        if (!"RECRUITING".equals(groupBuy.getStatus())
                || (groupBuy.getStartDate() != null && now.isBefore(groupBuy.getStartDate()))
                || (groupBuy.getEndDate() != null && !now.isBefore(groupBuy.getEndDate()))) {
            throw new CustomException(ErrorCode.GROUP_BUY_CLOSED);
        }

        int currentQuantity = groupBuy.getCurrentQuantity() == null ? 0 : groupBuy.getCurrentQuantity();
        int targetQuantity = groupBuy.getTargetQuantity() == null ? 0 : groupBuy.getTargetQuantity();
        if (currentQuantity + quantity > targetQuantity) {
            throw new CustomException(ErrorCode.GROUP_BUY_CAPACITY_EXCEEDED);
        }

        GroupBuyParticipant participant = groupBuyMapper
                .findParticipation(groupBuy.getGroupBuyId(), memberId)
                .orElse(null);

        int memberQuantity;
        if (participant == null) {
            participant = GroupBuyParticipant.builder()
                    .groupBuyId(groupBuy.getGroupBuyId())
                    .memberId(memberId)
                    .quantity(quantity)
                    .participationStatus("JOINED")
                    .build();
            groupBuyMapper.insertParticipation(participant);
            memberQuantity = quantity;
        } else if ("CANCELLED".equals(participant.getParticipationStatus())) {
            groupBuyMapper.rejoinParticipation(participant.getParticipantId(), quantity);
            memberQuantity = quantity;
        } else {
            groupBuyMapper.increaseParticipationQuantity(participant.getParticipantId(), quantity);
            int previousMemberQuantity = participant.getQuantity() == null ? 0 : participant.getQuantity();
            memberQuantity = previousMemberQuantity + quantity;
        }

        groupBuyMapper.increaseCurrentQuantity(groupBuy.getGroupBuyId(), quantity);
        int updatedQuantity = currentQuantity + quantity;
        if (updatedQuantity >= targetQuantity) {
            groupBuyMapper.completeGroupBuy(groupBuy.getGroupBuyId());
        }

        return new GroupBuyParticipationResponseDto(
                participant.getParticipantId(),
                groupBuy.getGroupBuyId(),
                productId,
                memberQuantity,
                updatedQuantity,
                targetQuantity,
                "JOINED"
        );
    }
}
