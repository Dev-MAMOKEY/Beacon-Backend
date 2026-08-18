package com.mamoki.beacon.domain.member.repository;

import com.mamoki.beacon.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByStdId(String stdId); // 학번으로 회원 조회시 사용
    boolean existsByStdId(String stdId); // 회원가입 시 학번 중복 방지 체크
    Optional<Member> findByRefreshToken(String refreshToken);
}