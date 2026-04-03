package com.mamoki.beacon.domain.member.entity;

import com.mamoki.beacon.domain.auth.dto.SignupResponse;
import com.mamoki.beacon.global.entity.GlobalEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "member")
public class Member extends GlobalEntity {

    @Column(name = "student_id")
    private int stdId;

    @Column(name = "grade")
    private int grade;

    @Column(name = "password_hash")
    private String password;

    @Column(name = "name")
    private String name;

    @Column(name = "fcm_token")
    private String fcmToken;

    @Column(name = "push_enabled")
    private Boolean pushEnabled;

    @Column(name = "refresh_token")
    private String refreshToken;

    @Column(name = "rt_at")
    private LocalDateTime rtAt;

    public Member(int stdId, String password, int grade, String name) {
        this.stdId = stdId;
        this.password = password;
        this.grade = grade;
        this.name = name;
    }

    public SignupResponse toSignupResponse() {
        return new SignupResponse(getId(), stdId, name);
    }

    public void updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
        this.rtAt = LocalDateTime.now();
    }

    public void revokeRefreshToken() {
        this.refreshToken = null;
        this.rtAt = null;
    }
}