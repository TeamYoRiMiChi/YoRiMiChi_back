package com.yorimichi.yorimichi.global.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("com.yorimichi.yorimichi.domain.**.repository")
public class MyBatisConfig {
}