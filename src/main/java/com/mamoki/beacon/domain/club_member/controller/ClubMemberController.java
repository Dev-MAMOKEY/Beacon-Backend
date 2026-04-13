package com.mamoki.beacon.domain.club_member.controller;

import com.mamoki.beacon.domain.club_member.dto.RoleUpdateRequest;
import com.mamoki.beacon.domain.club_member.service.ClubMemberService;
import com.mamoki.beacon.global.rsdata.RsData;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/clubs")
@RequiredArgsConstructor
public class ClubMemberController {

    private final ClubMemberService clubMemberService;

    @GetMapping("/{clubId}/members")
    public ResponseEntity<RsData> getClubMembers(@PathVariable("clubId") Long clubId, @AuthenticationPrincipal Long requesterId) {
        return ResponseEntity.ok().body(RsData.success(clubMemberService.getClubMembers(requesterId, clubId)));
    }

    @PatchMapping("/{clubId}/members/{memberId}/role")
    public ResponseEntity<RsData> updateRole(@PathVariable("clubId") Long clubId, @PathVariable("memberId") Long memberId,@AuthenticationPrincipal Long requesterId, @RequestBody RoleUpdateRequest request) {
        request = new RoleUpdateRequest(requesterId, clubId, memberId, request.newRole());
        clubMemberService.updateRole(request);
        return ResponseEntity.ok().body(RsData.success(null));
    }

    @DeleteMapping("/{clubId}/members/{memberId}")
    public ResponseEntity<RsData> softdeletedMember(@PathVariable("clubId") Long clubId, @PathVariable("memberId") Long memberId, @AuthenticationPrincipal Long requesterId) {
        clubMemberService.softdeletedMember(requesterId, clubId, memberId);
        return ResponseEntity.ok().body(RsData.success(null));
    }
}

