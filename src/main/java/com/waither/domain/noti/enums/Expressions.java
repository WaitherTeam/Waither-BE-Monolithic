package com.waither.domain.noti.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Expressions {
    VERY_COLD("매우 추울거예요."),
    COLD("추울거예요."),
    LITTLE_COLD("조금 추울거예요."),
    COOL("시원할거예요."),
    GOOD("적당할거예요."),
    WARM("따뜻할거예요."),
    LITTLE_HOT("조금 더울거예요."),
    HOT("더울거예요."),
    VERY_HOT("매우 더울거예요."),
    ;

    final String expression;


}
