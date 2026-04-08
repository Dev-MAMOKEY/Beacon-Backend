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
import com.mamoki.beacon.global.util.TotpUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InviteService {
    private final InviteRepository inviteRepository;
    private final MemberRepository memberRepository;
    private final ClubRepository clubRepository;
    private final ClubMemberRepository clubMemberRepository;

    //초대코드 생성 함수
    public String requestInviteCode(Long memberId, Long clubId) {
        ClubMember clubMember = clubMemberRepository.findByMemberIdAndClubId(memberId, clubId)
                .orElseThrow(() -> new IllegalArgumentException("해당 회원이 클럽의 멤버가 아닙니다."));

        if (clubMember.getRole() != Role.ADMIN) { //어드민 아니면 에러
            throw new CustomException(ErrorCode.FORBIDDEN);
        }

        //
        Club club = clubRepository.findById(clubId).orElseThrow(() -> new IllegalArgumentException("해당 클럽이 존재하지 않습니다."));
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new IllegalArgumentException("해당 회원이 존재하지 않습니다."));

        //이미 db에 있는데 중복저장되는거 방지하기 위해 오늘 생성된 초대코드가 있는지 확인
        LocalDateTime today = LocalDate.now(ZoneId.of("Asia/Seoul")).atStartOfDay();
        Optional<Invite> existing = inviteRepository.findByClubAndCreatedAt(club, today);
        if (existing.isPresent()) {
            return existing.get().getInviteCode();
        }

        String code;

        //클럽 psk로 totp 생성
        try{
            code = TotpUtil.generateTotp(club.getPsk());
        }catch (Exception e){
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }

        Invite invite = new Invite(member, club, code);
        inviteRepository.save(invite);

        return code;
    }

    //초대코드로 가입하는 함수
    @Transactional
    public void responseInviteCode(Long memberId, Long clubId, String inviteCode) {
        Club club = clubRepository.findById(clubId).orElseThrow(() -> new IllegalArgumentException("해당 클럽이 존재하지 않습니다."));

        //가입된 멤버인지 확인
        clubMemberRepository.findByMemberIdAndClubId(memberId, clubId).ifPresent(cm -> {
            throw new CustomException(ErrorCode.ALREADY_CLUB_MEMBER);
        });

        //초대 인증확인을 위한 변수
        boolean isValid;
        try {
            isValid = TotpUtil.verifyTotp(club.getPsk(), inviteCode);
        } catch (Exception e) { //값이 들어오지 않으면 500
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }

        if (!isValid) { //값이 다르면 INVALID INVITE CODE 던짐
            throw new CustomException(ErrorCode.INVALID_INVITE_CODE);
        }

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        ClubMember newMember = new ClubMember(member, club, Role.MEMBER);
        clubMemberRepository.save(newMember);
    }
}
