package com.yorimichi.yorimichi.domain.inquiry.repository;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.yorimichi.yorimichi.domain.inquiry.entity.Inquiry;

@Mapper
public interface InquiryMapper {

    void insert(Inquiry inquiry);

    List<Inquiry> findAllByMemberId(@Param("memberId") Long memberId);

    List<Inquiry> findAll();

    int updateAnswer(@Param("inquiryId") Long inquiryId, @Param("answer") String answer);
}
