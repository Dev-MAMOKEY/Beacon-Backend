package com.mamoki.beacon.domain.club_member.service;

import com.mamoki.beacon.domain.club_member.dto.ClubMemberResponse;
import com.mamoki.beacon.domain.club_member.dto.RoleUpdateRequest;
import com.mamoki.beacon.domain.club_member.entity.ClubMember;
import com.mamoki.beacon.domain.club_member.entity.Role;
import com.mamoki.beacon.domain.attendance.service.AttendanceService;
import com.mamoki.beacon.domain.club_member.repository.ClubMemberRepository;
import com.mamoki.beacon.domain.member.repository.MemberRepository;
import com.mamoki.beacon.global.exception.CustomException;
import com.mamoki.beacon.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ClubMemberService {

    private final ClubMemberRepository clubMemberRepository;
    private final MemberRepository memberRepository;
    private final AttendanceService attendanceService;

    // 공통 검증 (동아리 멤버 조회, 역할 변경, 멤버 제명의 경우 관리자 기능)
    // 따라서 요청하는 사람이 멤버 정보가 있는지 동아리에 속해 있는지 관리자가 맞는지 체크함

    public void validateAdmin(Long requesterId, Long clubId) {

        // 1. 요청한 회원의 정보가 존재하는지
        memberRepository.findById(requesterId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        // 2. 요청한 회원이 동아리에 가입 되어 있는지
        ClubMember requester = clubMemberRepository.findByMemberIdAndClubId(requesterId, clubId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_CLUB_MEMBER));

        // 3. 요청 회원이 관리자인지
        if (requester.getRole() != Role.ADMIN) {
            throw new CustomException(ErrorCode.CLUB_ADMIN_REQUIRED);
        }
    }

    // 멤버 목록 조회 (이름 또는 학번으로 검색 가능)
    public List<ClubMemberResponse> getClubMembers(Long requesterId, Long clubId, String search) {
        validateAdmin(requesterId, clubId);

        String keyword = (search == null) ? null : search.trim();
        boolean hasKeyword = keyword != null && !keyword.isBlank();

        return clubMemberRepository.findByClubIdAndDeletedAtIsNull(clubId).stream()
                .filter(cm -> {
                    if (!hasKeyword) return true;
                    String name = cm.getMember().getName();
                    String stdId = String.valueOf(cm.getMember().getStdId());
                    return name.contains(keyword) || stdId.contains(keyword);
                })
                .map(cm -> new ClubMemberResponse(
                        cm.getMember().getId(),
                        cm.getMember().getName(),
                        cm.getMember().getStdId(),
                        cm.getRole(),
                        attendanceService.getAttendanceRate(
                                cm.getMember().getId(), clubId).rate()
                ))
                .toList();
    }

    // 역할 변경
    @Transactional
    public void updateRole(RoleUpdateRequest request) {
        validateAdmin(request.requesterId(), request.clubId());

        // 자기 자신 역할은 수정할 수 없음
        if (request.requesterId().equals(request.targetMemberId())) {
            throw new CustomException(ErrorCode.CANNOT_UPDATE_OWN_ROLE);
        }

        // 수정 대상이 해당 동아리에 가입되어 있는지 여부 확인
        ClubMember target = clubMemberRepository.findByMemberIdAndClubId(request.targetMemberId(), request.clubId())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_CLUB_MEMBER));

        target.updateRole(request.newRole());
    }

    // 멤버 제명
    @Transactional
    public void softdeletedMember(Long requesterId, Long clubId, Long targetMemberId) {
        validateAdmin(requesterId, clubId);

        ClubMember target = clubMemberRepository.findByMemberIdAndClubId(targetMemberId, clubId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_CLUB_MEMBER));

        target.softDelete();
    }
}
