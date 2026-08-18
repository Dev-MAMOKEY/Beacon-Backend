package com.mamoki.beacon.global.swagger;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * JWT 인증이 필요한 모든 엔드포인트에 공통으로 붙는 401 응답 어노테이션
 * - TOKEN_MISSING : Authorization 헤더 없음
 * - TOKEN_EXPIRED : Access Token 만료
 * - TOKEN_INVALID : 토큰 형식/서명 오류
 * <p>
 * 여기서 나오는 401은 전부 <b>Access Token</b>에 대한 것이며,
 * 프론트는 이 코드를 받으면 POST /api/v1/auth/refresh 로 재발급을 시도하면 된다.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@ApiResponses({
    @ApiResponse(
        responseCode = "401",
        description = "Access Token 없음 / 만료 / 형식 오류 → 재발급(/auth/refresh) 후 재시도",
        content = @Content(mediaType = "application/json", examples = {
            @ExampleObject(name = "TOKEN_MISSING",  value = SwaggerErrorExamples.TOKEN_MISSING),
            @ExampleObject(name = "TOKEN_EXPIRED",  value = SwaggerErrorExamples.TOKEN_EXPIRED),
            @ExampleObject(name = "TOKEN_INVALID",  value = SwaggerErrorExamples.TOKEN_INVALID)
        })
    )
})
public @interface ApiJwtErrorResponse {}