package com.waither.domain.user.dto.request;

import java.time.LocalDateTime;

public class SurveyReqDto {

    // Todo : DTO에서 날짜/시간 관련 필드는 String 타입으로 선언하고, 서비스 계층에서 원하는 형식으로 파싱하는 것이 좋은가 고민중.
    public record SurveyRequestDto(

            // level
            Integer ans,
            LocalDateTime time
    ) {}

}
