package com.mamoki.beacon.domain.attendance.controller;

import com.mamoki.beacon.domain.attendance.service.AttendanceStreamService;
import com.mamoki.beacon.global.swagger.ApiAdminErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Tag(name = "AttendanceStream", description = "실시간 출석 피드(SSE) API")
@RestController
@RequestMapping("/api/v1/clubs/{clubId}/attendance")
@RequiredArgsConstructor
public class AttendanceStreamController {
    private final AttendanceStreamService attendanceStreamService;

    @Operation(summary = "실시간 출석 피드 구독 (SSE)",
        description = "출석이 발생할 때마다 attendance 이벤트를 실시간으로 수신합니다. ADMIN만 가능합니다. "
            + "브라우저 기본 EventSource는 Authorization 헤더를 못 보내므로 프론트는 fetch 기반 SSE 클라이언트를 사용해야 합니다.",
        security = @SecurityRequirement(name = "BearerAuth"))
    @ApiResponse(responseCode = "200", description = "SSE 연결 성공")
    @ApiAdminErrorResponse
    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter stream(
            @AuthenticationPrincipal Long memberId,
            @PathVariable Long clubId) {
        return attendanceStreamService.subscribe(memberId, clubId);
    }
}
