package com.waither.domain.noti.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;

@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "user_data")
@DynamicInsert
@Entity
public class UserData {

    @Id
    private String email;

    private String nickName;

    // 기상 특보 알림
    private boolean climateAlert;

    // 사용자 맞춤 알림
    private boolean userAlert;

    // 강설 정보 알림
    private boolean snowAlert;

    // 바람 세기 알림
    private boolean windAlert;

    // 바람세기 정도
    private Integer windDegree;

    // 직장 지역 레포트 알림 받기
    private boolean regionReport;

    //가중치
    private Double weight;

    public void updateValue(String key, String value) {
        switch (key) {
            case "nickName" -> nickName = value;
            case "climateAlert" -> climateAlert = Boolean.parseBoolean(value);
            case "userAlert" -> userAlert = Boolean.parseBoolean(value);
            case "snowAlert" -> snowAlert = Boolean.parseBoolean(value);
            case "windAlert" -> windAlert = Boolean.parseBoolean(value);
            case "regionReport" -> regionReport = Boolean.parseBoolean(value);
            case "windDegree" -> windDegree = Integer.valueOf(value);
            case "weight" -> weight = Double.valueOf(value);

        }
    }
}
