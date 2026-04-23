package com.mamoki.beacon.domain.attendance.service;

import com.mamoki.beacon.domain.attendance.dto.AttendanceDto;
import com.mamoki.beacon.domain.attendance.dto.AttendanceRateResponse;
import com.mamoki.beacon.domain.attendance.dto.MyAttendanceRecordDto;
import com.mamoki.beacon.domain.attendance.dto.TrendResponseDto;
import com.mamoki.beacon.domain.attendance.entity.Attendance;
import com.mamoki.beacon.domain.attendance.entity.AttendanceStatus;
import com.mamoki.beacon.domain.attendance.repository.AttendanceRepository;
import com.mamoki.beacon.domain.club_member.entity.ClubMember;
import com.mamoki.beacon.domain.club_member.entity.Role;
import com.mamoki.beacon.domain.club_member.repository.ClubMemberRepository;
import com.mamoki.beacon.domain.member.entity.Member;
import com.mamoki.beacon.domain.member.repository.MemberRepository;
import com.mamoki.beacon.domain.session.entity.Session;
import com.mamoki.beacon.domain.session.entity.SessionStatus;
import com.mamoki.beacon.domain.session.repository.SessionRepository;
import com.mamoki.beacon.global.exception.CustomException;
import com.mamoki.beacon.global.exception.ErrorCode;
import com.mamoki.beacon.global.fcm.service.FcmService;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AttendanceService {
    private final AttendanceRepository attendanceRepository;
    private final SessionRepository sessionRepository;
    private final MemberRepository memberRepository;
    private final ClubMemberRepository clubMemberRepository;
    private final PasswordEncoder passwordEncoder;
    private final FcmService fcmService;

    private static final long LATE_MINUTES = 5; //지각 시간

    //관리자 검증 함수
    private void validateAdmin(Long requesterId, Long clubId) {
        ClubMember requester = clubMemberRepository.findByMemberIdAndClubId(requesterId, clubId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_CLUB_MEMBER));
        if (requester.getRole() != Role.ADMIN) {
            throw new CustomException(ErrorCode.CLUB_ADMIN_REQUIRED);
        }
    }

    @Transactional //출석체크 함수
    public void checkAttendance(Long memberId, Long clubId, Long sessionId, String otpCode){
        clubMemberRepository.findByMemberIdAndClubId(memberId, clubId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_CLUB_MEMBER));

        Session session = sessionRepository.findByIdAndDeletedAtIsNull(sessionId)
                .orElseThrow(() -> new CustomException(ErrorCode.SESSION_NOT_FOUND));

        if (session.getSessionStatus() != SessionStatus.ACTIVE) { //세션 비활성화
            throw new CustomException(ErrorCode.SESSION_NOT_ACTIVE);
        }
        if (session.getOtpCode() == null || !passwordEncoder.matches(otpCode.toUpperCase(), session.getOtpCode())) { // otp값 없거나 틀릴때
            throw new CustomException(ErrorCode.INVALID_ATTENDANCE_CODE);
        }

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        if (attendanceRepository.existsByMemberAndSession(member, session)) { //출석 중복체크 하기 위한 throw
            throw new CustomException(ErrorCode.ALREADY_CHECKED_IN);
        }

        LocalDateTime now = LocalDateTime.now();

        AttendanceStatus status =
                now.isAfter(session.getStartAt().plusMinutes(LATE_MINUTES)) //시작시간 + 지각시간일때
                        ? AttendanceStatus.LATE //true면 지각
                        : AttendanceStatus.PRESENT;//false면

        attendanceRepository.save(Attendance.builder()
                .member(member)
                .session(session)
                .attendanceStatus(status)
                .isManual(false)
                .checkedAt(now)
                .createdAt(now)
                .updatedAt(now)
                .build());

        //출석한 사람한테만 출석완료되었다고 푸시알림
        if (Boolean.TRUE.equals(member.getPushEnabled())
                && member.getFcmToken() != null
                && !member.getFcmToken().isBlank()) {

            String statusText = switch (status) {
                case PRESENT -> "출석";
                case LATE -> "지각";
                default -> status.name();
            };

            //fcmservice 단일발송함수 호출
            fcmService.sendNotification(
                    "출석 완료",
                    "'" + session.getSessionName() + "' 출석이 완료되었습니다. (" + statusText + ")",
                    member.getFcmToken()
            );
        }
    }

    @Transactional //출석 종료함수 (종료 시 otp 널로 변경 후 출석안된 사용자 ABSENT로 변경)
    public void closeAttendance(Long sessionId) {
        Session session = sessionRepository.findByIdAndDeletedAtIsNull(sessionId)
                .orElseThrow(() -> new CustomException(ErrorCode.SESSION_NOT_FOUND));

        // 세션은 ACTIVED 유지, otp값만 null로 변경 (이유 : otp값이 남아있으면 출석요청 보낼 수 있으니까)
        session.setOtpCode(null);
        sessionRepository.save(session);

        //출석 ABSENT 처리하기 위해 동아리 인원들 전부 List로 뽑음
        List<ClubMember> clubMembers =
                clubMemberRepository.findByClubIdAndDeletedAtIsNull(session.getClub().getId());

        //출석한 애들은 여기에 저장
        Set<Long> attendedIds = attendanceRepository.findBySession(session).stream()
                .map(a -> a.getMember().getId())
                .collect(Collectors.toSet());

        LocalDateTime now = LocalDateTime.now();

        //출석하지 못한 애들은 ABSENT로 저장
        List<Attendance> absentList = clubMembers.stream()
                .filter(cm -> !attendedIds.contains(cm.getMember().getId()))
                .map(cm -> Attendance.builder()
                        .member(cm.getMember())
                        .session(session)
                        .attendanceStatus(AttendanceStatus.ABSENT)
                        .isManual(false)
                        .createdAt(now)
                        .updatedAt(now)
                        .build())
                .collect(Collectors.toList());

        attendanceRepository.saveAll(absentList);
    }

    @Transactional //관리자 수동 출석
    public void manualCheckAttendance(Long adminId, Long clubId, Long sessionId, Long targetMemberId, AttendanceStatus status, String note) {
        validateAdmin(adminId, clubId);

        if (status == AttendanceStatus.ABSENT) {
            throw new CustomException(ErrorCode.INVALID_MANUAL_STATUS);
        }

        Session session = sessionRepository.findByIdAndDeletedAtIsNull(sessionId)
                .orElseThrow(() -> new CustomException(ErrorCode.SESSION_NOT_FOUND));

        if (session.getSessionStatus() != SessionStatus.ACTIVE) {
            throw new CustomException(ErrorCode.SESSION_NOT_ACTIVE);
        }

        // 대상 멤버가 동아리 소속인지 확인
        clubMemberRepository.findByMemberIdAndClubId(targetMemberId, clubId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_CLUB_MEMBER));

        Member target = memberRepository.findById(targetMemberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        if (attendanceRepository.existsByMemberAndSession(target, session)) {
            throw new CustomException(ErrorCode.ALREADY_CHECKED_IN);
        }

        LocalDateTime now = LocalDateTime.now();
        attendanceRepository.save(Attendance.builder()
                .member(target)
                .session(session)
                .attendanceStatus(status)
                .isManual(true)
                .adminNote(note)
                .checkedAt(now)
                .createdAt(now)
                .updatedAt(now)
                .build());
    }

    @Transactional //관리자가 직접 출석 상태를 변경하는 함수
    public void updateStatus(Long adminId, Long clubId, Long recordId,
                             AttendanceStatus newStatus, String note) {
        validateAdmin(adminId, clubId);

        Attendance attendance = attendanceRepository.findById(recordId)
                .orElseThrow(() -> new CustomException(ErrorCode.ATTENDANCE_NOT_FOUND));

        attendance.updateStatus(newStatus, note);
    }

    public AttendanceRateResponse getAttendanceRate(Long memberId, Long clubId) {
        ClubMember clubMember = clubMemberRepository.findByMemberIdAndClubId(memberId, clubId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_CLUB_MEMBER));

        LocalDateTime joinedAt = clubMember.getJoinedAt();

        // 분모: 가입일 이후 열린 세션 수
        long totalSessions = sessionRepository
                .countByClubIdAndStartAtGreaterThanEqualAndDeletedAtIsNull(clubId, joinedAt);

        // 분자: PRESENT + LATE + ETC
        long attendedCount = attendanceRepository.countAttendedByMemberAndClub(
                memberId, clubId, joinedAt,
                List.of(AttendanceStatus.PRESENT, AttendanceStatus.LATE, AttendanceStatus.ETC)
        );

        double rate = (totalSessions == 0)
                ? 0.0
                : (double) attendedCount / totalSessions * 100.0;

        return new AttendanceRateResponse(memberId, clubId, totalSessions, attendedCount, rate);
    }

    //출석 조회 함수
    public Slice<AttendanceDto> getSessionAttendance(
            Long adminId, Long clubId, Long sessionId, Pageable pageable) {

        validateAdmin(adminId, clubId);

        Session session = sessionRepository.findByIdAndDeletedAtIsNull(sessionId)
                .orElseThrow(() -> new CustomException(ErrorCode.SESSION_NOT_FOUND));

        return attendanceRepository.findBySession(session, pageable)
                .map(a -> new AttendanceDto(
                        a.getAttendanceId(),
                        a.getMember().getId(),
                        a.getMember().getName(),
                        a.getMember().getStdId(),
                        a.getAttendanceStatus(),
                        a.getCheckedAt(),
                        a.getIsManual(),
                        a.getAdminNote()
                ));
    }

    //월별 출석 기록 조회 함수
    @Transactional(readOnly = true)
    public MyAttendanceRecordDto getMyAttendanceRecord(Long memberId, Long clubId, int year, int month) {
        clubMemberRepository.findByMemberIdAndClubId(memberId, clubId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_CLUB_MEMBER));

        //리스트에 저장
        List<Attendance> records = attendanceRepository.findMonthlyRecordsByMemberAndClub(
                memberId, clubId, year, month);

        //세션별 정보들을 리스트에 저장
        List<MyAttendanceRecordDto.AttendanceRecordItem> items = records.stream()
                .map(a -> new MyAttendanceRecordDto.AttendanceRecordItem(
                        a.getSession().getId(),
                        a.getSession().getSessionName(),
                        a.getSession().getStartAt().toLocalDate(),
                        a.getAttendanceStatus(),
                        a.getCheckedAt(),
                        a.getAdminNote()
                ))
                .toList();

        //status별로 숫자 체크를 하기위한 Map 생성
        Map<AttendanceStatus, Long> countMap = records.stream()
                .collect(Collectors.groupingBy(Attendance::getAttendanceStatus, Collectors.counting()));

        //Map에 있던 값들을(출석값들) DTO에 넘김
        MyAttendanceRecordDto.StatusSummary summary = new MyAttendanceRecordDto.StatusSummary(
                countMap.getOrDefault(AttendanceStatus.PRESENT, 0L),
                countMap.getOrDefault(AttendanceStatus.LATE, 0L),
                countMap.getOrDefault(AttendanceStatus.ABSENT, 0L),
                countMap.getOrDefault(AttendanceStatus.ETC, 0L)
        );

        //출석 조회함수에서 rate만 꺼내서 dto에 넣음
        AttendanceRateResponse response = getAttendanceRate(memberId, clubId);
        return new MyAttendanceRecordDto(year, month, items, summary, response.rate());
    }

    @Transactional(readOnly = true)
    public TrendResponseDto getTrend(Long adminId, Long clubId, LocalDate startDate, LocalDate endDate) {
        validateAdmin(adminId, clubId); //운영진 검증

        LocalDateTime startAt = startDate.atStartOfDay(); //월초
        LocalDateTime endAt = endDate.atTime(23, 59, 59); //월말

        List<Object[]> rows = attendanceRepository.countBySessionAndStatus(clubId, startAt, endAt);

        // 세션별로 매핑
        Map<Long, TrendResponseDto.TrendItem> sessionMap = new LinkedHashMap<>();


        for (Object[] row : rows) {
            //컬럼값들을 변수에 다 담음
            Long sessionId = ((Number) row[0]).longValue();
            String sessionName = (String) row[1];
            LocalDate date = ((LocalDateTime) row[2]).toLocalDate();
            AttendanceStatus status = (AttendanceStatus) row[3];
            long count = ((Number) row[4]).longValue();

            //출석률 계산
            sessionMap.compute(sessionId, (id, existing) -> {
                long prevTotal = existing != null ? existing.total() : 0L;
                long prevAttended = existing != null ? existing.attended() : 0L;

                boolean isAttended = status == AttendanceStatus.PRESENT
                        || status == AttendanceStatus.LATE
                        || status == AttendanceStatus.ETC;

                long newTotal = prevTotal + count;
                long newAttended = prevAttended + (isAttended ? count : 0L);
                double rate = newTotal == 0 ? 0.0 : (double) newAttended / newTotal * 100.0;

                return new TrendResponseDto.TrendItem(sessionId, sessionName, date, newTotal, newAttended, rate);
            });
        }

        return new TrendResponseDto(new ArrayList<>(sessionMap.values()));
    }
}
