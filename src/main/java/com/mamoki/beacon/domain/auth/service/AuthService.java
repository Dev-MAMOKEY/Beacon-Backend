package com.mamoki.beacon.domain.auth.service;

import com.mamoki.beacon.domain.auth.dto.*;
import com.mamoki.beacon.domain.club_member.entity.ClubMember;
import com.mamoki.beacon.domain.club_member.entity.Role;
import com.mamoki.beacon.domain.club_member.repository.ClubMemberRepository;
import com.mamoki.beacon.domain.member.entity.Member;
import com.mamoki.beacon.domain.member.repository.MemberRepository;
import com.mamoki.beacon.global.exception.CustomException;
import com.mamoki.beacon.global.exception.ErrorCode;
import com.mamoki.beacon.global.security.jwt.JwtProvider;
import com.mamoki.beacon.global.security.jwt.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final JwtProvider jwtProvider;
    private final JwtUtil jwtUtil;
    private final MemberRepository memberRepository;
    private final ClubMemberRepository clubMemberRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public SignupResponse signup(SignupRequest request) {
        if (memberRepository.existsByStdId(request.stdId())) {
            throw new CustomException(ErrorCode.DUPLICATE_STUDENT_ID);
        }
        Member member = memberRepository.save(
                new Member(
                        request.stdId(),
                        passwordEncoder.encode(request.password()),
                        request.grade(),
                        request.name()
                )
        );
        return member.toSignupResponse();
    }

    @Transactional
    public TokenResponse login(LoginRequest request) {
        // ① 학번으로 회원 조회
        Member member = memberRepository.findByStdId(request.stdId())
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_CREDENTIALS));

        // ② 비밀번호 검증
        if (!passwordEncoder.matches(request.password(), member.getPassword())) {
            throw new CustomException(ErrorCode.INVALID_CREDENTIALS);
        }

        // ③ FCM 토큰이 포함되어 있으면 갱신 (모바일 앱 로그인 케이스)
        if (request.fcmToken() != null && !request.fcmToken().isBlank()) {
            member.updateFcmToken(request.fcmToken());
        }

        // ④ AT/RT 발급
        String accessToken = jwtProvider.createAccessToken(member.getId());
        String refreshToken = jwtProvider.createRefreshToken(member.getId());
        member.updateRefreshToken(refreshToken); // RT DB 저장

        return new TokenResponse(accessToken, refreshToken);
    }

    @Transactional
    public TokenResponse reissue(ReissueTokenRequest request) {
        // ① RT 파싱 및 서명/만료 검증
        Long memberId;
        try {
            memberId = jwtUtil.getMemberId(request.refreshToken());
        } catch (ExpiredJwtException e) {
            throw new CustomException(ErrorCode.TOKEN_EXPIRED);
        } catch (JwtException e) {
            throw new CustomException(ErrorCode.TOKEN_INVALID);
        }

        // ② 회원 조회
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        // ③ DB에 저장된 RT와 비교 (탈취된 토큰 또는 로그아웃된 경우 차단)
        if (!request.refreshToken().equals(member.getRefreshToken())) {
            throw new CustomException(ErrorCode.REFRESH_TOKEN_REVOKED);
        }

        // ④ ClubMember 테이블에서 Role 결정 후 새 AT/RT 발급
        String newAccessToken = jwtProvider.createAccessToken(member.getId());
        String newRefreshToken = jwtProvider.createRefreshToken(member.getId());
        member.updateRefreshToken(newRefreshToken); // 새 RT로 DB 갱신

        return new TokenResponse(newAccessToken, newRefreshToken);
    }

    @Transactional
    public void logout(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
        member.revokeRefreshToken();
    }
}