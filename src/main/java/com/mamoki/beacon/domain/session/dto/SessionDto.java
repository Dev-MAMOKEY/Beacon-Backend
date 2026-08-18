package com.mamoki.beacon.domain.session.dto;

import com.mamoki.beacon.domain.session.entity.SessionCategory;
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
    private LocalDateTime expectStartAt;
    private LocalDateTime expectEndAt;
    private SessionCategory sessionCategory;
    private String location;
    private String description;
}
