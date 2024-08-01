package com.waither.domain.user.repository;

import com.waither.userservice.entity.Setting;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SettingRepository extends JpaRepository<Setting, Integer> {

}
