package com.mamoki.beacon.domain.club.service;

import com.mamoki.beacon.domain.club.dto.ClubDto;
import com.mamoki.beacon.domain.club.dto.ClubResponseDto;
import com.mamoki.beacon.domain.club.entity.Club;
import com.mamoki.beacon.domain.club.repository.ClubRepository;
import com.mamoki.beacon.domain.club_member.entity.ClubMember;
import com.mamoki.beacon.domain.club_member.entity.Role;
import com.mamoki.beacon.domain.club_member.repository.ClubMemberRepository;
import com.mamoki.beacon.domain.member.entity.Member;
import com.mamoki.beacon.domain.member.repository.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ClubService {
    private final ClubRepository clubRepository;
    private final MemberRepository memberRepository;
    private final ClubMemberRepository clubMemberRepository;

    @Transactional //동아리 생성 함수
    public String createClub(Long memberId, ClubDto clubDto) {
        String fixedUuid = UUID.randomUUID().toString().replace("-", "");

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));

        Club club = Club.builder()
                .clubName(clubDto.clubName())
                .clubDescription(clubDto.clubDescription())
                .fixed_uuid(fixedUuid)
                .psk(UUID.randomUUID().toString().replace("-", ""))
                .build();
        clubRepository.save(club);

        ClubMember clubMember = new ClubMember(member, club, Role.ADMIN);
        clubMemberRepository.save(clubMember);

        return fixedUuid;
    }

    @Transactional
    public void updateClub(Long memberId, Long clubId, ClubDto clubDto) {

        ClubMember clubMember = clubMemberRepository.findByMemberIdAndClubId(memberId, clubId)
                .orElseThrow(() -> new IllegalArgumentException("해당 동아리의 멤버가 아닙니다."));
        if (clubMember.getRole() != Role.ADMIN) {
            throw new IllegalArgumentException("동아리 관리자만 수행할 수 있습니다.");
        }

        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new IllegalArgumentException("동아리가 존재하지 않습니다."));
        if(clubDto.clubName() != null) { //동아리명 변경 시
            club.setClubName(clubDto.clubName());
        }
        if(clubDto.clubDescription() != null) { //동아리설명 변경 시
            club.setClubDescription(clubDto.clubDescription());
        }
    }

    @Transactional //소프트 삭제
    public void softDeleteClub(Long memberId, Long clubId) {
        ClubMember clubMember = clubMemberRepository.findByMemberIdAndClubId(memberId, clubId)
                .orElseThrow(() -> new IllegalArgumentException("해당 동아리의 멤버가 아닙니다."));
        if (clubMember.getRole() != Role.ADMIN) {
            throw new IllegalArgumentException("동아리 관리자만 수행할 수 있습니다.");
        }
        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new IllegalArgumentException("동아리가 존재하지 않습니다."));
        club.setIsDeleted(true); //소프트 삭제 처리
        club.setDeletedAt(LocalDateTime.now()); //소프트 삭제 시간 기록
    }

    //동아리 상세 정보 조회
    public ClubResponseDto searchClub(Long clubId) {
        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new IllegalArgumentException("동아리가 존재하지 않습니다."));
        if (Boolean.TRUE.equals(club.getIsDeleted())){
            throw new IllegalArgumentException("삭제된 동아리입니다.");
        }
        return new ClubResponseDto(
                club.getId(),
                club.getClubName(),
                club.getClubDescription(),
                club.getCreatedAt()
        );
    }
}
