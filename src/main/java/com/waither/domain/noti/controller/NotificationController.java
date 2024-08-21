package com.waither.domain.noti.controller;

import com.waither.domain.noti.dto.request.LocationDto;
import com.waither.domain.noti.service.NotificationService;
import com.waither.domain.user.entity.User;
import com.waither.global.jwt.annotation.CurrentUser;
import com.waither.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;


@RequiredArgsConstructor
@RequestMapping("/noti")
@RestController
public class NotificationController {

    private final NotificationService notificationService;


    @Operation(summary = "Get notification", description = "알림 목록 조회하기")
    @GetMapping("")
    public ApiResponse<?> getNotifications(@CurrentUser User currentUser,
                                           @PageableDefault(page = 0, size = 10, sort = "createdAt") Pageable pageable) {
        return ApiResponse.onSuccess(notificationService.getNotifications(currentUser, pageable));
    }

    @Operation(summary = "Delete notification", description = "알림 삭제하기")
    @DeleteMapping("")
    public ApiResponse<?> deleteNotification(@CurrentUser User currentUser, @RequestParam("id") String notificationId) {
        notificationService.deleteNotification(currentUser, notificationId);
        return ApiResponse.onSuccess(HttpStatus.OK);
    }

    @Operation(summary = "Send Go Out Alarm", description = "외출 알림 전송하기")
    @PostMapping("/goOut")
    public ApiResponse<?> sendGoOutAlarm(@CurrentUser User currentUser, @RequestBody @Valid LocationDto location) {
        notificationService.sendGoOutAlarm(currentUser, location);
        return ApiResponse.onSuccess(HttpStatus.OK);
    }

    @Operation(summary = "Current Location", description = "현재 위치 전송")
    @PostMapping("/location")
    public ApiResponse<?> updateLocation(@CurrentUser User currentUser, @RequestBody @Valid LocationDto locationDto) {
        notificationService.updateLocation(currentUser, locationDto);
        return ApiResponse.onSuccess(HttpStatus.OK);
    }

}
