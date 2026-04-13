package com.mamoki.beacon.domain.session.service;

import com.mamoki.beacon.domain.attendance.entity.Attendance;
import com.mamoki.beacon.domain.attendance.repository.AttendanceRepository;
import com.mamoki.beacon.domain.club.entity.Club;
import com.mamoki.beacon.domain.club.repository.ClubRepository;
import com.mamoki.beacon.domain.club_member.entity.ClubMember;
import com.mamoki.beacon.domain.club_member.repository.ClubMemberRepository;
import com.mamoki.beacon.domain.member.entity.Member;
import com.mamoki.beacon.domain.member.repository.MemberRepository;
import com.mamoki.beacon.domain.session.dto.SessionDto;
import com.mamoki.beacon.domain.session.entity.Session;
import com.mamoki.beacon.domain.session.entity.SessionStatus;
import com.mamoki.beacon.domain.session.repository.SessionRepository;
import com.mamoki.beacon.global.exception.CustomException;
import com.mamoki.beacon.global.exception.ErrorCode;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
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

    @Transactional
    public String createSession(Long memberId, SessionDto sessionDto){
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
                .startAt(sessionDto.getStartAt())
                .endAt(sessionDto.getEndAt())
                .member(member)
                .club(club)
                .build();
        sessionRepository.save(session);
        return sessionUuid;
    }

    //softDelete 시간 업데이트
    @Transactional
    public void softDeletedSession(Long sessionId){
        Session session = sessionRepository.findById(sessionId).orElseThrow(()-> new CustomException(ErrorCode.SESSION_NOT_FOUND));
        if(session.getSessionStatus() == SessionStatus.ACTIVED){
            throw new CustomException(ErrorCode.NOT_DELETED_SESSION);
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
    public void updatedSession(Long sessionId, SessionDto sessionDto){
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(()-> new CustomException(ErrorCode.SESSION_NOT_FOUND));
        if (session.getSessionStatus() == SessionStatus.ENDED) {
            throw new CustomException(ErrorCode.SESSION_ALREADY_ENDED);
        }

        if(sessionDto.getSessionName() != null){
            session.setSessionName(sessionDto.getSessionName());
        }
        if(sessionDto.getStartAt() != null){
            session.setStartAt(sessionDto.getStartAt());
        }
        if(sessionDto.getEndAt() != null){
            session.setEndAt(sessionDto.getEndAt());
        }
        if(sessionDto.getSessionStatus() != null){
            session.setSessionStatus(sessionDto.getSessionStatus());
        }

        sessionRepository.save(session);
    }

    //session시작하고 ACTIVED로 변경하기 위한 세션시작 함수
    @Transactional
    public void startedSession(Long sessionId){
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(()-> new CustomException(ErrorCode.SESSION_NOT_FOUND));
        session.setSessionStatus(SessionStatus.ACTIVED);
        sessionRepository.save(session);
    }

    //session 종료하기 위한 함수
    @Transactional
    public void endedSession(Long sessionId){
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(()-> new CustomException(ErrorCode.SESSION_NOT_FOUND));
        session.setSessionStatus(SessionStatus.ENDED);
        session.setEndAt(LocalDateTime.now());

        // 출석 기록 없는 멤버 ABSENT 자동 생성
        List<ClubMember> clubMembers = clubMemberRepository.findByClubId(session.getClub().getId());
        Set<Long> attendedMemberIds = attendanceRepository.findBySession(session)
                .stream()
                .map(a -> a.getMember().getId())
                .collect(Collectors.toSet());

        sessionRepository.save(session);
    }

    //session List 조회
    public Slice<Session> getSessionsByClub(Long clubId, Pageable pageable) {
        return sessionRepository.findByClubId(clubId, pageable);
    }

    //TOTP 인증 코드 세션 테이블에 해시값으로 저장?
}
