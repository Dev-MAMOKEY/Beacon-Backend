package com.mamoki.beacon.domain.session.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SessionDto {
    private String sessionName;
    private String uuid;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
}
