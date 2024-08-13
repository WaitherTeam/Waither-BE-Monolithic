package com.waither.domain.user.controller;

import com.waither.domain.user.dto.request.SurveyReqDto;
import com.waither.domain.user.entity.User;
import com.waither.domain.user.service.commandService.SurveyService;
import com.waither.global.annotation.AuthUser;
import com.waither.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/user/survey")
public class SurveyController {

    private final SurveyService surveyService;
    @PostMapping("/submit")
    public ApiResponse<String> createSurvey(@AuthUser String email, @RequestBody SurveyReqDto.SurveyRequestDto surveyRequestDto) {
        surveyService.createSurvey(email, surveyRequestDto);
        return ApiResponse.onSuccess("survey 생성완료");
    }

//    @PostMapping("/reset")
//    public ApiResponse<String> resetServeyData(@AuthUser User user) {
//        surveyService.resetSurveyData(user);
//        return ApiResponse.onSuccess("사용자의 설문 정보를 초기화 하였습니다.");
//    }

}
