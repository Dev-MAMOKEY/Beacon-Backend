package com.mamoki.beacon.domain.club.controller;

import com.mamoki.beacon.domain.club.dto.ClubCreateResponseDto;
import com.mamoki.beacon.domain.club.dto.ClubDto;
import com.mamoki.beacon.domain.club.dto.ClubResponseDto;
import com.mamoki.beacon.domain.club.service.ClubService;
import com.mamoki.beacon.global.rsdata.RsData;
import com.mamoki.beacon.global.swagger.ApiAdminErrorResponse;
import com.mamoki.beacon.global.swagger.ApiJwtErrorResponse;
import com.mamoki.beacon.global.swagger.SwaggerErrorExamples;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Club", description = "동아리 API")
@RestController
@RequestMapping("/api/v1/clubs")
@RequiredArgsConstructor
public class ClubController {
    private final ClubService clubService;

    @Operation(summary = "동아리 생성", description = "새로운 동아리를 생성합니다. 생성한 회원은 자동으로 ADMIN 역할을 부여받습니다.",
        security = @SecurityRequirement(name = "BearerAuth"))
    @ApiResponse(responseCode = "201", description = "동아리 생성 성공")
    @ApiResponse(responseCode = "404", description = "회원이 존재하지 않는 경우",
        content = @Content(mediaType = "application/json", examples =
            @ExampleObject(value = SwaggerErrorExamples.MEMBER_NOT_FOUND)))
    @ApiJwtErrorResponse
    @PostMapping("/create")
    public ResponseEntity<RsData<ClubCreateResponseDto>> createClub(@AuthenticationPrincipal Long memberId, @RequestBody ClubDto clubDto) {
        ClubCreateResponseDto response = clubService.createClub(memberId, clubDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(RsData.success(response));
    }

    @Operation(summary = "동아리 정보 수정", description = "동아리 이름 또는 설명을 수정합니다. ADMIN만 가능합니다.",
        security = @SecurityRequirement(name = "BearerAuth"))
    @ApiResponse(responseCode = "200", description = "동아리 수정 성공")
    @ApiResponse(responseCode = "404", description = "동아리가 존재하지 않는 경우",
        content = @Content(mediaType = "application/json", examples =
            @ExampleObject(value = SwaggerErrorExamples.CLUB_NOT_FOUND)))
    @ApiAdminErrorResponse
    @PatchMapping("/{clubId}")
    public ResponseEntity<RsData<String>> updateClub(@AuthenticationPrincipal Long memberId, @PathVariable Long clubId, @RequestBody ClubDto clubDto) {
        clubService.updateClub(memberId, clubId, clubDto);
        return ResponseEntity.ok().body(RsData.success("동아리 정보가 업데이트되었습니다."));
    }

    @Operation(summary = "동아리 삭제 (소프트 삭제)", description = "동아리를 삭제 처리합니다. ADMIN만 가능합니다.",
        security = @SecurityRequirement(name = "BearerAuth"))
    @ApiResponse(responseCode = "200", description = "동아리 삭제 성공")
    @ApiResponse(responseCode = "404", description = "동아리가 존재하지 않는 경우",
        content = @Content(mediaType = "application/json", examples =
            @ExampleObject(value = SwaggerErrorExamples.CLUB_NOT_FOUND)))
    @ApiAdminErrorResponse
    @PatchMapping("/soft-delete/{clubId}")
    public ResponseEntity<RsData<String>> softDeleteClub(@AuthenticationPrincipal Long memberId, @PathVariable Long clubId) {
        clubService.softDeleteClub(memberId, clubId);
        return ResponseEntity.ok().body(RsData.success("동아리가 소프트 삭제되었습니다."));
    }

    @Operation(summary = "동아리 상세 조회", description = "동아리 ID로 동아리 상세 정보를 조회합니다.",
        security = @SecurityRequirement(name = "BearerAuth"))
    @ApiResponse(responseCode = "200", description = "동아리 조회 성공")
    @ApiResponse(responseCode = "400", description = "이미 삭제된 동아리인 경우",
        content = @Content(mediaType = "application/json", examples =
            @ExampleObject(value = SwaggerErrorExamples.CLUB_ALREADY_DELETED)))
    @ApiResponse(responseCode = "404", description = "동아리가 존재하지 않는 경우",
        content = @Content(mediaType = "application/json", examples =
            @ExampleObject(value = SwaggerErrorExamples.CLUB_NOT_FOUND)))
    @ApiJwtErrorResponse
    @GetMapping("/{clubId}")
    public ResponseEntity<RsData<ClubResponseDto>> getClub(@PathVariable Long clubId) {
        ClubResponseDto clubResponseDto = clubService.searchClub(clubId);
        return ResponseEntity.ok().body(RsData.success(clubResponseDto));
    }
}