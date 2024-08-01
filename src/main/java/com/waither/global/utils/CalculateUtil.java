package com.waither.global.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CalculateUtil {

    public static double calculateMedian(double value1, double value2) {
        return (value1 + value2) / 2;
    }

}
