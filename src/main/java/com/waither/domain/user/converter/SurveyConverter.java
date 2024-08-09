package com.waither.domain.user.converter;

import com.waither.domain.user.dto.request.SurveyReqDto;
import com.waither.domain.user.entity.Survey;
import com.waither.domain.user.entity.UserData;
import com.waither.domain.user.entity.UserMedian;
import com.waither.domain.user.entity.enums.Season;
import com.waither.global.exception.CustomException;
import com.waither.global.response.UserErrorCode;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import static com.waither.global.utils.CalculateUtil.calculateMedian;


@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SurveyConverter {

    // UserData 기본값으로 설정
    public static UserData createUserData(Season season) {
        switch (season) {
            case SPRING_AUTUMN:
                return UserData.builder()
                        .level1(10.0)
                        .level2(17.0)
                        .level3(24.0)
                        .level4(27.0)
                        .level5(30.0)
                        .season(season)
                        .build();
            case SUMMER:
                return UserData.builder()
                        .level1(15.0)
                        .level2(24.0)
                        .level3(30.0)
                        .level4(33.0)
                        .level5(36.0)
                        .season(season)
                        .build();
            case WINTER:
                return UserData.builder()
                        .level1(-17.0)
                        .level2(-7.0)
                        .level3(0.0)
                        .level4(6.0)
                        .level5(12.0)
                        .season(season)
                        .build();
            default:
                throw new CustomException(UserErrorCode.INVALID_SEASON);
        }
    }

    // UserMedian 기본값으로 설정
    public static UserMedian createUserMedian(UserData userData) {
        return UserMedian.builder()
                .medianOf1And2(calculateMedian(userData.getLevel1(), userData.getLevel2()))
                .medianOf2And3(calculateMedian(userData.getLevel2(), userData.getLevel3()))
                .medianOf3And4(calculateMedian(userData.getLevel3(), userData.getLevel4()))
                .medianOf4And5(calculateMedian(userData.getLevel4(), userData.getLevel5()))
                .season(userData.getSeason())
                .build();
    }

    public static Survey toSurvey(SurveyReqDto.SurveyRequestDto surveyRequestDto, Double temp, Season season) {
        return Survey.builder()
                .temp(temp)
                .ans(surveyRequestDto.ans())
                .time(surveyRequestDto.time())
                .season(season)
                .build();
    }

}
