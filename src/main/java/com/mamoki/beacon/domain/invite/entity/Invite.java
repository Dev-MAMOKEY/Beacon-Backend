package com.mamoki.beacon.domain.invite.entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Invite {

    @Id@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "invite_id")
    private Long inviteId;

    @Column(name = "invite_code")
    private String inviteCode;

    @Column(name = "revoked_at")
    private Date revokedAt;

    @Column(name = "created_at")
    private Date createdAt;

}
