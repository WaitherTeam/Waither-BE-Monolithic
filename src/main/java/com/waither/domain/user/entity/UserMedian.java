package com.waither.domain.user.entity;

import com.waither.domain.user.entity.enums.Season;
import com.waither.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import static com.waither.global.utils.CalculateUtil.calculateMedian;


@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "user_median")
@Entity
public class UserMedian extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 각 답변의 평균 값 사이 중간값 (level 1 쪽이 추움 ~ level 5 쪽이 더움)
    private Double medianOf1And2;
    private Double medianOf2And3;
    private Double medianOf3And4;
    private Double medianOf4And5;

    // 계절
    @Enumerated(EnumType.STRING)
    private Season season;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public void setUser(User user) {
        this.user = user;
    }

    public void updateMedianValue(UserData userData) {
        this.medianOf1And2 = calculateMedian(userData.getLevel1(), userData.getLevel2());
        this.medianOf2And3 = calculateMedian(userData.getLevel2(), userData.getLevel3());
        this.medianOf3And4 = calculateMedian(userData.getLevel3(), userData.getLevel4());
        this.medianOf4And5 = calculateMedian(userData.getLevel4(), userData.getLevel5());
    }

}

