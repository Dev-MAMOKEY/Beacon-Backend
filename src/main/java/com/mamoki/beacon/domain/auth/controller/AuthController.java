package com.mamoki.beacon.domain.auth.controller;

import com.mamoki.beacon.domain.auth.dto.*;
import com.mamoki.beacon.domain.auth.service.AuthService;
import com.mamoki.beacon.global.rsdata.RsData;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth") // Base URL + 인증 관련 엔드포인트
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<RsData<SignupResponse>> signup(@Valid @RequestBody SignupRequest request) {
        SignupResponse response = authService.signup(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(RsData.success(response));
    }

    @PostMapping("/login")
    public ResponseEntity<RsData<TokenResponse>> login(@Valid @RequestBody LoginRequest request) {
        TokenResponse response = authService.login(request);
        return ResponseEntity.ok(RsData.success(response));
    }

    @PostMapping("/refresh")
    public ResponseEntity<RsData<TokenResponse>> refresh(@RequestBody ReissueTokenRequest request) {
        TokenResponse response = authService.reissue(request);
        return ResponseEntity.ok(RsData.success(response));
    }

    @PostMapping("/logout")
    public ResponseEntity<RsData<Void>> logout(@AuthenticationPrincipal Long memberId) {
        authService.logout(memberId);
        return ResponseEntity.ok(RsData.success(null));
    }
}
