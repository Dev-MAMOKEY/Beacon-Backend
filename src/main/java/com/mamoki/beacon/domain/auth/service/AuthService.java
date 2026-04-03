package com.mamoki.beacon.domain.auth.service;

import com.mamoki.beacon.global.security.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) // 읽기 전용 트랜잭션 설정 -> 쓰기 사용 시에는 @Transactional 사용
public class AuthService {

    private final JwtProvider jwtProvider;
}
