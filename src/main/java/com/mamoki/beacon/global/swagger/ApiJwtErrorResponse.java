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
 * - TOKEN_EXPIRED  : Access Token 만료
 * - TOKEN_INVALID  : 토큰 형식/서명 오류
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@ApiResponses({
    @ApiResponse(
        responseCode = "401",
        description = "Access Token 만료 또는 형식 오류",
        content = @Content(mediaType = "application/json", examples = {
            @ExampleObject(name = "TOKEN_EXPIRED",  value = SwaggerErrorExamples.TOKEN_EXPIRED),
            @ExampleObject(name = "TOKEN_INVALID",  value = SwaggerErrorExamples.TOKEN_INVALID)
        })
    )
})
public @interface ApiJwtErrorResponse {}