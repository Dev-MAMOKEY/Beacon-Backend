package com.mamoki.beacon.domain.session.service;

import com.mamoki.beacon.domain.club.entity.Club;
import com.mamoki.beacon.domain.club.repository.ClubRepository;
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
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SessionService {
    private final SessionRepository sessionRepository;
    private final ClubRepository clubRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public String createSession(Long memberId, SessionDto sessionDto){
        //세션 uuid생성
        String sessionUuid = UUID.randomUUID().toString().replace("-", "");
        //어떤 동아리에서 세션 생성하는지 확인
        Club club = clubRepository.findById(sessionDto.getClubId()).orElseThrow(()-> new CustomException(ErrorCode.NOT_FOUND_CLUB));
        //멤버 존재하는지 확인
        Member member = memberRepository.findById(memberId).orElseThrow(()-> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
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
        session.setDeletedAt(LocalDateTime.now());
        sessionRepository.save(session);
    }

    //session 업데이트 (입력 안한건 업데이트 안함), 주 목적은 세션 상태를 위한 함수
    @Transactional
    public void updatedSession(Long sessionId, SessionDto sessionDto){
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(()-> new CustomException(ErrorCode.SESSION_NOT_FOUND));

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
        sessionRepository.save(session);
    }

    //session List 조회
    public Slice<Session> getSessionsByClub(Long clubId, Pageable pageable) {
        return sessionRepository.findByClubId(clubId, pageable);
    }

    //TOTP 인증 코드 세션 테이블에 해시값으로 저장?
}
