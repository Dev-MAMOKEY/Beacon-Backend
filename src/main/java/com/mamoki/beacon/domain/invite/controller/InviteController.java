package com.mamoki.beacon.domain.invite.controller;

import com.mamoki.beacon.domain.invite.dto.InviteRequestDto;
import com.mamoki.beacon.domain.invite.dto.InviteResponseDto;
import com.mamoki.beacon.domain.invite.service.InviteService;
import com.mamoki.beacon.global.rsdata.RsData;
import com.mamoki.beacon.global.swagger.ApiAdminErrorResponse;
import com.mamoki.beacon.global.swagger.ApiJwtErrorResponse;
import com.mamoki.beacon.global.swagger.SwaggerErrorExamples;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Invite", description = "초대코드 API")
@RestController
@RequestMapping("/api/v1/clubs")
@RequiredArgsConstructor
public class InviteController {
    private final InviteService inviteService;

    @Operation(summary = "초대코드 발급", description = "동아리 초대코드를 발급합니다. 기존 유효 코드가 있으면 자동으로 무효화 후 새 코드를 발급합니다. ADMIN만 가능합니다.",
        security = @SecurityRequirement(name = "BearerAuth"))
    @ApiResponse(responseCode = "200", description = "초대코드 발급 성공")
    @ApiResponse(responseCode = "404", description = "동아리 또는 회원이 존재하지 않는 경우",
        content = @Content(mediaType = "application/json", examples = {
            @ExampleObject(name = "CLUB_NOT_FOUND",   value = SwaggerErrorExamples.CLUB_NOT_FOUND),
            @ExampleObject(name = "MEMBER_NOT_FOUND", value = SwaggerErrorExamples.MEMBER_NOT_FOUND)
        }))
    @ApiAdminErrorResponse
    @PostMapping("/{clubId}/invite-code")
    public ResponseEntity<RsData<InviteResponseDto>> issueInviteCode(
            @AuthenticationPrincipal Long memberId,
            @PathVariable("clubId") Long clubId) {
        String code = inviteService.requestInviteCode(memberId, clubId);
        return ResponseEntity.ok(RsData.success(new InviteResponseDto(code)));
    }

    @Operation(summary = "현재 유효한 초대코드 조회", description = "현재 유효한 초대코드를 조회합니다. ADMIN만 가능합니다.",
        security = @SecurityRequirement(name = "BearerAuth"))
    @ApiResponse(responseCode = "200", description = "초대코드 조회 성공")
    @ApiResponse(responseCode = "400", description = "유효한 초대코드가 없는 경우",
        content = @Content(mediaType = "application/json", examples =
            @ExampleObject(value = SwaggerErrorExamples.INVALID_INVITE_CODE)))
    @ApiResponse(responseCode = "404", description = "동아리가 존재하지 않는 경우",
        content = @Content(mediaType = "application/json", examples =
            @ExampleObject(value = SwaggerErrorExamples.CLUB_NOT_FOUND)))
    @ApiAdminErrorResponse
    @GetMapping("/{clubId}/invite-code")
    public ResponseEntity<RsData<InviteResponseDto>> getCurrentInviteCode(
            @AuthenticationPrincipal Long memberId,
            @PathVariable("clubId") Long clubId) {
        String code = inviteService.getCurrentInviteCode(memberId, clubId);
        return ResponseEntity.ok(RsData.success(new InviteResponseDto(code)));
    }

    @Operation(summary = "초대코드 무효화", description = "현재 유효한 초대코드를 수동으로 무효화합니다. ADMIN만 가능합니다.",
        security = @SecurityRequirement(name = "BearerAuth"))
    @ApiResponse(responseCode = "200", description = "초대코드 무효화 성공")
    @ApiResponse(responseCode = "400", description = "유효한 초대코드가 없는 경우",
        content = @Content(mediaType = "application/json", examples =
            @ExampleObject(value = SwaggerErrorExamples.INVALID_INVITE_CODE)))
    @ApiResponse(responseCode = "404", description = "동아리가 존재하지 않는 경우",
        content = @Content(mediaType = "application/json", examples =
            @ExampleObject(value = SwaggerErrorExamples.CLUB_NOT_FOUND)))
    @ApiAdminErrorResponse
    @DeleteMapping("/{clubId}/invite-code")
    public ResponseEntity<RsData<Void>> revokeInviteCode(
            @AuthenticationPrincipal Long memberId,
            @PathVariable("clubId") Long clubId) {
        inviteService.revokeInviteCode(memberId, clubId);
        return ResponseEntity.ok(RsData.success(null));
    }

    @Operation(summary = "초대코드로 동아리 가입", description = "초대코드를 입력하여 동아리에 가입합니다.",
        security = @SecurityRequirement(name = "BearerAuth"))
    @ApiResponse(responseCode = "200", description = "동아리 가입 성공")
    @ApiResponse(responseCode = "400", description = "초대코드가 유효하지 않거나 무효화된 경우",
        content = @Content(mediaType = "application/json", examples =
            @ExampleObject(value = SwaggerErrorExamples.INVALID_INVITE_CODE)))
    @ApiResponse(responseCode = "404", description = "동아리 또는 회원이 존재하지 않는 경우",
        content = @Content(mediaType = "application/json", examples = {
            @ExampleObject(name = "CLUB_NOT_FOUND",   value = SwaggerErrorExamples.CLUB_NOT_FOUND),
            @ExampleObject(name = "MEMBER_NOT_FOUND", value = SwaggerErrorExamples.MEMBER_NOT_FOUND)
        }))
    @ApiResponse(responseCode = "409", description = "이미 해당 동아리의 멤버인 경우",
        content = @Content(mediaType = "application/json", examples =
            @ExampleObject(value = SwaggerErrorExamples.ALREADY_CLUB_MEMBER)))
    @ApiJwtErrorResponse
    @PostMapping("/join")
    public ResponseEntity<RsData<Void>> joinClub(
            @AuthenticationPrincipal Long memberId,
            @RequestBody InviteRequestDto request) {
        inviteService.responseInviteCode(memberId, request.clubId(), request.inviteCode());
        return ResponseEntity.ok(RsData.success(null));
    }
}