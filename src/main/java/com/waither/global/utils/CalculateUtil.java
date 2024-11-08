package com.waither.global.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CalculateUtil {

    public static double calculateMedian(double value1, double value2) {
        return (value1 + value2) / 2;
    }

    public static double calculateWindChill(double temp, double wind) {
        if (temp > 10 || wind < 4.8) {
            return temp;
        }
        return 13.12 + 0.6215 * temp - 11.37 * Math.pow(wind, 0.16) + 0.3965 * temp * Math.pow(wind, 0.16);
    }

    // 열지수 공식
    public static double calculateHeatIndex(double temp, double humidity) {
        return -8.784695 + 1.61139411 * temp + 2.338549 * humidity
                - 0.14611605 * temp * humidity - 0.012308094 * temp * temp
                - 0.016424828 * humidity * humidity + 0.002211732 * temp * temp * humidity
                + 0.00072546 * temp * humidity * humidity
                - 0.000003582 * temp * temp * humidity * humidity;
    }

    // 불쾌지수 공식
    public static double calculateDiscomfortIndex(double temp, double humidity) {
        return 0.81 * temp + 0.01 * humidity * (0.99 * temp - 14.3) + 46.3;
    }

}
