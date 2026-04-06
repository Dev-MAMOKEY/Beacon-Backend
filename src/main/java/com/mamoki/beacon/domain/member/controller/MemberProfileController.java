package com.mamoki.beacon.domain.member.controller;

import com.mamoki.beacon.domain.member.dto.password.MemberPaswordUpdateRequest;
import com.mamoki.beacon.domain.member.dto.profile.MemberProfileResponse;
import com.mamoki.beacon.domain.member.dto.profile.MemberProfileUpdateRequest;
import com.mamoki.beacon.domain.member.service.MemberService;
import com.mamoki.beacon.global.rsdata.RsData;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/members") // Base URL + 회원 관련 엔드포인트
@RequiredArgsConstructor
public class MemberProfileController {

    private final MemberService memberService;

    @GetMapping("/me") // 내 정보 조회
    public ResponseEntity<RsData<MemberProfileResponse>> getMyInfo(@AuthenticationPrincipal Long memberId) {
        MemberProfileResponse response = memberService.getMyInfo(memberId);
        return ResponseEntity.ok(RsData.success(response));
    }

    // @AuthenticationPrincipal Long memberId -> SecurityContext에서 인증된 회원의 ID를 가져오는 어노테이션

    @PatchMapping("/me") // 내 정보 수정
    public ResponseEntity<RsData<MemberProfileResponse>> updateMyInfo(@AuthenticationPrincipal Long memberId, @Valid @RequestBody MemberProfileUpdateRequest request) {
        MemberProfileResponse response = memberService.updateProfile(memberId, request);
        return ResponseEntity.ok(RsData.success(response));
    }

    @PatchMapping("/me/password") // 내 비밀번호 수정
    @ResponseBody
    public ResponseEntity<RsData<Void>> updateMyPassword(@AuthenticationPrincipal Long memberId, @Valid @RequestBody MemberPaswordUpdateRequest request) {
        memberService.updatePassword(memberId, request);
        return ResponseEntity.ok(RsData.success(null));
    }
}
