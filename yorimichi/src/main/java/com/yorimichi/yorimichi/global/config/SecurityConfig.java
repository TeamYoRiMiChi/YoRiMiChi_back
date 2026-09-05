package com.yorimichi.yorimichi.global.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.yorimichi.yorimichi.global.error.ErrorCode;
import com.yorimichi.yorimichi.global.jwt.JwtAuthenticationFilter;

import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * 허용할 프론트 주소 패턴 (application.yml의 cors.allowed-origins)
     *
     * 설정이 없어도 서버가 뜼도록 기본값을 두었습니다.
     */
    @Value("${cors.allowed-origins:http://localhost:*,http://127.0.0.1:*,http://192.168.*.*:*,http://10.*.*.*:*}")
    private List<String> allowedOrigins;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            .authorizeHttpRequests(auth -> auth
                // CORS 사전 요청(preflight)은 항상 허용
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                // 인증 없이 접근 가능
                .requestMatchers(HttpMethod.POST, "/api/users").permitAll()          // 회원가입
                .requestMatchers("/api/users/login").permitAll()                     // 로그인
                .requestMatchers("/api/users/check-email").permitAll()               // 이메일 중복 확인
                .requestMatchers("/api/auth/**", "/api/oauth/**").permitAll()        // 소셜 로그인

                // 상품·카테고리 조회는 비로그인도 가능
                .requestMatchers(HttpMethod.GET, "/api/products/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/categories/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/gb-categories/**").permitAll()
                // 나머지는 로그인 필요
                .anyRequest().authenticated()
            )

            // 인증·인가 실패 시 로그인 페이지 대신 JSON을 돌려줍니다.
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint((req, res, e) ->
                        writeError(res, ErrorCode.UNAUTHORIZED))
                .accessDeniedHandler((req, res, e) ->
                        writeError(res, ErrorCode.UNAUTHORIZED))
            )

            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * 에러를 ErrorResponse와 같은 형태의 JSON으로 응답합니다.
     *
     * 필터 단계는 컨트롤러 밖이라 @RestControllerAdvice가 잡지 못하므로
     * 여기서 직접 JSON을 씁니다.
     */
    private void writeError(HttpServletResponse response, ErrorCode code) throws IOException {
        response.setStatus(code.getStatus().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        String body = String.format(
                "{\"success\":false,\"code\":\"%s\",\"message\":\"%s\"}",
                code.getCode(),
                code.getMessage()
        );

        response.getWriter().write(body);
    }

    /**
     * CORS 설정
     *
     * allowCredentials(true)를 쓰면 setAllowedOrigins에 "*"를 넣을 수 없습니다.
     * 대신 setAllowedOriginPatterns를 쓰면 와일드카드를 쓸 수 있어서,
     * 사설 IP(192.168.x.x 등)로 접속해도 허용됩니다.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOriginPatterns(allowedOrigins);
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setExposedHeaders(List.of("Authorization"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
