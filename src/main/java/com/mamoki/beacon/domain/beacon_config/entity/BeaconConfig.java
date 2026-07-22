package com.mamoki.beacon.domain.beacon_config.entity;

import com.mamoki.beacon.domain.club.entity.Club;
import com.mamoki.beacon.global.entity.GlobalEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "beacon_config")
public class BeaconConfig extends GlobalEntity {

    @OneToOne(fetch = FetchType.LAZY) //Club당 설정 1개
    @JoinColumn(name = "club_id", nullable = false, unique = true)
    private Club club;

    @Column(name = "beacon_uuid", nullable = false, length = 50)
    private String beaconUuid; //등록된 비콘 UUID

    @Column(name = "rssi_threshold", nullable = false)
    private Integer rssiThreshold; //유효 감지 RSSI 임계값 (dBm, 기본 -70)

    @Column(name = "late_threshold_minutes", nullable = false)
    private Integer lateThresholdMinutes; //지각 기준 시간 (분, 기본 10)

    @Column(name = "rssi_stabilization_seconds", nullable = false)
    private Integer rssiStabilizationSeconds; //신호 안정화 임계 시간 (초, 기본 3)
}
