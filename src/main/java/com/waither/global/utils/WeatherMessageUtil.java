package com.waither.global.utils;

import com.waither.domain.noti.enums.Expressions;
import com.waither.domain.user.entity.UserMedian;
import com.waither.domain.user.entity.enums.Season;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

import static com.waither.domain.noti.enums.Expressions.*;


@RequiredArgsConstructor
@Component
public class WeatherMessageUtil {

    public static Expressions[] winterExpressions = {WARM, GOOD, LITTLE_COLD, COLD, VERY_COLD };
    public static Expressions[] springAndAutumnExpressions = {HOT, LITTLE_HOT, GOOD, LITTLE_COLD, COLD};
    public static Expressions[] summerExpressions = {COOL, GOOD, LITTLE_HOT, HOT, VERY_HOT};
    public static double WEIGHT_ADJUSTMENT_FACTOR = 0.5;

    public static String createUserDataMessage(UserMedian userMedian, double temperature, double weight) {
        double medianBetween1And2 = userMedian.getMedianOf1And2() + weight * WEIGHT_ADJUSTMENT_FACTOR;
        double medianBetween2And3 = userMedian.getMedianOf2And3() + weight * WEIGHT_ADJUSTMENT_FACTOR;
        double medianBetween3And4 = userMedian.getMedianOf3And4() + weight * WEIGHT_ADJUSTMENT_FACTOR;
        double medianBetween4And5 = userMedian.getMedianOf4And5() + weight * WEIGHT_ADJUSTMENT_FACTOR;

        if (temperature < medianBetween1And2) {
            return getExpression(getCurrentSeason(), 1);
        } else if (medianBetween1And2 <= temperature && temperature < medianBetween2And3) {
            return getExpression(getCurrentSeason(), 2);
        }else if (medianBetween2And3 <= temperature && temperature < medianBetween3And4) {
            return getExpression(getCurrentSeason(), 3);
        }else if (medianBetween3And4 <= temperature && temperature < medianBetween4And5) {
            return getExpression(getCurrentSeason(), 4);
        }else if (medianBetween4And5 <= temperature) {
            return getExpression(getCurrentSeason(), 5);
        }
        return null;
    }

    public static Season getCurrentSeason() {
        LocalDateTime now = LocalDateTime.now();
        int month = now.getMonthValue();
        if ((3 <= month && month <= 5) || (9 <= month && month <= 11)) { //봄 or 가을 3, 4, 5 or 9, 10, 11
            return Season.SPRING_AUTUMN;
        } else if (6 <= month && month <= 8) { //여름 6, 7, 8
            return Season.SUMMER;
        } else return Season.WINTER; //겨울 12, 1, 2
    }

    public static String getExpression(Season season, int level) {
        if (season == Season.WINTER) { //겨울
            return winterExpressions[level - 1].getExpression();
        } else if (season == Season.SUMMER) { //여름
            return summerExpressions[level - 1].getExpression();
        } else { //봄, 가을
            return springAndAutumnExpressions[level - 1].getExpression();
        }
    }

    public static String getRainPredictionsMessage(List<Double> predictions) {

        List<String> predictionStr = predictions.stream()
                .map(prediction -> prediction == 0 ? "강수없음" : getRainExpression(prediction))
                .toList();
        //예시 ["강수없음", "약한 비", "약한 비", "비", "비", "비"]

        //몇 시간 뒤에 비가 얼만큼 몇 시간 동안 오는지?
        int startHour = -1;
        int duration = 0;
        String intensity = "";
        boolean isRaining = false;

        for (int i = 0; i < predictionStr.size(); i++) {
            String current = predictionStr.get(i);
            if (!current.equals("강수없음")) {
                if (!isRaining) {
                    startHour = i + 1;
                    intensity = current;
                }
                isRaining = true;
                duration++;
            } else if (isRaining) {
                break;
            }
        }

        if (startHour == -1) {
            return null;
        } else {
            String timePhrase = startHour == 1 ? "1시간 후부터" : startHour + "시간 후부터";
            String durationPhrase = duration == 1 ? "1시간 동안" : duration + "시간 동안";
            //예시 "3시간 후부터 약한 비가 4시간 동안 올 예정입니다."
            return String.format("%s %s가 %s 올 예정입니다.", timePhrase, intensity, durationPhrase);
        }
    }

    //강수 표현
    public static String getRainExpression(double prediction) {
        //1~3mm : 약한 비
        if (prediction > 1 && prediction < 3) {
            return "약한 비";
            //3~15mm : 비
        } else if (prediction >=3 && prediction < 15) {
            return "비";
            //15~30mm 강한 비
        } else if (prediction >= 15 &&prediction <= 30) {
            return "강한 비";
            //30mm~ 매우 강한 비
        } else if (prediction >= 30) {
            return "매우 강한 비";
        } else return "비";
    }
}
