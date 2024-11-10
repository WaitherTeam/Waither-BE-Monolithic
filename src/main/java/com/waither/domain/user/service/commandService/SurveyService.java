package com.waither.domain.user.service.commandService;

import com.waither.domain.noti.enums.Expressions;
import com.waither.domain.user.converter.SurveyConverter;
import com.waither.domain.user.dto.request.SurveyReqDto;
import com.waither.domain.user.dto.response.SurveyResDto;
import com.waither.domain.user.entity.Survey;
import com.waither.domain.user.entity.User;
import com.waither.domain.user.entity.UserData;
import com.waither.domain.user.entity.UserMedian;
import com.waither.domain.user.entity.enums.Season;
import com.waither.domain.user.repository.SurveyRepository;
import com.waither.domain.user.repository.UserDataRepository;
import com.waither.domain.user.repository.UserMedianRepository;
import com.waither.domain.user.repository.UserRepository;
import com.waither.domain.weather.service.WeatherService;
import com.waither.global.exception.CustomException;
import com.waither.domain.user.exception.UserErrorCode;
import com.waither.global.utils.WeatherMessageUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static com.waither.global.utils.WeatherMessageUtil.getCurrentSeason;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class SurveyService {

    private final WeatherService weatherService;
    private final SurveyRepository surveyRepository;
    private final UserDataRepository userDataRepository;
    private final UserMedianRepository userMedianRepository;
    private final UserRepository userRepository;

    @Transactional
    public SurveyResDto.ExpressionListDto getSeasonalExpressions() {
        Season currentSeason = WeatherMessageUtil.getCurrentSeason();
        Expressions[] expressions;

        switch (currentSeason) {
            case WINTER:
                expressions = WeatherMessageUtil.winterExpressions;
                break;
            case SUMMER:
                expressions = WeatherMessageUtil.summerExpressions;
                break;
            default:  // SPRING_AUTUMN
                expressions = WeatherMessageUtil.springAndAutumnExpressions;
                break;
        }

        List<String> expressionList = Arrays.stream(expressions)
                .map(Expressions::getExpression)
                .toList();

        return SurveyConverter.toExpressionListDto(expressionList);
    }

    @Transactional
    public void createSurvey(User currentUser, SurveyReqDto.SurveyRequestDto surveyRequestDto) {
        User user = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));
        Double temp = getTemp(surveyRequestDto.latitude(), surveyRequestDto.longitude(), surveyRequestDto.time());
        Survey survey = SurveyConverter.toSurvey(surveyRequestDto, temp, getCurrentSeason());
        survey.setUser(user);

        UserData userData = userDataRepository.findByUserAndSeason(user, survey.getSeason())
                .orElseThrow(() -> new CustomException(UserErrorCode.NO_USER_DATA_FOUND));
        UserMedian userMedian = userMedianRepository.findByUserAndSeason(user, survey.getSeason())
                .orElseThrow(() -> new CustomException(UserErrorCode.NO_USER_MEDIAN_FOUND));

        updateUserData(userData, surveyRequestDto.ans(), temp);
        updateUserMedian(userData, userMedian);

        surveyRepository.save(survey);
    }

    private void updateUserData(UserData userData, Integer ans, Double temp) {

        double newValue = (userData.getLevel(ans) + temp) / 2 ;

        if (!isValidLevelValue(userData, ans, newValue)) {
            throw new CustomException(UserErrorCode.IGNORE_SURVEY_ANSWER);
        }

        userData.updateLevelValue(ans, newValue);

        userDataRepository.save(userData);
    }

    private void updateUserMedian(UserData userData, UserMedian userMedian) {
        userMedian.updateMedianValue(userData);
        userMedianRepository.save(userMedian);
    }

    public Double getTemp(double latitude, double longitude, LocalDateTime time) {
        return weatherService.getWindChill(latitude, longitude, time);
    }

    // 상위, 하위 온도가 해당 온도를 넘는가
    private boolean isValidLevelValue(UserData userData, Integer ans, Double newValue) {
        Double lowerLevelValue = ans > 1 ? userData.getLevel(ans - 1) : Double.MIN_VALUE;
        Double upperLevelValue = ans < 5 ? userData.getLevel(ans + 1) : Double.MAX_VALUE;

        return newValue >= lowerLevelValue && newValue <= upperLevelValue;
    }

}
