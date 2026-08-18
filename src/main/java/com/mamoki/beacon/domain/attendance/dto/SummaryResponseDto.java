package com.mamoki.beacon.domain.attendance.dto;

//대시보드 상단 요약 카드용 응답 (프론트 요구 필드명 그대로)
public record SummaryResponseDto(
        long todayPresent,   //오늘 출석 수
        long todayLate,      //오늘 지각 수
        long totalMembers,   //전체 멤버 수
        double avgRate       //멤버 평균 출석률 (%)
) {
}
