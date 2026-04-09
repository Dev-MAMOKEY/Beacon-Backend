package com.mamoki.beacon.domain.club_member.controller;

import com.mamoki.beacon.domain.club_member.service.ClubMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/clubs")
@RequiredArgsConstructor
public class ClubMemberController {

    private ClubMemberService clubMemberService;

    @GetMapping("/{clubId}/members")
    public ResponseEntity<>
}
