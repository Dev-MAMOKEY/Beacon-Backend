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
        sessionRepository.save(session);
    }
}
