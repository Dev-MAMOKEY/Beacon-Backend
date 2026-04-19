package com.mamoki.beacon.domain.invite.controller;

import com.mamoki.beacon.domain.invite.dto.InviteRequestDto;
import com.mamoki.beacon.domain.invite.dto.InviteResponseDto;
import com.mamoki.beacon.domain.invite.service.InviteService;
import com.mamoki.beacon.global.rsdata.RsData;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/clubs")
@RequiredArgsConstructor
public class InviteController {
    private final InviteService inviteService;

    //초대코드 발급 (ADMIN)
    @PostMapping("/{clubId}/invite-code")
    public ResponseEntity<RsData<InviteResponseDto>> issueInviteCode(
            @AuthenticationPrincipal Long memberId,
            @PathVariable("clubId") Long clubId) {
        String code = inviteService.requestInviteCode(memberId, clubId);
        return ResponseEntity.ok(RsData.success(new InviteResponseDto(code)));
    }

    //현재 유효한 초대코드 조회 (ADMIN)
    @GetMapping("/{clubId}/invite-code")
    public ResponseEntity<RsData<InviteResponseDto>> getCurrentInviteCode(
            @AuthenticationPrincipal Long memberId,
            @PathVariable("clubId") Long clubId) {
        String code = inviteService.getCurrentInviteCode(memberId, clubId);
        return ResponseEntity.ok(RsData.success(new InviteResponseDto(code)));
    }

    //초대코드 무효화 (ADMIN)
    @DeleteMapping("/{clubId}/invite-code")
    public ResponseEntity<RsData<Void>> revokeInviteCode(
            @AuthenticationPrincipal Long memberId,
            @PathVariable("clubId") Long clubId) {
        inviteService.revokeInviteCode(memberId, clubId);
        return ResponseEntity.ok(RsData.success(null));
    }

    //초대코드로 가입 (MEMBER)
    @PostMapping("/join")
    public ResponseEntity<RsData<Void>> joinClub(
            @AuthenticationPrincipal Long memberId,
            @RequestBody InviteRequestDto request) {
        inviteService.responseInviteCode(memberId, request.clubId(), request.inviteCode());
        return ResponseEntity.ok(RsData.success(null));
    }
}
