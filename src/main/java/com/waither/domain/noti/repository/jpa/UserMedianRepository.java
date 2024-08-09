package com.waither.domain.noti.repository.jpa;

import com.waither.global.enums.Season;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserMedianRepository extends JpaRepository<UserMedian, String> {

    Optional<UserMedian> findByEmailAndSeason(String email, Season season);
}
