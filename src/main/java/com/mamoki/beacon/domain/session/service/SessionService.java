package com.mamoki.beacon.domain.session.service;

import com.mamoki.beacon.domain.attendance.entity.Attendance;
import com.mamoki.beacon.domain.attendance.entity.AttendanceStatus;
import com.mamoki.beacon.domain.attendance.repository.AttendanceRepository;
import com.mamoki.beacon.domain.club.entity.Club;
import com.mamoki.beacon.domain.club.repository.ClubRepository;
import com.mamoki.beacon.domain.club_member.entity.ClubMember;
import com.mamoki.beacon.domain.club_member.entity.Role;
import com.mamoki.beacon.domain.club_member.repository.ClubMemberRepository;
import com.mamoki.beacon.domain.member.entity.Member;
import com.mamoki.beacon.domain.member.repository.MemberRepository;
import com.mamoki.beacon.domain.session.dto.SessionDto;
import com.mamoki.beacon.domain.session.dto.SessionStartDto;
import com.mamoki.beacon.domain.session.entity.Session;
import com.mamoki.beacon.domain.session.entity.SessionStatus;
import com.mamoki.beacon.domain.session.repository.SessionRepository;
import com.mamoki.beacon.global.exception.CustomException;
import com.mamoki.beacon.global.exception.ErrorCode;
import com.mamoki.beacon.global.util.TotpUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SessionService {
    private final SessionRepository sessionRepository;
    private final ClubRepository clubRepository;
    private final MemberRepository memberRepository;
    private final AttendanceRepository attendanceRepository;
    private final ClubMemberRepository clubMemberRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public String createSession(Long memberId, SessionDto sessionDto) {

        ClubMember clubMember = clubMemberRepository.findByMemberIdAndClubId(memberId, sessionDto.getClubId())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_CLUB_MEMBER));
        if (clubMember.getRole() != Role.ADMIN) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }

        if (sessionRepository.existsByClubIdAndSessionStatusAndDeletedAtIsNull(
                sessionDto.getClubId(), SessionStatus.ACTIVED)) {
            throw new CustomException(ErrorCode.SESSION_ALREADY_ACTIVE);
        }
        Club club = clubRepository.findById(sessionDto.getClubId())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_CLUB));
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        //세션 uuid생성
        String sessionUuid = UUID.randomUUID().toString().replace("-", "");
        Session session = Session.builder()
                .sessionStatus(SessionStatus.SCHEDULED)
                .sessionName(sessionDto.getSessionName())
                .uuid(sessionUuid)
                .exceptStartAt(sessionDto.getExceptStartAt())
                .exceptEndAt(sessionDto.getExceptEndAt())
                .member(member)
                .club(club)
                .build();
        sessionRepository.save(session);
        return sessionUuid;
    }

    //softDelete 시간 업데이트
    @Transactional
    public void softDeletedSession(Long memberId, Long sessionId) {

        Session session = sessionRepository.findById(sessionId).orElseThrow(() -> new CustomException(ErrorCode.SESSION_NOT_FOUND));
        if (session.getSessionStatus() == SessionStatus.ACTIVED) {
            throw new CustomException(ErrorCode.NOT_DELETED_SESSION);
        }

        ClubMember clubMember = clubMemberRepository.findByMemberIdAndClubId(memberId, session.getClub().getId())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_CLUB_MEMBER));
        if (clubMember.getRole() != Role.ADMIN) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }

        // 연결된 attendance 소프트 삭제
        List<Attendance> attendances = attendanceRepository.findBySession(session);
        attendances.forEach(a -> a.setDeletedAt(LocalDateTime.now()));
        attendanceRepository.saveAll(attendances);

        session.setDeletedAt(LocalDateTime.now());
        sessionRepository.save(session);
    }

    //session 업데이트 (입력 안한건 업데이트 안함), 주 목적은 세션 상태를 위한 함수
    @Transactional
    public void updatedSession(Long memberId, Long sessionId, SessionDto sessionDto) {

        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new CustomException(ErrorCode.SESSION_NOT_FOUND));

        ClubMember clubMember = clubMemberRepository.findByMemberIdAndClubId(memberId, session.getClub().getId())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_CLUB_MEMBER));
        if (clubMember.getRole() != Role.ADMIN) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }

        if (session.getSessionStatus() == SessionStatus.ENDED) {
            throw new CustomException(ErrorCode.SESSION_ALREADY_ENDED);
        }
        if (sessionDto.getSessionName() != null) {
            session.setSessionName(sessionDto.getSessionName());
        }
        if (sessionDto.getExceptStartAt() != null) {
            session.setExceptStartAt(sessionDto.getExceptStartAt());
        }
        if (sessionDto.getExceptEndAt() != null) {
            session.setExceptEndAt(sessionDto.getExceptEndAt());
        }

        sessionRepository.save(session);
    }

    //session시작하고 ACTIVED로 변경하기 위한 세션시작 함수
    @Transactional
    public SessionStartDto startedSession(Long memberId, Long sessionId) {
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new CustomException(ErrorCode.SESSION_NOT_FOUND));

        ClubMember clubMember = clubMemberRepository.findByMemberIdAndClubId(memberId, session.getClub().getId())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_CLUB_MEMBER));
        if (clubMember.getRole() != Role.ADMIN) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }

        if (session.getSessionStatus() != SessionStatus.SCHEDULED) {
            throw new CustomException(ErrorCode.SESSION_ALREADY_ACTIVE);
        }

        String otpCode;
        try {
            otpCode = TotpUtil.generateSessionOtp(session.getUuid());
            session.setOtpCode(passwordEncoder.encode(otpCode));
        } catch (Exception e) {
            throw new CustomException(ErrorCode.OTP_GENERATION_FAILED);
        }

        session.setSessionStatus(SessionStatus.ACTIVED);
        session.setStartAt(LocalDateTime.now());
        sessionRepository.save(session);

        return new SessionStartDto(otpCode, session.getUuid());
    }

    //session 종료하기 위한 함수
    @Transactional
    public void endedSession(Long memberId, Long sessionId){
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(()-> new CustomException(ErrorCode.SESSION_NOT_FOUND));

        ClubMember clubMember = clubMemberRepository.findByMemberIdAndClubId(memberId, session.getClub().getId())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_CLUB_MEMBER));
        if (clubMember.getRole() != Role.ADMIN) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }

        if (session.getSessionStatus() != SessionStatus.ACTIVED) { //세션 활성상태만 가능
            throw new CustomException(ErrorCode.SESSION_NOT_ACTIVE);
        }

        session.setSessionStatus(SessionStatus.ENDED);
        session.setOtpCode(null); //otp 널값으로 변경
        session.setEndAt(LocalDateTime.now());

        // 출석 기록 없는 멤버 ABSENT 자동 생성
        List<ClubMember> clubMembers = clubMemberRepository.findByClubId(session.getClub().getId());

        //set을 쓰는 이유 Set은 해시값이라 이미 위치가 지정되어있어서 순차적으로 확인하지않고 바로 접근가능 List -> 배열식으로 되어있어서 순차적으로 접근 (결론: 빠름)
        Set<Long> attendedMemberIds = attendanceRepository.findBySession(session)
                .stream()
                .map(a -> a.getMember().getId()) //Attendance 객체에서 memberId값 추출하고
                .collect(Collectors.toSet()); //set으로 정렬

        List<Attendance> absentList = clubMembers.stream()
                .filter(cm -> !attendedMemberIds.contains(cm.getMember().getId())) //결석만 필터
                .map(cm -> Attendance.builder() //결석한 멤버 뽑아서 ABSENT
                        .member(cm.getMember())
                        .session(session)
                        .attendanceStatus(AttendanceStatus.ABSENT)
                        .isManual(false)
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build())
                .collect(Collectors.toList());

        attendanceRepository.saveAll(absentList);
        sessionRepository.save(session);
    }

    //session List 조회
    public Slice<Session> getSessionsByClub(Long clubId, SessionStatus status, Pageable pageable) {

        if (status != null) {
            return sessionRepository.findByClubIdAndSessionStatusAndDeletedAtIsNull(clubId, status, pageable);
        }
        return sessionRepository.findByClubIdAndDeletedAtIsNull(clubId, pageable);
    }

}
