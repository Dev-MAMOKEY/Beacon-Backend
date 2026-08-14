package com.mamoki.beacon.domain.invite.service;

import com.mamoki.beacon.domain.club.entity.Club;
import com.mamoki.beacon.domain.club.repository.ClubRepository;
import com.mamoki.beacon.domain.club_member.entity.ClubMember;
import com.mamoki.beacon.domain.club_member.entity.Role;
import com.mamoki.beacon.domain.club_member.repository.ClubMemberRepository;
import com.mamoki.beacon.domain.invite.entity.Invite;
import com.mamoki.beacon.domain.invite.repository.InviteRepository;
import com.mamoki.beacon.domain.member.entity.Member;
import com.mamoki.beacon.domain.member.repository.MemberRepository;
import com.mamoki.beacon.global.exception.CustomException;
import com.mamoki.beacon.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;

@Service
@RequiredArgsConstructor
public class InviteService {
    private final InviteRepository inviteRepository;
    private final MemberRepository memberRepository;
    private final ClubRepository clubRepository;
    private final ClubMemberRepository clubMemberRepository;

    private static final String CODE_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int CODE_LENGTH = 4;
    private static final SecureRandom RANDOM = new SecureRandom();

    //관리자 검증 함수
    private void validateAdmin(Long memberId, Long clubId) {
        ClubMember cm = clubMemberRepository.findByMemberIdAndClubId(memberId, clubId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_CLUB_MEMBER));
        if (cm.getRole() != Role.ADMIN) {
            throw new CustomException(ErrorCode.CLUB_ADMIN_REQUIRED);
        }
    }

    //4자리 랜덤 영숫자 생성
    private String generateRandomCode() {
        StringBuilder sb = new StringBuilder(CODE_LENGTH);
        for (int i = 0; i < CODE_LENGTH; i++) {
            sb.append(CODE_CHARS.charAt(RANDOM.nextInt(CODE_CHARS.length())));
        }
        return sb.toString();
    }

    //초대코드 발급 (기존 유효 코드 자동 무효화 후 새 코드 발급)
    @Transactional
    public String requestInviteCode(Long memberId, Long clubId) {
        validateAdmin(memberId, clubId);

        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new CustomException(ErrorCode.CLUB_NOT_FOUND));
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        //기존 유효 코드 있으면 무효화
        inviteRepository.findByClubAndRevokedAtIsNull(club)
                .ifPresent(Invite::revoke);

        //새 코드 생성 및 저장
        String code = generateRandomCode();
        Invite invite = new Invite(member, club, code);
        inviteRepository.save(invite);

        return code;
    }

    //초대코드로 가입
    @Transactional
    public void responseInviteCode(Long memberId, Long clubId, String inviteCode) {
        if (clubId == null) {
            throw new CustomException(ErrorCode.INVALID_INVITE_CODE);
        }

        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new CustomException(ErrorCode.CLUB_NOT_FOUND));

        //이미 가입된 멤버인지 확인
        clubMemberRepository.findByMemberIdAndClubId(memberId, clubId).ifPresent(cm -> {
            throw new CustomException(ErrorCode.ALREADY_CLUB_MEMBER);
        });

        //코드 DB 조회 + 무효화 여부 검증
        Invite invite = inviteRepository.findByInviteCodeAndClub(inviteCode, club)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_INVITE_CODE));
        if (invite.getRevokedAt() != null) {
            throw new CustomException(ErrorCode.INVALID_INVITE_CODE);
        }

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        ClubMember newMember = new ClubMember(member, club, Role.MEMBER);
        clubMemberRepository.save(newMember);
    }

    //현재 유효한 초대코드 조회 (관리자)
    @Transactional(readOnly = true)
    public String getCurrentInviteCode(Long memberId, Long clubId) {
        validateAdmin(memberId, clubId);

        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new CustomException(ErrorCode.CLUB_NOT_FOUND));

        Invite invite = inviteRepository.findByClubAndRevokedAtIsNull(club)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_INVITE_CODE));

        return invite.getInviteCode();
    }

    //초대코드 수동 무효화 (관리자)
    @Transactional
    public void revokeInviteCode(Long memberId, Long clubId) {
        validateAdmin(memberId, clubId);

        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new CustomException(ErrorCode.CLUB_NOT_FOUND));

        Invite invite = inviteRepository.findByClubAndRevokedAtIsNull(club)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_INVITE_CODE));
        invite.revoke();
    }
}
