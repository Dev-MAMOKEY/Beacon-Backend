package com.mamoki.beacon.global.security;

import com.mamoki.beacon.global.exception.ErrorCode;
import com.mamoki.beacon.global.rsdata.RsData;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

/**
 * 인증 실패(401) 응답을 RsData 포맷으로 통일해주는 핸들러.
 * <p>
 * 시큐리티 필터 체인에서 나는 인증 실패는 컨트롤러 진입 전이라 @RestControllerAdvice가 못 잡는다.
 * 이 훅이 없으면 "토큰 없이 보호 API 호출" 시 401이 <b>빈 body</b>로 나가서
 * 프론트가 error.code를 읽을 수 없다.
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        ErrorCode errorCode = ErrorCode.TOKEN_MISSING; // 토큰 자체가 없어서 인증 정보가 안 만들어진 상황

        response.setStatus(errorCode.getHttpStatus().value());
        response.setContentType("application/json; charset=UTF-8");
        // ResponseEntity를 쓸 수 없는 위치라 RsData를 직접 직렬화해서 body에 씀
        response.getWriter().write(objectMapper.writeValueAsString(RsData.fail(errorCode)));
    }
}
