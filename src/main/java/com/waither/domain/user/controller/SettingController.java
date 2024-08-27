package com.waither.domain.user.controller;

import com.waither.domain.user.dto.request.SettingReqDto;
import com.waither.domain.user.dto.response.SettingResDto;
import com.waither.domain.user.entity.User;
import com.waither.domain.user.service.commandService.SettingService;
import com.waither.domain.user.service.queryService.SettingQueryService;
import com.waither.global.jwt.annotation.CurrentUser;
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
    public ApiResponse<SettingResDto.CustomDto> getUserCustom(@CurrentUser User currentUser) {
        return ApiResponse.onSuccess(settingQueryService.getUserCustom(currentUser));
    }

    // 직장 지역 설정 조회
    @GetMapping("/region")
    public ApiResponse<SettingResDto.RegionDto> getRegion(@CurrentUser User currentUser) {
        return ApiResponse.onSuccess(settingQueryService.getRegion(currentUser));
    }

    // 알림 설정 조회
    @GetMapping("/noti")
    public ApiResponse<SettingResDto.NotificationDto> getNotification(@CurrentUser User currentUser) {
        return ApiResponse.onSuccess(settingQueryService.getNotification(currentUser));
    }

    // 바람 세기 설정 조회
    @GetMapping("/noti/wind")
    public ApiResponse<SettingResDto.WindDto> getWind(@CurrentUser User currentUser) {
        return ApiResponse.onSuccess(settingQueryService.getWind(currentUser));
    }

    // 메인 화면 날씨 상세 정보 조회
    @GetMapping("/display")
    public ApiResponse<SettingResDto.DisplayDto> getDisplay(@CurrentUser User currentUser) {
        return ApiResponse.onSuccess(settingQueryService.getDisplay(currentUser));
    }

    // 사용자 가중치 설정 조회
    @GetMapping("/user-weight")
    public ApiResponse<SettingResDto.WeightDto> getWeight(@CurrentUser User currentUser) {
        return ApiResponse.onSuccess(settingQueryService.getWeight(currentUser));
    }

    // 마이페이지 조회
    @GetMapping("/mypage")
    public ApiResponse<SettingResDto.UserInfoDto> getUserInfo(@CurrentUser User currentUser) {
        return ApiResponse.onSuccess(settingQueryService.getUserInfo(currentUser));
    }

    /* --------- Update (Put) --------- */

    // 사용자 맞춤 서비스 제공
    @PutMapping("/custom")
    public ApiResponse<String> updateCustom(@CurrentUser User currentUser, @RequestBody SettingReqDto.CustomDto customDto) {
        settingService.updateCustom(currentUser, customDto);
        return ApiResponse.onSuccess("설정값 변경이 완료되었습니다.");
    }

    // 메인 화면 날씨 상세 정보 보기 (강수량, 퓽향/풍속, 미세먼지)
    @PatchMapping("/display")
    public ApiResponse<String> updateDisplay(@CurrentUser User currentUser, @RequestBody SettingReqDto.DisplayDto displayDto) {
        settingService.updateDisplay(currentUser, displayDto);
        return ApiResponse.onSuccess(
                "설정값 변경이 완료되었습니다.");
    }

    // 외출 알림 설정
    @PutMapping("/noti/out-alert")
    public ApiResponse<String> updateOutAlert(@CurrentUser User currentUser, @RequestBody SettingReqDto.OutAlertDto outAlertDto) {
        settingService.updateOutAlert(currentUser, outAlertDto);
        return ApiResponse.onSuccess("설정값 변경이 완료되었습니다.");
    }

    // 알림 설정 (요일 & 시간)
    @PutMapping("/noti/out-alert-set")
    public ApiResponse<String> updateOutAlertSet(@CurrentUser User currentUser, @RequestBody SettingReqDto.OutAlertSetDto outAlertSetDto) {
        settingService.updateOutAlertSet(currentUser, outAlertSetDto);
        return ApiResponse.onSuccess("설정값 변경이 완료되었습니다.");
    }

    // 기상 특보 알림 설정
    @PutMapping("/noti/climate-alert")
    public ApiResponse<String> updateClimateAlert(@CurrentUser User currentUser, @RequestBody SettingReqDto.ClimateAlertDto climateAlertDto) {
        settingService.updateClimateAlert(currentUser, climateAlertDto);
        return ApiResponse.onSuccess("설정값 변경이 완료되었습니다.");
    }

    // 사용자 맞춤 예보 설정
    @PutMapping("/noti/user-alert")
    public ApiResponse<String> updateUserAlert(@CurrentUser User currentUser, @RequestBody SettingReqDto.UserAlertDto userAlertDto) {
        settingService.updateUserAlert(currentUser, userAlertDto);
        return ApiResponse.onSuccess("설정값 변경이 완료되었습니다.");
    }

    // 강설 정보 알림 설정
    @PutMapping("/noti/snow-alert")
    public ApiResponse<String> updateSnowAlert(@CurrentUser User currentUser, @RequestBody SettingReqDto.SnowAlertDto snowAlertDto) {
        settingService.updateSnowAlert(currentUser, snowAlertDto);
        return ApiResponse.onSuccess("설정값 변경이 완료되었습니다.");
    }

    // 바람 세기 알림
    @PutMapping("/noti/wind")
    public ApiResponse<String> updateWind(@CurrentUser User currentUser, @RequestBody SettingReqDto.WindDto windDto) {
        settingService.updateWind(currentUser, windDto);
        return ApiResponse.onSuccess(
                "설정값 변경이 완료되었습니다.");
    }

    // 직장 지역 레포트 알림 받기
    @PutMapping("/region-report")
    public ApiResponse<String> updateRegionReport(@CurrentUser User currentUser, @RequestBody SettingReqDto.RegionReportDto regionReportDto) {
        settingService.updateRegionReport(currentUser, regionReportDto);
        return ApiResponse.onSuccess("설정값 변경이 완료되었습니다.");
    }

    // 직장 지역 설정
    @PutMapping("/region")
    public ApiResponse<String> updateRegion(@CurrentUser User currentUser, @RequestBody SettingReqDto.RegionDto regionDto) {
        settingService.updateRegion(currentUser, regionDto);
        return ApiResponse.onSuccess("설정값 변경이 완료되었습니다.");
    }

    // 사용자 가중치 설정
    @PutMapping("/user-weight")
    public ApiResponse<String> updateWeight(@CurrentUser User currentUser, @RequestBody SettingReqDto.WeightDto weightDto) {
        settingService.updateWeight(currentUser, weightDto);
        return ApiResponse.onSuccess("설정값 변경이 완료되었습니다.");
    }

}
