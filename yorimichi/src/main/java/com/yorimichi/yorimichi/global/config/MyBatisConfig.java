package com.yorimichi.yorimichi.global.config;

import org.apache.ibatis.annotations.Mapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan(
        basePackages = "com.yorimichi.yorimichi.domain",
        annotationClass = Mapper.class
)
public class MyBatisConfig {
}