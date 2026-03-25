package com.mamoki.beacon.domain.member.entity;

import com.mamoki.beacon.global.entity.GlobalEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Member extends GlobalEntity {

    @Column(name = "student_id")
    private String stdId;

    @Column(name = "password_hash")
    private String password;

    @Column(name = "name")
    private String name;

    @Column(name = "fcm_token")
    private String fcmToken;

    @Column(name = "push_enabled")
    private Boolean pushEnabled;
}
