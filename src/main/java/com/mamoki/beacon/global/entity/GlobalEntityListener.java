package com.mamoki.beacon.global.entity;

import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;

import java.time.LocalDateTime;

public class GlobalEntityListener {
    @PrePersist
    public void prePersist(GlobalEntity entity) {
        LocalDateTime now = LocalDateTime.now();
        entity.setCreatedAt(now);
        entity.setUpdatedAt(now);
    }

    @PreUpdate
    public void preUpdate(GlobalEntity entity) {
        entity.setUpdatedAt(LocalDateTime.now());
    }
}
