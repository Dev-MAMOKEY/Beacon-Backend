package com.mamoki.beacon.global.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
public class JwtUtil {

    private final SecretKey secretKey; // JWT secret key
    private final long accessTokenExpMin; // access token 만료 시간 = 1시간 설정
    private final long refreshTokenExpDay; // refresh token 만료 시간 = 30일 설정

    public JwtUtil(
            @Value("${custom.jwt.secret-key}") String secretKey,
            @Value("${custom.jwt.access-exp-min}") long accessTokenExpMin,
            @Value("${custom.jwt.refresh-exp-day}") long refreshTokenExpDay
    ) {
        this.secretKey = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        this.accessTokenExpMin = accessTokenExpMin * 60 * 1000L; // 분 단위를 밀리초로 변환
        this.refreshTokenExpDay = refreshTokenExpDay * 24 * 60 * 60 * 1000L; // 일 단위를 밀리초로 변환
    }

    // 토큰 공통 파싱 메서드
    public Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey) // secretkey값 객체
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public Long getMemberId(String token) { // 회원 id값 추출 메서드
        return Long.parseLong(parseClaims(token).getSubject());
    }

    // 토큰 종류("access" / "refresh") 추출 메서드
    // 발급 시 JwtProvider가 넣어준 type 클레임을 읽어, AT를 RT 자리에 잘못 보낸 요청을 걸러내는 데 사용
    public String getTokenType(Claims claims) {
        return claims.get("type", String.class);
    }

    public SecretKey getSecretKey() {
        return secretKey;
    }

    public long getAccessTokenExpMin() {
        return accessTokenExpMin;
    }

    public long getRefreshTokenExpDay() {
        return refreshTokenExpDay;
    }
}