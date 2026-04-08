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
@RequestMapping("/api/v1/invites")
@RequiredArgsConstructor
public class InviteController {
    private final InviteService inviteService;

    //어드민 초대코드 확인용 api
    @GetMapping("/{clubId}")
    public ResponseEntity<RsData<InviteResponseDto>> getInviteCode(
            @AuthenticationPrincipal Long memberId,
            @PathVariable Long clubId) throws Exception {

        String code = inviteService.requestInviteCode(memberId, clubId);
        return ResponseEntity.ok(RsData.success(new InviteResponseDto(code)));
    }

    // 회원이 초대코드로 가입하기위한 api
    @PostMapping("/{clubId}/join")
    public ResponseEntity<RsData<Void>> joinClub(
            @AuthenticationPrincipal Long memberId,
            @PathVariable Long clubId,
            @RequestBody InviteRequestDto request) throws Exception {

        inviteService.responseInviteCode(memberId, clubId, request.inviteCode());
        return ResponseEntity.ok(RsData.success(null));
    }
}
