package com.mamoki.beacon.global.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;


@AllArgsConstructor
@NoArgsConstructor
@MappedSuperclass
@Getter
@Setter
@SuperBuilder
@EntityListeners(GlobalEntityListener.class)
public class GlobalEntity {

    @Id@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    public void softDelete() { // 멤버 제명(소프트 딜리트) 위한 메서드
        this.deletedAt = LocalDateTime.now();
    }
}
