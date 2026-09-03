package com.yorimichi.yorimichi.domain.overseas.repository;

import org.apache.ibatis.annotations.Mapper;

import com.yorimichi.yorimichi.domain.overseas.entity.Category;

import java.util.List;

/**
 * 카테고리 DB 접근
 *
 * 여기에는 메서드 이름만 있고 실제 SQL은
 * resources/mapper/category/CategoryMapper.xml 에 있습니다.
 * namespace가 이 인터페이스의 전체 경로와 일치해야 연결됩니다.
 */
@Mapper
public interface CategoryMapper {

    /** 전체 카테고리 조회 */
    List<Category> findAll();
}
