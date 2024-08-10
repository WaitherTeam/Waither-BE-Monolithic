package com.waither.domain.user.repository;

import com.waither.domain.user.entity.Survey;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SurveyRepository extends JpaRepository<Survey, Integer> {

    Survey findByUserId(Long UserId);
}
