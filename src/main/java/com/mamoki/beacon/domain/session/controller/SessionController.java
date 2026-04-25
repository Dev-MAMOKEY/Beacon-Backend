package com.mamoki.beacon.domain.session.controller;

import com.mamoki.beacon.domain.session.dto.SessionCreateRequestDto;
import com.mamoki.beacon.domain.session.dto.SessionDto;
import com.mamoki.beacon.domain.session.dto.SessionStartDto;
import com.mamoki.beacon.domain.session.entity.Session;
import com.mamoki.beacon.domain.session.entity.SessionStatus;
import com.mamoki.beacon.domain.session.service.SessionService;
import com.mamoki.beacon.global.rsdata.RsData;
import com.mamoki.beacon.global.swagger.ApiAdminErrorResponse;
import com.mamoki.beacon.global.swagger.ApiMemberErrorResponse;
import com.mamoki.beacon.global.swagger.SwaggerErrorExamples;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Session", description = "세션 API")
@RestController
@RequestMapping("/api/v1/clubs/{clubId}/sessions")
@RequiredArgsConstructor
public class SessionController {
    private final SessionService sessionService;

    @Operation(summary = "세션 생성", description = "동아리 세션을 생성합니다. 반복 옵션 설정 시 요일 기반으로 여러 세션이 일괄 생성됩니다. ADMIN만 가능합니다.",
        security = @SecurityRequirement(name = "BearerAuth"))
    @ApiResponse(responseCode = "201", description = "세션 생성 성공")
    @ApiResponse(responseCode = "404", description = "동아리 또는 회원이 존재하지 않는 경우",
        content = @Content(mediaType = "application/json", examples = {
            @ExampleObject(name = "CLUB_NOT_FOUND",   value = SwaggerErrorExamples.CLUB_NOT_FOUND),
            @ExampleObject(name = "MEMBER_NOT_FOUND", value = SwaggerErrorExamples.MEMBER_NOT_FOUND)
        }))
    @ApiAdminErrorResponse
    @PostMapping
    public ResponseEntity<RsData<Void>> createSession(
            @AuthenticationPrincipal Long memberId,
            @PathVariable Long clubId,
            @RequestBody SessionCreateRequestDto requestDto) {
        sessionService.createSession(memberId, clubId, requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(RsData.success(null));
    }

    @Operation(summary = "세션 목록 조회", description = "동아리의 세션 목록을 페이지네이션으로 조회합니다. status 파라미터로 필터링 가능합니다. (SCHEDULED / ACTIVE / ENDED)",
        security = @SecurityRequirement(name = "BearerAuth"))
    @ApiResponse(responseCode = "200", description = "세션 목록 조회 성공")
    @ApiMemberErrorResponse
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

    @Operation(summary = "활성 세션 조회", description = "현재 진행 중인(ACTIVE) 세션을 조회합니다.",
        security = @SecurityRequirement(name = "BearerAuth"))
    @ApiResponse(responseCode = "200", description = "활성 세션 조회 성공")
    @ApiResponse(responseCode = "400", description = "현재 활성화된 세션이 없는 경우",
        content = @Content(mediaType = "application/json", examples =
            @ExampleObject(value = SwaggerErrorExamples.SESSION_NOT_ACTIVE)))
    @ApiMemberErrorResponse
    @GetMapping("/active")
    public ResponseEntity<RsData<Session>> getActiveSession(
            @AuthenticationPrincipal Long memberId,
            @PathVariable Long clubId) {
        Session session = sessionService.getActiveSession(memberId, clubId);
        return ResponseEntity.ok().body(RsData.success(session));
    }

    @Operation(summary = "세션 상세 조회", description = "세션 ID로 세션 상세 정보를 조회합니다.",
        security = @SecurityRequirement(name = "BearerAuth"))
    @ApiResponse(responseCode = "200", description = "세션 조회 성공")
    @ApiResponse(responseCode = "404", description = "세션이 존재하지 않는 경우",
        content = @Content(mediaType = "application/json", examples =
            @ExampleObject(value = SwaggerErrorExamples.SESSION_NOT_FOUND)))
    @ApiMemberErrorResponse
    @GetMapping("/{sessionId}")
    public ResponseEntity<RsData<Session>> getSession(
            @AuthenticationPrincipal Long memberId,
            @PathVariable Long clubId,
            @PathVariable Long sessionId) {
        Session session = sessionService.getSessionDetail(memberId, clubId, sessionId);
        return ResponseEntity.ok().body(RsData.success(session));
    }

    @Operation(summary = "세션 수정", description = "세션 이름, 예정 시작/종료 시간을 수정합니다. 종료된 세션은 수정 불가합니다. ADMIN만 가능합니다.",
        security = @SecurityRequirement(name = "BearerAuth"))
    @ApiResponse(responseCode = "200", description = "세션 수정 성공")
    @ApiResponse(responseCode = "400", description = "이미 종료된 세션인 경우",
        content = @Content(mediaType = "application/json", examples =
            @ExampleObject(value = SwaggerErrorExamples.SESSION_ALREADY_ENDED)))
    @ApiResponse(responseCode = "404", description = "세션이 존재하지 않는 경우",
        content = @Content(mediaType = "application/json", examples =
            @ExampleObject(value = SwaggerErrorExamples.SESSION_NOT_FOUND)))
    @ApiAdminErrorResponse
    @PatchMapping("/{sessionId}")
    public ResponseEntity<RsData<String>> updatedSession(
            @AuthenticationPrincipal Long memberId,
            @PathVariable Long clubId,
            @PathVariable Long sessionId,
            @RequestBody SessionDto sessionDto) {
        sessionService.updatedSession(memberId, sessionId, sessionDto);
        return ResponseEntity.ok().body(RsData.success("세션이 업데이트되었습니다."));
    }

    @Operation(summary = "세션 삭제 (소프트 삭제)", description = "세션을 삭제합니다. 진행 중인(ACTIVE) 세션은 삭제할 수 없습니다. ADMIN만 가능합니다.",
        security = @SecurityRequirement(name = "BearerAuth"))
    @ApiResponse(responseCode = "200", description = "세션 삭제 성공")
    @ApiResponse(responseCode = "400", description = "진행 중인 세션은 삭제 불가",
        content = @Content(mediaType = "application/json", examples =
            @ExampleObject(value = SwaggerErrorExamples.NOT_DELETED_SESSION)))
    @ApiResponse(responseCode = "404", description = "세션이 존재하지 않는 경우",
        content = @Content(mediaType = "application/json", examples =
            @ExampleObject(value = SwaggerErrorExamples.SESSION_NOT_FOUND)))
    @ApiAdminErrorResponse
    @DeleteMapping("/{sessionId}")
    public ResponseEntity<RsData<String>> softDeletedSession(
            @AuthenticationPrincipal Long memberId,
            @PathVariable Long clubId,
            @PathVariable Long sessionId) {
        sessionService.softDeletedSession(memberId, sessionId);
        return ResponseEntity.ok().body(RsData.success("세션이 삭제되었습니다."));
    }

    @Operation(summary = "세션 시작", description = "세션을 시작하고 ACTIVE 상태로 변경합니다. OTP 출석 코드가 발급되며, 동아리 전체 멤버에게 FCM 푸시 알림이 발송됩니다. ADMIN만 가능합니다.",
        security = @SecurityRequirement(name = "BearerAuth"))
    @ApiResponse(responseCode = "200", description = "세션 시작 성공 (OTP 코드 반환)")
    @ApiResponse(responseCode = "400", description = "OTP 코드 생성 실패",
        content = @Content(mediaType = "application/json", examples =
            @ExampleObject(value = SwaggerErrorExamples.OTP_GENERATION_FAILED)))
    @ApiResponse(responseCode = "404", description = "세션이 존재하지 않는 경우",
        content = @Content(mediaType = "application/json", examples =
            @ExampleObject(value = SwaggerErrorExamples.SESSION_NOT_FOUND)))
    @ApiResponse(responseCode = "409", description = "이미 진행 중이거나 종료된 세션인 경우",
        content = @Content(mediaType = "application/json", examples =
            @ExampleObject(value = SwaggerErrorExamples.SESSION_ALREADY_ACTIVE)))
    @ApiAdminErrorResponse
    @PostMapping("/{sessionId}/start")
    public ResponseEntity<RsData<SessionStartDto>> startSession(
            @AuthenticationPrincipal Long memberId,
            @PathVariable Long clubId,
            @PathVariable Long sessionId) {
        SessionStartDto result = sessionService.startedSession(memberId, sessionId);
        return ResponseEntity.ok().body(RsData.success(result));
    }

    @Operation(summary = "세션 종료", description = "세션을 종료하고 ENDED 상태로 변경합니다. 미출석 멤버는 자동으로 ABSENT 처리됩니다. ADMIN만 가능합니다.",
        security = @SecurityRequirement(name = "BearerAuth"))
    @ApiResponse(responseCode = "200", description = "세션 종료 성공")
    @ApiResponse(responseCode = "400", description = "활성화된 세션이 아닌 경우",
        content = @Content(mediaType = "application/json", examples =
            @ExampleObject(value = SwaggerErrorExamples.SESSION_NOT_ACTIVE)))
    @ApiResponse(responseCode = "404", description = "세션이 존재하지 않는 경우",
        content = @Content(mediaType = "application/json", examples =
            @ExampleObject(value = SwaggerErrorExamples.SESSION_NOT_FOUND)))
    @ApiAdminErrorResponse
    @PostMapping("/{sessionId}/end")
    public ResponseEntity<RsData<String>> endedSession(
            @AuthenticationPrincipal Long memberId,
            @PathVariable Long clubId,
            @PathVariable Long sessionId) {
        sessionService.endedSession(memberId, sessionId);
        return ResponseEntity.ok().body(RsData.success("세션이 종료되었습니다."));
    }
}