package com.mamoki.beacon.global.security;

import org.springframework.context.annotation.Configuration;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf().disable() // CSRF 보호 비활성화 (API 서버에서는 일반적으로 필요 없음)
            .authorizeHttpRequests()
                .requestMatchers("/api/**").authenticated() // API 경로는 인증 필요
                .anyRequest().permitAll() // 그 외의 경로는 모두 허용
            .and()
            .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS); // 세션 사용 안 함 (JWT 사용 시)

        return http.build();
    }
}
