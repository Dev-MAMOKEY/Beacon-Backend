package com.mamoki.beacon.domain.club.entity;

import com.mamoki.beacon.global.entity.GlobalEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Entity
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

    @Column(name = "fixed_uuid")
    private String fixedUuid;

    @Column(name = "psk")
    private String psk;

    public Club(String clubName, String clubDescription) {
        this.clubName = clubName;
        this.clubDescription = clubDescription;
    }
}
