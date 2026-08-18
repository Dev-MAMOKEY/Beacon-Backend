package com.mamoki.beacon.domain.member.entity;

import com.mamoki.beacon.domain.auth.dto.SignupResponse;
import com.mamoki.beacon.domain.member.dto.profile.MemberProfileResponse;
import com.mamoki.beacon.global.entity.GlobalEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "member")
public class Member extends GlobalEntity {

    @Column(name = "student_id")
    private String stdId;

    @Column(name = "password_hash")
    private String password;

    @Column(name = "name")
    private String name;

    @Column(name = "title")
    private String title;

    @Column(name = "fcm_token")
    private String fcmToken;

    @Column(name = "push_enabled")
    private Boolean pushEnabled = true;

    @Column(name = "refresh_token")
    private String refreshToken;

    @Column(name = "rt_at")
    private LocalDateTime rtAt;

    public Member(String stdId, String password, String name) {
        this.stdId = stdId;
        this.password = password;
        this.name = name;
        this.pushEnabled = true;
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

    public MemberProfileResponse toMemberProfileResponse(List<Long> clubIds) {
        return new MemberProfileResponse(name, clubIds, stdId, title, pushEnabled);
    }

    public void updatePassword(String newPassword) {
        this.password = newPassword;
    }

    public void updateFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }

    public void updateProfile(String newName, String newTitle, Boolean changePushEnabled) {
        this.name = newName;
        this.title = newTitle;
        this.pushEnabled = changePushEnabled;
    }
}