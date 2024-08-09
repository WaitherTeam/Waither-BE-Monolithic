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
    public ApiResponse<SettingResDto.CustomDto> getUserCustom(@AuthUser User user) {
        return ApiResponse.onSuccess(settingQueryService.getUserCustom(user));
    }

    // 직장 지역 설정 조회
    @GetMapping("/region")
    public ApiResponse<SettingResDto.RegionNameDto> getRegion(@AuthUser User user) {
        return ApiResponse.onSuccess(settingQueryService.getRegion(user));
    }

//    // 알림 설정 조회
    @GetMapping("/noti")
    public ApiResponse<SettingResDto.NotificationDto> getNotification(@AuthUser User user) {
        return ApiResponse.onSuccess(settingQueryService.getNotification(user));
    }

    // 바람 세기 설정 조회
    @GetMapping("/noti/wind")
    public ApiResponse<SettingResDto.WindDto> getWind(@AuthUser User user) {
        return ApiResponse.onSuccess(settingQueryService.getWind(user));
    }

    // 메인 화면 날씨 상세 정보 조회
    @GetMapping("/display")
    public ApiResponse<SettingResDto.DisplayDto> getDisplay(@AuthUser User user) {
        return ApiResponse.onSuccess(settingQueryService.getDisplay(user));
    }

    // 사용자 가중치 설정 조회
    @GetMapping("/user-weight")
    public ApiResponse<SettingResDto.WeightDto> getWeight(@AuthUser User user) {
        return ApiResponse.onSuccess(settingQueryService.getWeight(user));
    }

    // 마이페이지 조회
    @GetMapping("/mypage")
    public ApiResponse<SettingResDto.UserInfoDto> getUserInfo(@AuthUser User user) {
        return ApiResponse.onSuccess(settingQueryService.getUserInfo(user));
    }

    /* --------- Update (Put) --------- */

    // 사용자 맞춤 서비스 제공
    @PutMapping("/custom")
    public ApiResponse<String> updateCustom(@AuthUser User user, @RequestBody SettingReqDto.CustomDto customDto) {
        settingService.updateCustom(user, customDto);
        return ApiResponse.onSuccess("설정값 변경이 완료되었습니다.");
    }

    // 메인 화면 날씨 상세 정보 보기 (강수량, 퓽향/풍속, 미세먼지)
    @PatchMapping("/display")
    public ApiResponse<String> updateDisplay(@AuthUser User user, @RequestBody SettingReqDto.DisplayDto displayDto) {
        settingService.updateDisplay(user, displayDto);
        return ApiResponse.onSuccess(
                "설정값 변경이 완료되었습니다.");
    }

    // 외출 알림 설정
    @PutMapping("/noti/out-alert")
    public ApiResponse<String> updateOutAlert(@AuthUser User user, @RequestBody SettingReqDto.OutAlertDto outAlertDto) {
        settingService.updateOutAlert(user, outAlertDto);
        return ApiResponse.onSuccess("설정값 변경이 완료되었습니다.");
    }

    // 알림 설정 (요일 & 시간)
    @PutMapping("/noti/out-alert-set")
    public ApiResponse<String> updateOutAlertSet(@AuthUser User user, @RequestBody SettingReqDto.OutAlertSetDto outAlertSetDto) {
        settingService.updateOutAlertSet(user, outAlertSetDto);
        return ApiResponse.onSuccess("설정값 변경이 완료되었습니다.");
    }

    // 기상 특보 알림 설정
    @PutMapping("/noti/climate-alert")
    public ApiResponse<String> updateClimateAlert(@AuthUser User user, @RequestBody SettingReqDto.ClimateAlertDto climateAlertDto) {
        settingService.updateClimateAlert(user, climateAlertDto);
        return ApiResponse.onSuccess("설정값 변경이 완료되었습니다.");
    }

    // 사용자 맞춤 예보 설정
    @PutMapping("/noti/user-alert")
    public ApiResponse<String> updateUserAlert(@AuthUser User user, @RequestBody SettingReqDto.UserAlertDto userAlertDto) {
        settingService.updateUserAlert(user, userAlertDto);
        return ApiResponse.onSuccess("설정값 변경이 완료되었습니다.");
    }

    // 강설 정보 알림 설정
    @PutMapping("/noti/snow-alert")
    public ApiResponse<String> updateSnowAlert(@AuthUser User user, @RequestBody SettingReqDto.SnowAlertDto snowAlertDto) {
        settingService.updateSnowAlert(user, snowAlertDto);
        return ApiResponse.onSuccess("설정값 변경이 완료되었습니다.");
    }

    // 바람 세기 알림
    @PutMapping("/noti/wind")
    public ApiResponse<String> updateWind(@AuthUser User user, @RequestBody SettingReqDto.WindDto windDto) {
        settingService.updateWind(user, windDto);
        return ApiResponse.onSuccess(
                "설정값 변경이 완료되었습니다.");
    }

    // 직장 지역 레포트 알림 받기
    @PutMapping("/region-report")
    public ApiResponse<String> updateRegionReport(@AuthUser User user, @RequestBody SettingReqDto.RegionReportDto regionReportDto) {
        settingService.updateRegionReport(user, regionReportDto);
        return ApiResponse.onSuccess("설정값 변경이 완료되었습니다.");
    }

    // 직장 지역 설정
    @PutMapping("/region")
    public ApiResponse<String> updateRegion(@AuthUser User user, @RequestBody SettingReqDto.RegionDto regionDto) {
        settingService.updateRegion(user, regionDto);
        return ApiResponse.onSuccess("설정값 변경이 완료되었습니다.");
    }

    // 사용자 가중치 설정
    @PutMapping("/user-weight")
    public ApiResponse<String> updateWeight(@AuthUser User user, @RequestBody SettingReqDto.WeightDto weightDto) {
        settingService.updateWeight(user, weightDto);
        return ApiResponse.onSuccess("설정값 변경이 완료되었습니다.");
    }

}
