package com.mamoki.beacon.domain.beacon_config.service;

import com.mamoki.beacon.domain.beacon_config.dto.BeaconConfigDto;
import com.mamoki.beacon.domain.beacon_config.entity.BeaconConfig;
import com.mamoki.beacon.domain.beacon_config.repository.BeaconConfigRepository;
import com.mamoki.beacon.domain.club.entity.Club;
import com.mamoki.beacon.domain.club.repository.ClubRepository;
import com.mamoki.beacon.domain.club_member.entity.ClubMember;
import com.mamoki.beacon.domain.club_member.entity.Role;
import com.mamoki.beacon.domain.club_member.repository.ClubMemberRepository;
import com.mamoki.beacon.global.exception.CustomException;
import com.mamoki.beacon.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BeaconConfigService {
    private final BeaconConfigRepository beaconConfigRepository;
    private final ClubRepository clubRepository;
    private final ClubMemberRepository clubMemberRepository;

    private static final int DEFAULT_RSSI_THRESHOLD = -70;
    private static final int DEFAULT_LATE_THRESHOLD_MINUTES = 10;
    private static final int DEFAULT_RSSI_STABILIZATION_SECONDS = 3;

    @Transactional //조회 (멤버 누구나, 없으면 기본값으로 생성)
    public BeaconConfigDto getConfig(Long memberId, Long clubId) {
        clubMemberRepository.findByMemberIdAndClubId(memberId, clubId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_CLUB_MEMBER));
        return toDto(getOrCreate(clubId));
    }

    @Transactional //수정 (ADMIN만)
    public BeaconConfigDto updateConfig(Long memberId, Long clubId, BeaconConfigDto dto) {
        ClubMember clubMember = clubMemberRepository.findByMemberIdAndClubId(memberId, clubId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_CLUB_MEMBER));
        if (clubMember.getRole() != Role.ADMIN) {
            throw new CustomException(ErrorCode.CLUB_ADMIN_REQUIRED);
        }
        BeaconConfig config = getOrCreate(clubId);
        config.setBeaconUuid(dto.uuid());
        config.setLateThresholdMinutes(dto.lateThresholdMinutes());
        config.setRssiStabilizationSeconds(dto.rssiStabilizationSeconds());
        config.setRssiThreshold(dto.rssiThreshold()); //@Transactional이라 dirty checking으로 저장
        return toDto(config);
    }

    //설정이 없으면 기본값으로 생성 → 기존 동아리도 마이그레이션 없이 첫 조회 때 자동 생성
    private BeaconConfig getOrCreate(Long clubId) {
        return beaconConfigRepository.findByClubId(clubId)
                .orElseGet(() -> {
                    Club club = clubRepository.findById(clubId)
                            .orElseThrow(() -> new CustomException(ErrorCode.CLUB_NOT_FOUND));
                    return beaconConfigRepository.save(BeaconConfig.builder()
                            .club(club)
                            .beaconUuid(club.getFixed_uuid()) //초기 UUID는 동아리 생성 시 발급된 값
                            .rssiThreshold(DEFAULT_RSSI_THRESHOLD)
                            .lateThresholdMinutes(DEFAULT_LATE_THRESHOLD_MINUTES)
                            .rssiStabilizationSeconds(DEFAULT_RSSI_STABILIZATION_SECONDS)
                            .build());
                });
    }

    private BeaconConfigDto toDto(BeaconConfig config) {
        return new BeaconConfigDto(config.getBeaconUuid(), config.getLateThresholdMinutes(),
                config.getRssiStabilizationSeconds(), config.getRssiThreshold());
    }
}