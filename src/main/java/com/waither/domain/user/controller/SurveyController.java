package com.waither.domain.user.controller;

import com.waither.domain.user.dto.request.SurveyReqDto;
import com.waither.domain.user.dto.response.SurveyResDto;
import com.waither.domain.user.entity.User;
import com.waither.domain.user.service.commandService.SurveyService;
import com.waither.global.jwt.annotation.CurrentUser;
import com.waither.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/user/survey")
public class SurveyController {

    private final SurveyService surveyService;

    @GetMapping("/expressions")
    @Operation(summary = "현재 계절에 맞는 체감 온도 표현 목록 조회")
    public ApiResponse<SurveyResDto.ExpressionListDto> getSeasonalExpressions() {
        SurveyResDto.ExpressionListDto expressionList = surveyService.getSeasonalExpressions();
        return ApiResponse.onSuccess(expressionList);
    }

    @PostMapping("/submit")
    @Operation(summary = "설문조사 제출")
    public ResponseEntity<ApiResponse<String>> createSurvey(@CurrentUser User currentUser, @RequestBody SurveyReqDto.SurveyRequestDto surveyRequestDto) {
        surveyService.createSurvey(currentUser, surveyRequestDto);
        // SignUp 때만 201 Created 사용
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        ApiResponse.onSuccess(HttpStatus.CREATED, "설문조사 생성완료.")
                );
    }


//    @PostMapping("/reset")
//    public ApiResponse<String> resetServeyData(@CurrentUser User user) {
//        surveyService.resetSurveyData(user);
//        return ApiResponse.onSuccess("사용자의 설문 정보를 초기화 하였습니다.");
//    }

}
