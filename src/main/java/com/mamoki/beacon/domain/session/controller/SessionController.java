package com.mamoki.beacon.domain.session.controller;

import com.mamoki.beacon.domain.session.dto.SessionCreateRequestDto;
import com.mamoki.beacon.domain.session.dto.SessionDto;
import com.mamoki.beacon.domain.session.dto.SessionStartDto;
import com.mamoki.beacon.domain.session.entity.Session;
import com.mamoki.beacon.domain.session.entity.SessionStatus;
import com.mamoki.beacon.domain.session.service.SessionService;
import com.mamoki.beacon.global.rsdata.RsData;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/clubs/{clubId}/sessions")
@RequiredArgsConstructor
public class SessionController {
    private final SessionService sessionService;

    // 세션 생성 (ADMIN)
    @PostMapping
    public ResponseEntity<RsData<Void>> createSession(
            @AuthenticationPrincipal Long memberId,
            @PathVariable Long clubId,
            @RequestBody SessionCreateRequestDto requestDto) {
        sessionService.createSession(memberId, clubId, requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(RsData.success(null));
    }

    // 세션 목록 조회 (MEMBER)
    @GetMapping
    public ResponseEntity<RsData<Slice<Session>>> getSessions(
            @AuthenticationPrincipal Long memberId,
            @PathVariable Long clubId,
            @RequestParam(required = false) SessionStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Slice<Session> sessions = sessionService.getSessionsByClub(clubId, status, pageable);
        return ResponseEntity.ok().body(RsData.success(sessions));
    }

    // 활성 세션 조회 (MEMBER)
    @GetMapping("/active")
    public ResponseEntity<RsData<Session>> getActiveSession(
            @AuthenticationPrincipal Long memberId,
            @PathVariable Long clubId) {
        Session session = sessionService.getActiveSession(memberId, clubId);
        return ResponseEntity.ok().body(RsData.success(session));
    }

    // 세션 상세 조회 (MEMBER)
    @GetMapping("/{sessionId}")
    public ResponseEntity<RsData<Session>> getSession(
            @AuthenticationPrincipal Long memberId,
            @PathVariable Long clubId,
            @PathVariable Long sessionId) {
        Session session = sessionService.getSessionDetail(memberId, clubId, sessionId);
        return ResponseEntity.ok().body(RsData.success(session));
    }

    // 세션 수정 (ADMIN)
    @PatchMapping("/{sessionId}")
    public ResponseEntity<RsData<String>> updatedSession(
            @AuthenticationPrincipal Long memberId,
            @PathVariable Long clubId,
            @PathVariable Long sessionId,
            @RequestBody SessionDto sessionDto) {
        sessionService.updatedSession(memberId, sessionId, sessionDto);
        return ResponseEntity.ok().body(RsData.success("세션이 업데이트되었습니다."));
    }

    // 세션 삭제 (ADMIN)
    @DeleteMapping("/{sessionId}")
    public ResponseEntity<RsData<String>> softDeletedSession(
            @AuthenticationPrincipal Long memberId,
            @PathVariable Long clubId,
            @PathVariable Long sessionId) {
        sessionService.softDeletedSession(memberId, sessionId);
        return ResponseEntity.ok().body(RsData.success("세션이 삭제되었습니다."));
    }

    // 세션 시작 (ADMIN)
    @PostMapping("/{sessionId}/start")
    public ResponseEntity<RsData<SessionStartDto>> startSession(
            @AuthenticationPrincipal Long memberId,
            @PathVariable Long clubId,
            @PathVariable Long sessionId) {
        SessionStartDto result = sessionService.startedSession(memberId, sessionId);
        return ResponseEntity.ok().body(RsData.success(result));
    }

    // 세션 종료 (ADMIN)
    @PostMapping("/{sessionId}/end")
    public ResponseEntity<RsData<String>> endedSession(
            @AuthenticationPrincipal Long memberId,
            @PathVariable Long clubId,
            @PathVariable Long sessionId) {
        sessionService.endedSession(memberId, sessionId);
        return ResponseEntity.ok().body(RsData.success("세션이 종료되었습니다."));
    }
}
