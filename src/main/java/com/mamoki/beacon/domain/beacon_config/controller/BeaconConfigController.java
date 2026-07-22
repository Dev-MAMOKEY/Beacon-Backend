package com.mamoki.beacon.domain.beacon_config.controller;

import com.mamoki.beacon.domain.beacon_config.dto.BeaconConfigDto;
import com.mamoki.beacon.domain.beacon_config.service.BeaconConfigService;
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
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "BeaconConfig", description = "비콘 설정 API")
@RestController
@RequestMapping("/api/v1/clubs/{clubId}/beacon")
@RequiredArgsConstructor
public class BeaconConfigController {
    private final BeaconConfigService beaconConfigService;

    @Operation(summary = "비콘 설정 조회", description = "동아리의 비콘 설정을 조회합니다. 설정이 없으면 기본값으로 생성 후 반환합니다. 동아리 멤버만 가능합니다.",
        security = @SecurityRequirement(name = "BearerAuth"))
    @ApiResponse(responseCode = "200", description = "비콘 설정 조회 성공")
    @ApiResponse(responseCode = "404", description = "동아리가 존재하지 않는 경우",
        content = @Content(mediaType = "application/json", examples =
            @ExampleObject(value = SwaggerErrorExamples.CLUB_NOT_FOUND)))
    @ApiJwtErrorResponse
    @GetMapping
    public ResponseEntity<RsData<BeaconConfigDto>> getBeaconConfig(
            @AuthenticationPrincipal Long memberId,
            @PathVariable Long clubId) {
        return ResponseEntity.ok(RsData.success(beaconConfigService.getConfig(memberId, clubId)));
    }

    @Operation(summary = "비콘 설정 수정", description = "동아리의 비콘 설정(UUID, 지각 기준, 안정화 시간, RSSI 임계값)을 수정합니다. ADMIN만 가능합니다.",
        security = @SecurityRequirement(name = "BearerAuth"))
    @ApiResponse(responseCode = "200", description = "비콘 설정 수정 성공")
    @ApiResponse(responseCode = "404", description = "동아리가 존재하지 않는 경우",
        content = @Content(mediaType = "application/json", examples =
            @ExampleObject(value = SwaggerErrorExamples.CLUB_NOT_FOUND)))
    @ApiAdminErrorResponse
    @PutMapping
    public ResponseEntity<RsData<BeaconConfigDto>> updateBeaconConfig(
            @AuthenticationPrincipal Long memberId,
            @PathVariable Long clubId,
            @Valid @RequestBody BeaconConfigDto beaconConfigDto) {
        return ResponseEntity.ok(RsData.success(beaconConfigService.updateConfig(memberId, clubId, beaconConfigDto)));
    }
}
