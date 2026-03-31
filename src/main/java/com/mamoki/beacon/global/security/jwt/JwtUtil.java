package com.mamoki.beacon.global.security.jwt;

import com.mamoki.beacon.domain.club_member.entity.Role;
import com.mamoki.beacon.global.rsdata.RsData;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.LocalDateTime;

@Slf4j
@Component
public class JwtUtil {

    private final Key key; // JWT secret key
    private final long accessExpMin; // Access Token 만료 시간 (1시간)
    private final long refreshExpDay; // Refresh Token 만료 시간 (30일)

    public JwtUtil(
            @Value("${custom.jwt.secret.key}") String secretKey,
            @Value("${custom.jwt.access.exp.min}") Long accessExpMin,
            @Value("${custom.jwt.refresh.exp.day}") Long refreshExpDay
    ) {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        this.accessExpMin = accessExpMin * 60 * 1000; // 분 단위를 밀리초로 변환
        this.refreshExpDay = refreshExpDay * 24 * 60 * 60 * 1000; // 일 단위를 밀리초로 변환
    }

    // 토큰 공통 파싱 메서드
    public Claims parseClaims(String token) {
        return Jwts.parser()
                .setSigningKey(key) // secretkey값 객체
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public Long getMemberId(String token) { // 회원 id값 추출 메서드
        return Long.parseLong(parseClaims(token).getSubject());
    }

    public Role getRole(String token) { // ENUM 타입 Role 값 추출 메서드
        return Role.valueOf(parseClaims(token).get("role", String.class));
    }
}