package com.yorimichi.yorimichi.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * MVC 관련 공통 설정
 *
 * CORS는 SecurityConfig에서 처리합니다.
 * Spring Security를 쓰면 Security 필터 체인이 먼저 요청을 가로채므로
 * 여기(WebConfig)에 CORS를 또 설정하면 두 군데를 관리해야 하고,
 * 값이 어긋났을 때 원인을 찾기 어려워집니다.
 *
 * 인터셉터, 정적 리소스 경로 등이 필요해지면 여기에 추가하세요.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {
}
