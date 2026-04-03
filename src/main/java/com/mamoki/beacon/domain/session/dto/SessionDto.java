package com.mamoki.beacon.domain.session.dto;

import com.mamoki.beacon.domain.session.entity.SessionStatus;
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
    private SessionStatus sessionStatus;
    private String sessionName;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private Long clubId;
    private String uuid;
}
