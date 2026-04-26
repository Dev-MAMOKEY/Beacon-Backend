package com.mamoki.beacon.domain.member.service;

import com.mamoki.beacon.domain.member.dto.fcm.FcmTokenUpdateRequest;
import com.mamoki.beacon.domain.member.dto.password.MemberPaswordUpdateRequest;
import com.mamoki.beacon.domain.member.dto.profile.MemberProfileResponse;
import com.mamoki.beacon.domain.member.dto.profile.MemberProfileUpdateRequest;
import com.mamoki.beacon.domain.member.entity.Member;
import com.mamoki.beacon.domain.member.repository.MemberRepository;
import com.mamoki.beacon.global.exception.CustomException;
import com.mamoki.beacon.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public MemberProfileResponse getMyInfo(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
        return member.toMemberProfileResponse();
    }

    @Transactional
    public void updatePassword(Long memberId, MemberPaswordUpdateRequest request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        if (!passwordEncoder.matches(request.currentPassword(), member.getPassword())) {
            throw new CustomException(ErrorCode.INVALID_CREDENTIALS);
        }

        if (!request.newPassword().equals(request.confirmNewPassword())) {
            throw new CustomException(ErrorCode.PASSWORD_CONFIRMATION_MISMATCH);
        }
        member.updatePassword(passwordEncoder.encode(request.newPassword()));
    }

    @Transactional
    public MemberProfileResponse updateProfile(Long memberId, MemberProfileUpdateRequest request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        member.updateProfile(request.name(), request.pushEnabled());

        return member.toMemberProfileResponse();
    }

    @Transactional
    public void updateFcmToken(Long memberId, FcmTokenUpdateRequest request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        member.updateFcmToken(request.fcmToken());
    }
}
