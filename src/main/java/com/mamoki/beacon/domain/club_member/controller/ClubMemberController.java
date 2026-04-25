package com.mamoki.beacon.domain.club_member.controller;

import com.mamoki.beacon.domain.club_member.dto.RoleUpdateRequest;
import com.mamoki.beacon.domain.club_member.service.ClubMemberService;
import com.mamoki.beacon.global.rsdata.RsData;
import com.mamoki.beacon.global.swagger.ApiAdminErrorResponse;
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

@Tag(name = "ClubMember", description = "동아리 멤버 API")
@RestController
@RequestMapping("/api/v1/clubs")
@RequiredArgsConstructor
public class ClubMemberController {

    private final ClubMemberService clubMemberService;

    @Operation(summary = "동아리 멤버 목록 조회", description = "동아리 멤버 목록을 조회합니다. 이름 또는 학번으로 검색 가능합니다. ADMIN만 가능합니다.",
        security = @SecurityRequirement(name = "BearerAuth"))
    @ApiResponse(responseCode = "200", description = "멤버 목록 조회 성공")
    @ApiResponse(responseCode = "404", description = "회원이 존재하지 않는 경우",
        content = @Content(mediaType = "application/json", examples =
            @ExampleObject(value = SwaggerErrorExamples.MEMBER_NOT_FOUND)))
    @ApiAdminErrorResponse
    @GetMapping("/{clubId}/members")
    public ResponseEntity<RsData> getClubMembers(
            @PathVariable("clubId") Long clubId,
            @AuthenticationPrincipal Long requesterId,
            @RequestParam(required = false) String search) {
        return ResponseEntity.ok().body(RsData.success(
                clubMemberService.getClubMembers(requesterId, clubId, search)));
    }

    @Operation(summary = "멤버 역할 변경", description = "동아리 멤버의 역할(ADMIN/MEMBER)을 변경합니다. ADMIN만 가능하며, 자기 자신의 역할은 변경할 수 없습니다.",
        security = @SecurityRequirement(name = "BearerAuth"))
    @ApiResponse(responseCode = "200", description = "역할 변경 성공")
    @ApiResponse(responseCode = "403", description = "자기 자신의 역할을 변경하려는 경우",
        content = @Content(mediaType = "application/json", examples =
            @ExampleObject(value = SwaggerErrorExamples.CANNOT_UPDATE_OWN_ROLE)))
    @ApiResponse(responseCode = "404", description = "회원이 존재하지 않거나 대상이 동아리 멤버가 아닌 경우",
        content = @Content(mediaType = "application/json", examples = {
            @ExampleObject(name = "MEMBER_NOT_FOUND", value = SwaggerErrorExamples.MEMBER_NOT_FOUND),
            @ExampleObject(name = "NOT_CLUB_MEMBER",  value = SwaggerErrorExamples.NOT_CLUB_MEMBER)
        }))
    @ApiAdminErrorResponse
    @PatchMapping("/{clubId}/members/{memberId}/role")
    public ResponseEntity<RsData> updateRole(@PathVariable("clubId") Long clubId, @PathVariable("memberId") Long memberId, @AuthenticationPrincipal Long requesterId, @RequestBody RoleUpdateRequest request) {
        request = new RoleUpdateRequest(requesterId, clubId, memberId, request.newRole());
        clubMemberService.updateRole(request);
        return ResponseEntity.ok().body(RsData.success(null));
    }

    @Operation(summary = "멤버 제명 (소프트 삭제)", description = "동아리 멤버를 제명합니다. ADMIN만 가능합니다.",
        security = @SecurityRequirement(name = "BearerAuth"))
    @ApiResponse(responseCode = "200", description = "멤버 제명 성공")
    @ApiResponse(responseCode = "404", description = "대상 멤버가 동아리에 존재하지 않는 경우",
        content = @Content(mediaType = "application/json", examples =
            @ExampleObject(value = SwaggerErrorExamples.NOT_CLUB_MEMBER)))
    @ApiAdminErrorResponse
    @DeleteMapping("/{clubId}/members/{memberId}")
    public ResponseEntity<RsData> softdeletedMember(@PathVariable("clubId") Long clubId, @PathVariable("memberId") Long memberId, @AuthenticationPrincipal Long requesterId) {
        clubMemberService.softdeletedMember(requesterId, clubId, memberId);
        return ResponseEntity.ok().body(RsData.success(null));
    }
}