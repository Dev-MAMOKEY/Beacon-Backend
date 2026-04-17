package com.mamoki.beacon.global.security.jwt;

import com.mamoki.beacon.global.exception.ErrorCode;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.mamoki.beacon.global.exception.ErrorCode.TOKEN_EXPIRED;
import static com.mamoki.beacon.global.exception.ErrorCode.TOKEN_INVALID;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter { // 중복 검증 방지 및 요청당 한 번만 실행하기 위해 상속

    private static final String BEARER_PREFIX = "Bearer "; // Authorization 헤더에서 토큰을 추출할 때 사용되는 접두사 선언

    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper; // Java 객체를 JSON으로 변환하기 위해 사용

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String authorizationHeader = request.getHeader("Authorization"); // Authorization 헤더에서 토큰을 추출

        // 1. 토큰 추출 로직 (헤더값이 없거나 Bearer로 시작하지 않는 경우 다음 필터로 이동)
        if (authorizationHeader == null || !authorizationHeader.startsWith(BEARER_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }
        // 위 조건을 모두 통과한 경우 -> "Bearer " 접두사 제거하여 실제 토큰 추출
        String token = authorizationHeader.substring(BEARER_PREFIX.length());
        try {
            Claims claims = jwtUtil.parseClaims(token); // 토큰에서 클레임 추출

            Long memberId = Long.parseLong(claims.getSubject());

            // 추출된 정보를 통해 Spring Security가 이해할 수 있도록 인증 객체를 생성해주는 작업
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            memberId, null, List.of(new SimpleGrantedAuthority("ROLE_MEMBER"))); // 인증 객체 생성
            SecurityContextHolder.getContext().setAuthentication(authentication); // SecurityContext에 인증 객체 저장
        } catch (ExpiredJwtException e) { // 토큰이 만료된 경우 예외처리
            sendErrorResponse(response, TOKEN_EXPIRED);
            return;
        } catch (JwtException e) { // 토큰이 유효하지 않은 경우 예외처리 (형식이 잘못되었거나 서명이 유효하지 않은 경우)
            sendErrorResponse(response, TOKEN_INVALID);
            return;
        }

        filterChain.doFilter(request, response); // 다음 필터로 넘어감
    }

    private void sendErrorResponse(HttpServletResponse response, ErrorCode errorCode) throws IOException {
        response.setStatus(errorCode.getHttpStatus().value());
        response.setContentType("application/json; charset=UTF-8");

        // 응답 본문에 포함될 JSON 객체 생성 (success, data, error, timestamp 필드 포함)
        Map<String, Object> body = new HashMap<>();
        body.put("success", false);
        body.put("data", null);
        body.put("error", Map.of(
                "code", errorCode.getCode(),
                "message", errorCode.getMessage()
        ));
        body.put("timestamp", LocalDateTime.now().toString());

        response.getWriter() // Map 객체를 JSON 문자열로 변환하여 응답 본문에 작성
                .write(objectMapper.writeValueAsString(body));
    }
}