package com.waither.domain.user.repository;

import com.waither.domain.user.entity.User;
import com.waither.domain.user.entity.UserData;
import com.waither.domain.user.entity.enums.Season;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

public interface UserDataRepository extends JpaRepository<UserData, Integer> {
    Optional<UserData> findByUserAndSeason(User user, Season season);

    Optional<UserData> findByUser_Email(String email);

    Optional<UserData> findByUser(User user);

}
