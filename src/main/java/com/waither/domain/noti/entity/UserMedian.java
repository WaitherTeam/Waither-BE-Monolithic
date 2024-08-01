package com.waither.domain.noti.entity;

import com.waither.global.enums.Season;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;

@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "user_median")
@DynamicInsert
@Entity
public class UserMedian {

    @Id
    private String email;
    private Double medianOf1And2;
    private Double medianOf2And3;
    private Double medianOf3And4;
    private Double medianOf4And5;

    @Enumerated(value = EnumType.STRING)
    public Season season;

    public void setLevel(int level, Double value) {
        switch (level) {
            case 1 -> medianOf1And2 = value;
            case 2 -> medianOf2And3 = value;
            case 3 -> medianOf3And4 = value;
            case 4 -> medianOf4And5 = value;
        }
    }

    public void setLevel(KafkaDto.UserMedianDto userMedianDto) {
        KafkaDto.SeasonData seasonData = userMedianDto.seasonData();
        medianOf1And2 = seasonData.medianOf1And2();
        medianOf2And3 = seasonData.medianOf2And3();
        medianOf3And4 = seasonData.medianOf3And4();
        medianOf4And5 = seasonData.medianOf4And5();

    }
}
