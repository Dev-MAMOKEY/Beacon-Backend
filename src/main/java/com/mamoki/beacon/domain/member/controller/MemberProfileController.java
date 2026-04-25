package com.mamoki.beacon.domain.member.controller;

import com.mamoki.beacon.domain.member.dto.password.MemberPaswordUpdateRequest;
import com.mamoki.beacon.domain.member.dto.profile.MemberProfileResponse;
import com.mamoki.beacon.domain.member.dto.profile.MemberProfileUpdateRequest;
import com.mamoki.beacon.domain.member.service.MemberService;
import com.mamoki.beacon.global.rsdata.RsData;
import com.mamoki.beacon.global.swagger.ApiJwtErrorResponse;
import com.mamoki.beacon.global.swagger.SwaggerErrorExamples;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Member", description = "회원 프로필 API")
@RestController
@RequestMapping("/api/v1/members")
@RequiredArgsConstructor
public class MemberProfileController {

    private final MemberService memberService;

    @Operation(summary = "내 정보 조회", description = "로그인한 회원의 프로필 정보를 조회합니다.",
        security = @SecurityRequirement(name = "BearerAuth"))
    @ApiResponse(responseCode = "200", description = "내 정보 조회 성공")
    @ApiResponse(responseCode = "404", description = "회원이 존재하지 않는 경우",
        content = @Content(mediaType = "application/json", examples =
            @ExampleObject(value = SwaggerErrorExamples.MEMBER_NOT_FOUND)))
    @ApiJwtErrorResponse
    @GetMapping("/me")
    public ResponseEntity<RsData<MemberProfileResponse>> getMyInfo(@AuthenticationPrincipal Long memberId) {
        MemberProfileResponse response = memberService.getMyInfo(memberId);
        return ResponseEntity.ok(RsData.success(response));
    }

    @Operation(summary = "내 정보 수정", description = "로그인한 회원의 이름 및 푸시 알림 수신 여부를 수정합니다.",
        security = @SecurityRequirement(name = "BearerAuth"))
    @ApiResponse(responseCode = "200", description = "내 정보 수정 성공")
    @ApiResponse(responseCode = "404", description = "회원이 존재하지 않는 경우",
        content = @Content(mediaType = "application/json", examples =
            @ExampleObject(value = SwaggerErrorExamples.MEMBER_NOT_FOUND)))
    @ApiJwtErrorResponse
    @PatchMapping("/me")
    public ResponseEntity<RsData<MemberProfileResponse>> updateMyInfo(@AuthenticationPrincipal Long memberId, @Valid @RequestBody MemberProfileUpdateRequest request) {
        MemberProfileResponse response = memberService.updateProfile(memberId, request);
        return ResponseEntity.ok(RsData.success(response));
    }

    @Operation(summary = "비밀번호 변경", description = "현재 비밀번호를 확인 후 새 비밀번호로 변경합니다.",
        security = @SecurityRequirement(name = "BearerAuth"))
    @ApiResponse(responseCode = "200", description = "비밀번호 변경 성공")
    @ApiResponse(responseCode = "401", description = "현재 비밀번호 불일치 또는 새 비밀번호 확인 불일치",
        content = @Content(mediaType = "application/json", examples = {
            @ExampleObject(name = "INVALID_CREDENTIALS",          value = SwaggerErrorExamples.INVALID_CREDENTIALS),
            @ExampleObject(name = "PASSWORD_CONFIRMATION_MISMATCH",value = SwaggerErrorExamples.PASSWORD_CONFIRMATION_MISMATCH)
        }))
    @ApiResponse(responseCode = "404", description = "회원이 존재하지 않는 경우",
        content = @Content(mediaType = "application/json", examples =
            @ExampleObject(value = SwaggerErrorExamples.MEMBER_NOT_FOUND)))
    @ApiJwtErrorResponse
    @PatchMapping("/me/password")
    @ResponseBody
    public ResponseEntity<RsData<Void>> updateMyPassword(@AuthenticationPrincipal Long memberId, @Valid @RequestBody MemberPaswordUpdateRequest request) {
        memberService.updatePassword(memberId, request);
        return ResponseEntity.ok(RsData.success(null));
    }
}