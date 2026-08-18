package com.mamoki.beacon.domain.club.repository;

import com.mamoki.beacon.domain.club.entity.Club;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface ClubRepository extends JpaRepository<Club, Long> {
    Optional<Club> findByClubName(String clubName);
    @Query("SELECT c FROM Club c WHERE c.id = :id AND (c.isDeleted = false OR c.isDeleted IS NULL)")
    Optional<Club> findByIdAndIsDeletedFalse(@Param("id") Long id);
}
