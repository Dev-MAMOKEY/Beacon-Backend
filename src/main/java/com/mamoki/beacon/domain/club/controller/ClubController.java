package com.mamoki.beacon.domain.club.controller;

import com.mamoki.beacon.domain.club.dto.ClubCreateResponseDto;
import com.mamoki.beacon.domain.club.dto.ClubDto;
import com.mamoki.beacon.domain.club.dto.ClubResponseDto;
import com.mamoki.beacon.domain.club.service.ClubService;
import com.mamoki.beacon.global.rsdata.RsData;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/clubs")
@RequiredArgsConstructor
public class ClubController {
    private final ClubService clubService;

    @PostMapping("/create") //동아리 생성 로직
    public ResponseEntity<RsData<ClubCreateResponseDto>> createClub(@AuthenticationPrincipal Long memberId, @RequestBody ClubDto clubDto) {
        ClubCreateResponseDto response = clubService.createClub(memberId, clubDto);
        return ResponseEntity.ok().body(RsData.created(response));
    }

    @PatchMapping("/{clubId}")
    public ResponseEntity<RsData<String>> updateClub(@AuthenticationPrincipal Long memberId, @PathVariable Long clubId, @RequestBody ClubDto clubDto){
        clubService.updateClub(memberId, clubId, clubDto);
        return ResponseEntity.ok().body(RsData.success("동아리 정보가 업데이트되었습니다."));
    }

    @PatchMapping("/soft-delete/{clubId}")
    public ResponseEntity<RsData<String>> softDeleteClub(@AuthenticationPrincipal Long memberId, @PathVariable Long clubId){
        clubService.softDeleteClub(memberId, clubId);
        return ResponseEntity.ok().body(RsData.success("동아리가 소프트 삭제되었습니다."));
    }

    @GetMapping("/{clubId}")
    public ResponseEntity<RsData<ClubResponseDto>> getClub(@PathVariable Long clubId){
        ClubResponseDto clubResponseDto = clubService.searchClub(clubId);
        return ResponseEntity.ok().body(RsData.success(clubResponseDto));
    }
}
