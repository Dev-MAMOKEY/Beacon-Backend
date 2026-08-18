package com.mamoki.beacon.domain.club.entity;

import com.mamoki.beacon.global.entity.GlobalEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@SuperBuilder
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "club")
public class Club extends GlobalEntity {
    @Column(name = "club_name")
    private String clubName;

    @Column(name = "club_description")
    private String clubDescription;

    @Column(name = "is_deleted")
    private Boolean isDeleted;

    private String fixed_uuid;

    private String psk;
}
