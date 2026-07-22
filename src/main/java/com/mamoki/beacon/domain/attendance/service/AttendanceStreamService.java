package com.mamoki.beacon.domain.attendance.service;

import com.mamoki.beacon.domain.attendance.dto.AttendanceFeedDto;
import com.mamoki.beacon.domain.club_member.entity.ClubMember;
import com.mamoki.beacon.domain.club_member.entity.Role;
import com.mamoki.beacon.domain.club_member.repository.ClubMemberRepository;
import com.mamoki.beacon.global.exception.CustomException;
import com.mamoki.beacon.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
@Service
@RequiredArgsConstructor
public class AttendanceStreamService {
    private final ClubMemberRepository clubMemberRepository;

    private static final long TIMEOUT_MILLIS = 30L * 60 * 1000; //연결 유지 30분 (만료되면 프론트가 재연결)

    //clubId -> 그 동아리 대시보드를 보고 있는 브라우저 연결 목록
    private final Map<Long, List<SseEmitter>> emitters = new ConcurrentHashMap<>();

    //SSE 구독 (대시보드 진입 시 호출, ADMIN만)
    public SseEmitter subscribe(Long memberId, Long clubId) {
        ClubMember clubMember = clubMemberRepository.findByMemberIdAndClubId(memberId, clubId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_CLUB_MEMBER));
        if (clubMember.getRole() != Role.ADMIN) {
            throw new CustomException(ErrorCode.CLUB_ADMIN_REQUIRED);
        }

        SseEmitter emitter = new SseEmitter(TIMEOUT_MILLIS);
        emitters.computeIfAbsent(clubId, key -> new CopyOnWriteArrayList<>()).add(emitter);

        //연결이 끝나면(정상 종료/타임아웃/에러) 목록에서 제거 → 죽은 연결이 쌓이는 것 방지
        emitter.onCompletion(() -> remove(clubId, emitter));
        emitter.onTimeout(() -> remove(clubId, emitter));
        emitter.onError(e -> remove(clubId, emitter));

        //구독 직후 확인용 이벤트 1건 전송 (프록시가 빈 응답을 버퍼링하는 것도 방지)
        try {
            emitter.send(SseEmitter.event().name("connect").data("connected"));
        } catch (IOException e) {
            remove(clubId, emitter);
        }
        return emitter;
    }

    //출석 발생 시 해당 동아리 구독자 전원에게 이벤트 발송 (AttendanceService에서 호출)
    public void publish(Long clubId, AttendanceFeedDto feed) {
        List<SseEmitter> clubEmitters = emitters.get(clubId);
        if (clubEmitters == null || clubEmitters.isEmpty()) {
            return; //보고 있는 사람이 없으면 아무것도 안 함
        }
        for (SseEmitter emitter : clubEmitters) {
            try {
                emitter.send(SseEmitter.event().name("attendance").data(feed));
            } catch (IOException e) {
                remove(clubId, emitter); //전송 실패 = 끊긴 연결이므로 제거
            }
        }
    }

    //30초마다 하트비트 전송 (중간 프록시가 조용한 연결을 끊는 것 방지)
    @Scheduled(fixedRate = 30_000)
    public void heartbeat() {
        emitters.forEach((clubId, clubEmitters) -> {
            for (SseEmitter emitter : clubEmitters) {
                try {
                    emitter.send(SseEmitter.event().comment("heartbeat"));
                } catch (IOException e) {
                    remove(clubId, emitter);
                }
            }
        });
    }

    private void remove(Long clubId, SseEmitter emitter) {
        List<SseEmitter> clubEmitters = emitters.get(clubId);
        if (clubEmitters != null) {
            clubEmitters.remove(emitter);
        }
    }
}
