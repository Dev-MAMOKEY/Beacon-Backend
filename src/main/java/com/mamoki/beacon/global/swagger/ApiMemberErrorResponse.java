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
 * 동아리 MEMBER 이상이 접근하는 엔드포인트에 붙는 공통 에러 응답 어노테이션
 * - 401 : TOKEN_EXPIRED, TOKEN_INVALID
 * - 403 : NOT_CLUB_MEMBER
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
    ),
    @ApiResponse(
        responseCode = "403",
        description = "해당 동아리의 멤버가 아닌 경우",
        content = @Content(mediaType = "application/json", examples =
            @ExampleObject(name = "NOT_CLUB_MEMBER", value = SwaggerErrorExamples.NOT_CLUB_MEMBER)
        )
    )
})
public @interface ApiMemberErrorResponse {}