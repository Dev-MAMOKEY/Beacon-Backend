package com.mamoki.beacon.domain.club.repository;

import com.mamoki.beacon.domain.club.entity.Club;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ClubRepository extends JpaRepository<Club, Long> {
    Optional<Club> findByClubName(String clubName);
}
