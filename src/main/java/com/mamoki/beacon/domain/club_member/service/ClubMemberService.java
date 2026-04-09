package com.mamoki.beacon.domain.club_member.service;

import com.mamoki.beacon.domain.club_member.dto.RoleUpdateRequest;
import com.mamoki.beacon.domain.club_member.entity.ClubMember;
import com.mamoki.beacon.domain.club_member.entity.Role;
import com.mamoki.beacon.domain.club_member.repository.ClubMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ClubMemberService {

    private final ClubMemberRepository clubMemberRepository;

    // 회원 역할 변경 로직
    @Transactional
    public void updateMemberRole(RoleUpdateRequest request) {
        
    }
}
