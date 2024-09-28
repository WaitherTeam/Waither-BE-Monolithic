package com.waither.domain.user.repository;

import com.waither.domain.user.entity.Survey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface SurveyRepository extends JpaRepository<Survey, Integer> {

    Survey findByUserId(Long UserId);

    @Query("SELECT s FROM Survey s WHERE s.temp BETWEEN :lowerTemp AND :upperTemp AND s.time > :startDate")
    List<Survey> findByTempRangeAndDateAfter(
            @Param("lowerTemp") Double lowerTemp,
            @Param("upperTemp") Double upperTemp,
            @Param("startDate") LocalDateTime startDate
    );
}
