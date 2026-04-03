package com.mamoki.beacon.global.config;

import com.mamoki.beacon.domain.club.entity.Club;
import com.mamoki.beacon.domain.club.repository.ClubRepository;
import com.mamoki.beacon.domain.club_member.entity.ClubMember;
import com.mamoki.beacon.domain.club_member.entity.Role;
import com.mamoki.beacon.domain.club_member.repository.ClubMemberRepository;
import com.mamoki.beacon.domain.member.entity.Member;
import com.mamoki.beacon.domain.member.repository.MemberRepository;
import com.mamoki.beacon.global.security.jwt.JwtProvider;
import com.mamoki.beacon.global.security.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) // 읽기 전용 트랜잭션 설정 -> 쓰기 사용 시에는 @Transactional 사용
public class DataInitializer {

    private final JwtProvider jwtProvider;
    private final MemberRepository memberRepository;
    private final ClubRepository clubRepository;
    private final ClubMemberRepository clubMemberRepository;
    private final JwtUtil jwtUtil;

    @Bean
        // 테스트 진행 위해 CommandLineRunner 빈 등록 -> 실행 시 초기 데이터 미리 삽입

    CommandLineRunner initMember() { // 회원 초기 데이터 삽입
        return args -> {
            if (memberRepository.count() == 0) {
                memberRepository.save(new Member(20230001, "qwer1234", 1, "사용자"));
                memberRepository.save(new Member(20239999, "admin1234", 4, "관리자"));
            }
        };
    }

    @Bean
    CommandLineRunner initClub() { // 동아리 초기 데이터 삽입
        return args -> {
            if (clubRepository.count() == 0) {
                clubRepository.save(new Club("마모키", "신한대 소프트웨어융합학과 IT 동아리입니다."));
                clubRepository.save(new Club("키모마", "테스트를 위한 동아리입니다."));
            }
        };
    }

    @Bean
    CommandLineRunner initClubMember() { // 회원과 동아리를 조회하여 ClubMember 엔티티 초기 데이터 삽입
        return args -> {
            if (clubMemberRepository.count() == 0) {
                // 초기 데이터로 회원과 동아리를 조회하여 ClubMember 엔티티를 생성

                // 회원 조회
                Member member = memberRepository.findByStdId(20230001)
                        .orElseThrow(() -> new RuntimeException("회원이 존재하지 않습니다."));
                Member admin = memberRepository.findByStdId(20239999)
                        .orElseThrow(() -> new RuntimeException("관리자가 존재하지 않습니다."));

                // 동아리 조회
                Club mamoki = clubRepository.findByClubName("마모키")
                        .orElseThrow(() -> new RuntimeException("마모키 동아리가 존재하지 않습니다."));
                Club kimoma = clubRepository.findByClubName("키모마")
                        .orElseThrow(() -> new RuntimeException("키모마 동아리가 존재하지 않습니다."));

                clubMemberRepository.saveAll(List.of(
                        new ClubMember(member, mamoki, Role.MEMBER), // 일반 회원으로 마모키 동아리에 가입
                        new ClubMember(admin, mamoki, Role.ADMIN), // 관리자 회원 마모키 동아리에 가입
                        new ClubMember(admin, kimoma, Role.ADMIN) // 관리자 회원 키모마 동아리에 가입
                ));
            }
        };
    }
}
