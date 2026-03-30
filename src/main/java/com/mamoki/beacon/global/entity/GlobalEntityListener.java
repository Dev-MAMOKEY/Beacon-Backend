package com.mamoki.beacon.global.entity;

import jakarta.persistence.EntityListeners;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;

import java.time.LocalDateTime;

@EntityListeners(value = GlobalEntityListener.class)
public class GlobalEntityListener {

    @PrePersist  // INSERT 직전
    public void prePersist(GlobalEntity entity) {
        LocalDateTime now = LocalDateTime.now();
        entity.setCreatedAt(now);
        entity.setUpdatedAt(now);
    }

    @PreUpdate   // UPDATE 직전
    public void preUpdate(GlobalEntity entity) {
        entity.setUpdatedAt(LocalDateTime.now());
    }
}
