package com.waither.domain.user.entity;

import com.waither.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

// 코드 일부 생략

@Builder
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "settings")
@Entity
@DynamicInsert
@DynamicUpdate
public class Setting extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 외출 알림
    @Column(name = "outAlert", nullable = false)
    private boolean outAlert;

    // 외출 시간
    @Column(name = "outTime")
    private LocalTime outTime;

    // 월 ~ 금 알림
    @ElementCollection
    @CollectionTable(name = "setting_days", joinColumns = @JoinColumn(name = "setting_id"))
    @Column(name = "day")
    @Enumerated(EnumType.STRING)
    private Set<DayOfWeek> days = new HashSet<>();

    // 기상 특보 알림
    @Column(name = "climateAlert", nullable = false)
    private boolean climateAlert;

    // 사용자 맞춤 예보 받기
    @Column(name = "userAlert", nullable = false)
    private boolean userAlert;

    // 강설 정보 알림
    @Column(name = "snowAlert", nullable = false)
    private boolean snowAlert;

    // 바람 세기 알림
    @Column(name = "windAlert", nullable = false)
    private boolean windAlert;
    // 바람세기 정도
    @Column(name = "windDegree", nullable = false)
    private Integer windDegree;

    // 직장 지역 레포트 알림 받기
    @Column(name = "regionReport", nullable = false)
    private boolean regionReport;

    // 강수량 보기
    @Column(name = "precipitation", nullable = false)
    private boolean precipitation;
    // 풍량/풍속 보기
    @Column(name = "wind", nullable = false)
    private boolean wind;
    // 미세먼지 보기
    @Column(name = "dust", nullable = false)
    private boolean dust;

    // 사용자 데이터 가중치
    private Double weight;

    // Mapping
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "region_id", unique = true)
    private Region region;

    // Id에 Setter 쓰지 않기 위해, 명시적으로 지정
    public void setId(Long id) {
    }

}