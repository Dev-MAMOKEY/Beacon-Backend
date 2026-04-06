package com.mamoki.beacon.domain.session.controller;

import com.mamoki.beacon.domain.session.dto.SessionDto;
import com.mamoki.beacon.domain.session.entity.Session;
import com.mamoki.beacon.domain.session.service.SessionService;
import com.mamoki.beacon.global.rsdata.RsData;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping ("/api/v1/session")
@RequiredArgsConstructor
public class SessionController {
    private final SessionService sessionService;

    @PostMapping("/create") //세션 생성 api
    public ResponseEntity<RsData<String>> createSession(@AuthenticationPrincipal Long memberId, @RequestBody SessionDto sessionDto) {
        String uuid = sessionService.createSession(memberId, sessionDto);
        return ResponseEntity.ok().body(RsData.success(uuid));
    }

    @PatchMapping("/soft-delete/{sessionId}") //세션 소프트 삭제 api
    public ResponseEntity<RsData<String>> softDeletedSession(@PathVariable Long sessionId) {
        sessionService.softDeletedSession(sessionId);
        return ResponseEntity.ok().body(RsData.success("세션이 소프트 삭제되었습니다."));
    }

    @PatchMapping("/update/{sessionId}") // 세션 수정 api
    public ResponseEntity<RsData<String>> updatedSession(@PathVariable Long sessionId, @RequestBody SessionDto sessionDto) {
        sessionService.updatedSession(sessionId, sessionDto);
        return ResponseEntity.ok().body(RsData.success("세션이 업데이트되었습니다."));
    }

    @PatchMapping("/start/{sessionId}") // 세션 시작 api
    public ResponseEntity<RsData<String>> startSession(@PathVariable Long sessionId) {
        sessionService.startedSession(sessionId);
        return ResponseEntity.ok().body(RsData.success("세션이 시작되었습니다."));
    }

    @PatchMapping("/ended/{sessionId}") // 세션 종료 api
    public ResponseEntity<RsData<String>> endedSession(@PathVariable Long sessionId) {
        sessionService.endedSession(sessionId);
        return ResponseEntity.ok().body(RsData.success("세션이 종료되었습니다."));
    }

    @GetMapping("/list/{clubId}") //세션 조회 api
    public ResponseEntity<RsData<Slice<Session>>> getSessionsByClubId(@PathVariable Long clubId, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Slice<Session> sessions = sessionService.getSessionsByClub(clubId, pageable);
        return ResponseEntity.ok().body(RsData.success(sessions));
    }
}
