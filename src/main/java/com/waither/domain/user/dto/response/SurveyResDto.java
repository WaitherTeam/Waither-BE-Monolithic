package com.waither.domain.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;

public class SurveyResDto {

    @Builder
    public record ExpressionListDto(
            @Schema(description = "체감 온도 표현 목록")
            List<String> expressions
    ) {}
}