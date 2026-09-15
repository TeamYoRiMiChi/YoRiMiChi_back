package com.yorimichi.yorimichi.domain.inquiry.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yorimichi.yorimichi.domain.inquiry.dto.InquiryCreateRequestDto;
import com.yorimichi.yorimichi.domain.inquiry.entity.Inquiry;
import com.yorimichi.yorimichi.domain.inquiry.repository.InquiryMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InquiryService {

    private final InquiryMapper inquiryMapper;

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
}
