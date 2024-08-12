package com.waither.domain.user.repository;

import com.waither.domain.user.entity.Setting;
import com.waither.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface SettingRepository extends JpaRepository<Setting, Integer> {

    //windAlert == true && windDegree > param 사용자 쿼리
    List<Setting> findAllByWindAlertIsTrueAndWindDegreeGreaterThan(Integer windDegree);

    List<Setting> findAllBySnowAlertIsTrue();

    List<Setting> findAllByClimateAlertIsTrue();

    Optional<Setting> findByUser(User user);
}
