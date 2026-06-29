package com.mamoki.beacon.global.security.jwt;

import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.Date;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JwtProvider {

    private final JwtUtil jwtUtil;

    public String createAccessToken(Long memberId) {
        return Jwts.builder()
                .subject(String.valueOf(memberId)) // 회원 ID를 문자열로 설정
                .claim("type", "access") // 토큰 유형을 클레임에 추가
                .id(UUID.randomUUID().toString()) // 고유한 JWT ID 설정
                .issuedAt(new Date()) // 발급 시간 설정
                .expiration(new Date(System.currentTimeMillis() + jwtUtil.getAccessTokenExpMin())) // 만료 시간 설정
                .signWith(jwtUtil.getSecretKey()) // 서명에 사용할 비밀 키 설정
                .compact();
    }

    public String createRefreshToken(Long memberId) {
        return Jwts.builder()
                .subject(String.valueOf(memberId)) // 회원 ID를 문자열로 설정
                .claim("type", "refresh")
                .id(UUID.randomUUID().toString()) // 고유한 JWT ID 설정
                .issuedAt(new Date()) // 발급 시간 설정
                .expiration(new Date(System.currentTimeMillis() + jwtUtil.getRefreshTokenExpDay())) // 만료 시간 설정
                .signWith(jwtUtil.getSecretKey()) // 서명에 사용할 비밀 키 설정
                .compact();
    }
}
