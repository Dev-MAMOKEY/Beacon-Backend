package com.mamoki.beacon.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class CorsConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        // 허용할 프론트 출처 (개발 단계에서는 전체 허용)
        // 운영 시 실제 프론트 도메인으로 좁히는 것을 권장: 예) "https://app.example.com"
        config.setAllowedOriginPatterns(List.of("*"));

        // 허용 HTTP 메서드
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));

        // 허용 요청 헤더 (Authorization 포함 전체 허용)
        config.setAllowedHeaders(List.of("*"));

        // 응답에서 프론트가 읽을 수 있게 노출할 헤더
        config.setExposedHeaders(List.of("Authorization"));

        // 쿠키/인증정보 포함 요청 허용
        config.setAllowCredentials(true);

        // 프리플라이트(OPTIONS) 캐시 시간(초)
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
