package com.waither.domain.user.controller;

import com.waither.domain.user.dto.request.SettingReqDto;
import com.waither.domain.user.dto.response.SettingResDto;
import com.waither.domain.user.entity.User;
import com.waither.domain.user.service.commandService.SettingService;
import com.waither.domain.user.service.queryService.SettingQueryService;
import com.waither.global.annotation.AuthUser;
import com.waither.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/user/setting")
public class SettingController {

    private final SettingService settingService;
    private final SettingQueryService settingQueryService;

    /* --------- ReadOnly (GET) <화면 별로 Response 구성> --------- */
    // 사용자 맞춤 서비스 제공 조히
    @GetMapping("/custom")
    public ApiResponse<SettingResDto.CustomDto> getUserCustom(@AuthUser String email) {
        return ApiResponse.onSuccess(settingQueryService.getUserCustom(email));
    }

    // 직장 지역 설정 조회
    @GetMapping("/region")
    public ApiResponse<SettingResDto.RegionNameDto> getRegion(@AuthUser String email) {
        return ApiResponse.onSuccess(settingQueryService.getRegion(email));
    }

//    // 알림 설정 조회
    @GetMapping("/noti")
    public ApiResponse<SettingResDto.NotificationDto> getNotification(@AuthUser String email) {
        return ApiResponse.onSuccess(settingQueryService.getNotification(email));
    }

    // 바람 세기 설정 조회
    @GetMapping("/noti/wind")
    public ApiResponse<SettingResDto.WindDto> getWind(@AuthUser String email) {
        return ApiResponse.onSuccess(settingQueryService.getWind(email));
    }

    // 메인 화면 날씨 상세 정보 조회
    @GetMapping("/display")
    public ApiResponse<SettingResDto.DisplayDto> getDisplay(@AuthUser String email) {
        return ApiResponse.onSuccess(settingQueryService.getDisplay(email));
    }

    // 사용자 가중치 설정 조회
    @GetMapping("/user-weight")
    public ApiResponse<SettingResDto.WeightDto> getWeight(@AuthUser String email) {
        return ApiResponse.onSuccess(settingQueryService.getWeight(email));
    }

    // 마이페이지 조회
    @GetMapping("/mypage")
    public ApiResponse<SettingResDto.UserInfoDto> getUserInfo(@AuthUser String email) {
        return ApiResponse.onSuccess(settingQueryService.getUserInfo(email));
    }

    /* --------- Update (Put) --------- */

    // 사용자 맞춤 서비스 제공
    @PutMapping("/custom")
    public ApiResponse<String> updateCustom(@AuthUser String email, @RequestBody SettingReqDto.CustomDto customDto) {
        settingService.updateCustom(email, customDto);
        return ApiResponse.onSuccess("설정값 변경이 완료되었습니다.");
    }

    // 메인 화면 날씨 상세 정보 보기 (강수량, 퓽향/풍속, 미세먼지)
    @PatchMapping("/display")
    public ApiResponse<String> updateDisplay(@AuthUser String email, @RequestBody SettingReqDto.DisplayDto displayDto) {
        settingService.updateDisplay(email, displayDto);
        return ApiResponse.onSuccess(
                "설정값 변경이 완료되었습니다.");
    }

    // 외출 알림 설정
    @PutMapping("/noti/out-alert")
    public ApiResponse<String> updateOutAlert(@AuthUser String email, @RequestBody SettingReqDto.OutAlertDto outAlertDto) {
        settingService.updateOutAlert(email, outAlertDto);
        return ApiResponse.onSuccess("설정값 변경이 완료되었습니다.");
    }

    // 알림 설정 (요일 & 시간)
    @PutMapping("/noti/out-alert-set")
    public ApiResponse<String> updateOutAlertSet(@AuthUser String email, @RequestBody SettingReqDto.OutAlertSetDto outAlertSetDto) {
        settingService.updateOutAlertSet(email, outAlertSetDto);
        return ApiResponse.onSuccess("설정값 변경이 완료되었습니다.");
    }

    // 기상 특보 알림 설정
    @PutMapping("/noti/climate-alert")
    public ApiResponse<String> updateClimateAlert(@AuthUser String email, @RequestBody SettingReqDto.ClimateAlertDto climateAlertDto) {
        settingService.updateClimateAlert(email, climateAlertDto);
        return ApiResponse.onSuccess("설정값 변경이 완료되었습니다.");
    }

    // 사용자 맞춤 예보 설정
    @PutMapping("/noti/user-alert")
    public ApiResponse<String> updateUserAlert(@AuthUser String email, @RequestBody SettingReqDto.UserAlertDto userAlertDto) {
        settingService.updateUserAlert(email, userAlertDto);
        return ApiResponse.onSuccess("설정값 변경이 완료되었습니다.");
    }

    // 강설 정보 알림 설정
    @PutMapping("/noti/snow-alert")
    public ApiResponse<String> updateSnowAlert(@AuthUser String email, @RequestBody SettingReqDto.SnowAlertDto snowAlertDto) {
        settingService.updateSnowAlert(email, snowAlertDto);
        return ApiResponse.onSuccess("설정값 변경이 완료되었습니다.");
    }

    // 바람 세기 알림
    @PutMapping("/noti/wind")
    public ApiResponse<String> updateWind(@AuthUser String email, @RequestBody SettingReqDto.WindDto windDto) {
        settingService.updateWind(email, windDto);
        return ApiResponse.onSuccess(
                "설정값 변경이 완료되었습니다.");
    }

    // 직장 지역 레포트 알림 받기
    @PutMapping("/region-report")
    public ApiResponse<String> updateRegionReport(@AuthUser String email, @RequestBody SettingReqDto.RegionReportDto regionReportDto) {
        settingService.updateRegionReport(email, regionReportDto);
        return ApiResponse.onSuccess("설정값 변경이 완료되었습니다.");
    }

    // 직장 지역 설정
    @PutMapping("/region")
    public ApiResponse<String> updateRegion(@AuthUser String email, @RequestBody SettingReqDto.RegionDto regionDto) {
        settingService.updateRegion(email, regionDto);
        return ApiResponse.onSuccess("설정값 변경이 완료되었습니다.");
    }

    // 사용자 가중치 설정
    @PutMapping("/user-weight")
    public ApiResponse<String> updateWeight(@AuthUser String email, @RequestBody SettingReqDto.WeightDto weightDto) {
        settingService.updateWeight(email, weightDto);
        return ApiResponse.onSuccess("설정값 변경이 완료되었습니다.");
    }

}
