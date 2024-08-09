package com.waither.domain.user.repository;

import com.waither.domain.user.entity.User;
import com.waither.domain.user.entity.UserMedian;
import com.waither.domain.user.entity.enums.Season;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserMedianRepository extends JpaRepository<UserMedian, Integer> {
    Optional<UserMedian> findByUserAndSeason(User user, Season season);

}
