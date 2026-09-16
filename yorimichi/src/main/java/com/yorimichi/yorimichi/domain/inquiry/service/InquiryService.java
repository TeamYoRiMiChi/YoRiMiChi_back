package com.yorimichi.yorimichi.domain.inquiry.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yorimichi.yorimichi.domain.inquiry.dto.InquiryCreateRequestDto;
import com.yorimichi.yorimichi.domain.inquiry.dto.InquiryResponseDto;
import com.yorimichi.yorimichi.domain.inquiry.entity.Inquiry;
import com.yorimichi.yorimichi.domain.inquiry.repository.InquiryMapper;
import com.yorimichi.yorimichi.domain.user.repository.UserMapper;
import com.yorimichi.yorimichi.global.error.CustomException;
import com.yorimichi.yorimichi.global.error.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InquiryService {

    private final InquiryMapper inquiryMapper;
    private final UserMapper userMapper;

    @Transactional
    public Long create(Long memberId, InquiryCreateRequestDto request) {
        Inquiry inquiry = Inquiry.builder()
                .memberId(memberId)
                .category(request.getCategory())
                .title(request.getTitle().trim())
                .content(request.getContent().trim())
                .status("WAITING")
                .build();

        inquiryMapper.insert(inquiry);
        return inquiry.getInquiryId();
    }

    @Transactional(readOnly = true)
    public List<InquiryResponseDto> getMyInquiries(Long memberId) {
        return inquiryMapper.findAllByMemberId(memberId).stream()
                .map(InquiryResponseDto::from)
                .toList();
    }

    @Transactional
    public void updatePending(Long memberId, Long inquiryId, InquiryCreateRequestDto request) {
        if (memberId == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }
        int updated = inquiryMapper.updatePending(
                inquiryId, memberId, request.getCategory(),
                request.getTitle().trim(), request.getContent().trim());
        if (updated > 0) {
            return;
        }

        Inquiry inquiry = inquiryMapper.findById(inquiryId);
        if (inquiry == null) {
            throw new CustomException(ErrorCode.INQUIRY_NOT_FOUND);
        }
        if (!memberId.equals(inquiry.getMemberId())) {
            throw new CustomException(ErrorCode.INQUIRY_NOT_OWNER);
        }
        throw new CustomException(ErrorCode.INQUIRY_ALREADY_ANSWERED);
    }

    @Transactional(readOnly = true)
    public List<InquiryResponseDto> getAll(Long memberId) {
        validateAdmin(memberId);
        return inquiryMapper.findAll().stream().map(InquiryResponseDto::from).toList();
    }

    @Transactional
    public void answer(Long memberId, Long inquiryId, String answer) {
        validateAdmin(memberId);
        if (inquiryMapper.updateAnswer(inquiryId, answer.trim()) == 0) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
        }
    }

    private void validateAdmin(Long memberId) {
        if (memberId == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }
        boolean admin = userMapper.findById(memberId)
                .map(user -> "ADMIN".equals(user.getRole()))
                .orElse(false);
        if (!admin) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }
    }
}
