package com.mamoki.beacon.domain.session.service;

import com.mamoki.beacon.domain.club.entity.Club;
import com.mamoki.beacon.domain.club.repository.ClubRepository;
import com.mamoki.beacon.domain.session.dto.SessionDto;
import com.mamoki.beacon.domain.session.entity.Session;
import com.mamoki.beacon.domain.session.entity.SessionStatus;
import com.mamoki.beacon.domain.session.repository.SessionRepository;
import com.mamoki.beacon.global.exception.CustomException;
import com.mamoki.beacon.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SessionService {
    private final SessionRepository sessionRepository;
    private final ClubRepository clubRepository;


    public void createSession(SessionDto sessionDto){
        //세션 uuid생성
        String sessionUuid = UUID.randomUUID().toString().replace("-", "");
        //어떤 동아리에서 세션 생성하는지 확인
        Club club = clubRepository.findById(sessionDto.getClubId()).orElseThrow(()-> new CustomException(ErrorCode.NOT_FOUND_CLUB));
        Session session = Session.builder()
                .sessionStatus(SessionStatus.SCHEDULED)
                .sessionName(sessionDto.getSessionName())
                .uuid(sessionUuid)
                .startAt(sessionDto.getStartAt())
                .endAt(sessionDto.getEndAt())
                .club(club)
                .build();
        //세션이 ACTIVED이면 에러던짐
        if(session.getSessionStatus() == SessionStatus.ACTIVED){
            throw new CustomException(ErrorCode.SESSION_ALREADY_ACTIVE);
        }
        sessionRepository.save(session);
    }

    //softDelete 시간 업데이트
    public void softDeletedSession(Long sessionId){
        Session session = sessionRepository.findById(sessionId).orElseThrow(()-> new CustomException(ErrorCode.SESSION_NOT_FOUND));
        session.setDeletedAt(LocalDateTime.now());
        sessionRepository.save(session);
    }

    //session 업데이트 (입력 안한건 업데이트 안함), 주 목적은 세션 상태를 위한 함수
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
    public void startedSession(Long sessionId){
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(()-> new CustomException(ErrorCode.SESSION_NOT_FOUND));
        session.setSessionStatus(SessionStatus.ACTIVED);
        sessionRepository.save(session);
    }

    //TOTP 인증 코드 세션 테이블에 해시값으로 저장?
}
