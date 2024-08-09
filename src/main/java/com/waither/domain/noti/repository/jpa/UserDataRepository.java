package com.waither.domain.noti.repository.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserDataRepository extends JpaRepository<UserData, String> {

    Optional<UserData> findByEmail(String email);

    List<UserData> findAllByClimateAlertIsTrue();

    List<UserData> findAllByWindAlertIsTrue();

    List<UserData> findAllBySnowAlertIsTrue();
}
