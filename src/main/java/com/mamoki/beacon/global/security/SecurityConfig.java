package com.mamoki.beacon.global.security;

import com.mamoki.beacon.global.security.jwt.JwtAuthenticationFilter;
import com.mamoki.beacon.global.security.jwt.JwtUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import tools.jackson.databind.ObjectMapper;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // JwtAuthenticationFilter는 @Component가 없으므로 여기서 직접 Bean 등록
    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(JwtUtil jwtUtil, ObjectMapper objectMapper) {
        return new JwtAuthenticationFilter(jwtUtil, objectMapper);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
                                           JwtAuthenticationFilter jwtAuthenticationFilter,
                                           JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint,
                                           JwtAccessDeniedHandler jwtAccessDeniedHandler) throws Exception {
        http
            // Spring Security 7 (Boot 4.x) 람다 DSL 필수 — 기존 메서드 체이닝 방식 제거됨
            // CorsConfig의 CorsConfigurationSource Bean을 자동으로 사용
            .cors(cors -> {})
            .csrf(csrf -> csrf.disable())
            .formLogin(form -> form.disable())
            .httpBasic(basic -> basic.disable())
            // JWT 기반 인증은 서버 세션을 사용하지 않으므로 STATELESS 설정
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authorizeHttpRequests(auth -> auth
                // 로그인/회원가입, 그리고 토큰 재발급은 인증 불필요
                // refresh는 "AT가 만료된 상태"에서 호출되는 API이므로 인증을 요구하면 안 된다.
                // (요구하면 AT 만료 → 재발급 불가 → 무조건 재로그인이 되어버림)
                .requestMatchers("/api/v1/auth/signup", "/api/v1/auth/login", "/api/v1/auth/refresh").permitAll()
                .requestMatchers("/fcm-test.html", "/firebase-messaging-sw.js", "/favicon.ico").permitAll() // FCM 테스트 페이지
                .requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**").permitAll() // Swagger UI 및 API 문서 접근 허용
                .anyRequest().authenticated()
            )
            // JWT 필터를 Spring Security 기본 인증 필터 앞에 배치
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            // 인증(401) / 인가(403) 실패도 RsData 포맷으로 응답 (기본값은 빈 body라 프론트가 code를 못 읽음)
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                .accessDeniedHandler(jwtAccessDeniedHandler)
            );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}