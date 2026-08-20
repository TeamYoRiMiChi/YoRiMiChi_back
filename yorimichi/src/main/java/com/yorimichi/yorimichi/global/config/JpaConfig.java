package com.yorimichi.yorimichi.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing   // @CreatedDate, @LastModifiedDate 자동 처리용
public class JpaConfig {
}