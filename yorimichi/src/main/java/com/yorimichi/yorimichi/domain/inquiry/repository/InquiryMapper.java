package com.yorimichi.yorimichi.domain.inquiry.repository;

import org.apache.ibatis.annotations.Mapper;

import com.yorimichi.yorimichi.domain.inquiry.entity.Inquiry;

@Mapper
public interface InquiryMapper {

    void insert(Inquiry inquiry);
}
