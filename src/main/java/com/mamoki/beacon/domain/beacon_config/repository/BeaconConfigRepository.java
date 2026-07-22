package com.mamoki.beacon.domain.beacon_config.repository;

import com.mamoki.beacon.domain.beacon_config.entity.BeaconConfig;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BeaconConfigRepository extends JpaRepository<BeaconConfig, Long> {
    Optional<BeaconConfig> findByClubId(Long clubId); //club.id 기준 조회 (Club당 1개)
}
