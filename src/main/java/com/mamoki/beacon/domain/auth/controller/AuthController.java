package com.mamoki.beacon.domain.auth.controller;

import com.mamoki.beacon.domain.auth.dto.*;
import com.mamoki.beacon.domain.auth.service.AuthService;
import com.mamoki.beacon.global.rsdata.RsData;
import com.mamoki.beacon.global.swagger.ApiJwtErrorResponse;
import com.mamoki.beacon.global.swagger.SwaggerErrorExamples;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Auth", description = "인증 API")
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "회원가입", description = "학번, 비밀번호, 학년, 이름으로 신규 회원을 등록합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "회원가입 성공"),
        @ApiResponse(responseCode = "400", description = "유효성 검증 실패 (학번 8자리 아님 / 비밀번호 형식 오류 / 필수값 누락)",
            content = @Content(mediaType = "application/json", examples =
                @ExampleObject(value = "{\"success\":false,\"data\":null,\"error\":{\"code\":\"VALIDATION_FAILED\",\"message\":\"비밀번호는 영어와 숫자를 각각 1개 이상 포함해야 합니다.\"},\"timestamp\":\"2025-04-25T10:00:00\"}"))),
        @ApiResponse(responseCode = "409", description = "이미 사용중인 학번",
            content = @Content(mediaType = "application/json", examples =
                @ExampleObject(value = SwaggerErrorExamples.DUPLICATE_STUDENT_ID)))
    })
    @PostMapping("/signup")
    public ResponseEntity<RsData<SignupResponse>> signup(@Valid @RequestBody SignupRequest request) {
        SignupResponse response = authService.signup(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(RsData.success(response));
    }

    @Operation(summary = "로그인", description = "학번과 비밀번호로 로그인하여 Access Token, Refresh Token을 발급받습니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "로그인 성공"),
        @ApiResponse(responseCode = "401", description = "학번 없음 또는 비밀번호 불일치",
            content = @Content(mediaType = "application/json", examples =
                @ExampleObject(value = SwaggerErrorExamples.INVALID_CREDENTIALS)))
    })
    @PostMapping("/login")
    public ResponseEntity<RsData<TokenResponse>> login(@Valid @RequestBody LoginRequest request) {
        TokenResponse response = authService.login(request);
        return ResponseEntity.ok(RsData.success(response));
    }

    @Operation(summary = "토큰 재발급", description = "Refresh Token으로 새로운 Access Token과 Refresh Token을 재발급합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "토큰 재발급 성공"),
        @ApiResponse(responseCode = "401", description = "토큰 만료 / 형식 오류 / 이미 무효화된 Refresh Token",
            content = @Content(mediaType = "application/json", examples = {
                @ExampleObject(name = "TOKEN_EXPIRED",        value = SwaggerErrorExamples.TOKEN_EXPIRED),
                @ExampleObject(name = "TOKEN_INVALID",        value = SwaggerErrorExamples.TOKEN_INVALID),
                @ExampleObject(name = "REFRESH_TOKEN_REVOKED",value = SwaggerErrorExamples.REFRESH_TOKEN_REVOKED)
            })),
        @ApiResponse(responseCode = "404", description = "토큰에 담긴 회원이 존재하지 않는 경우",
            content = @Content(mediaType = "application/json", examples =
                @ExampleObject(value = SwaggerErrorExamples.MEMBER_NOT_FOUND)))
    })
    @PostMapping("/refresh")
    public ResponseEntity<RsData<TokenResponse>> refresh(@RequestBody ReissueTokenRequest request) {
        TokenResponse response = authService.reissue(request);
        return ResponseEntity.ok(RsData.success(response));
    }

    @Operation(summary = "로그아웃", description = "현재 로그인된 회원의 Refresh Token을 무효화합니다.",
        security = @SecurityRequirement(name = "BearerAuth"))
    @ApiResponse(responseCode = "200", description = "로그아웃 성공")
    @ApiResponse(responseCode = "404", description = "회원이 존재하지 않는 경우",
        content = @Content(mediaType = "application/json", examples =
            @ExampleObject(value = SwaggerErrorExamples.MEMBER_NOT_FOUND)))
    @ApiJwtErrorResponse
    @PostMapping("/logout")
    public ResponseEntity<RsData<Void>> logout(@AuthenticationPrincipal Long memberId) {
        authService.logout(memberId);
        return ResponseEntity.ok(RsData.success(null));
    }
}