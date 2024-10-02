package com.waither.domain.user.controller;

import com.waither.domain.user.dto.request.UserReqDto;
import com.waither.domain.user.entity.User;
import com.waither.domain.user.service.commandService.UserService;
import com.waither.global.jwt.annotation.CurrentUser;
import com.waither.global.jwt.dto.JwtDto;
import com.waither.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/user")
public class UserController {

    private final UserService userService;

    // 회원가입
    @Operation(summary = "Sign Up", description = "회원가입")
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<String>> signup(@Valid @RequestBody UserReqDto.SignUpRequestDto requestDto) {
        userService.signup(requestDto);
        // 201 Created 사용
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        ApiResponse.onSuccess(HttpStatus.CREATED, "회원가입이 성공적으로 완료되었습니다.")
                );
    }

    // Jwt 토큰 재발급
    @GetMapping("/reissue")
    public ApiResponse<JwtDto> reissueToken(@RequestHeader("RefreshToken") String refreshToken) {
        JwtDto jwtDto = userService.reissueToken(refreshToken);
        return ApiResponse.onSuccess(jwtDto);
    }

    // 이메일에 인증번호 보내기
    @GetMapping("/emails/submit-authcode")
    public ApiResponse<String> submitAuthCode(@RequestParam String email) {
            userService.sendAuthCodeToEmail(email);
            return ApiResponse.onSuccess("인증번호 전송에 성공했습니다.");
    }

    // 이메일 인증하기
    @PostMapping("/emails/verifications")
    public ApiResponse<String> verificationEmail(@RequestBody UserReqDto.EmailVerificationDto verificationDto) {
        userService.verifyCode(verificationDto);
        return ApiResponse.onSuccess("이메일 인증에 성공했습니다.");
    }

    // 임시 비밀번호 발급
    @GetMapping("/emails/temporary-password")
    public ApiResponse<String> submitTemporaryPassword(@RequestParam String email) {
            String tempPassword = userService.sendTempPassword(email);
            userService.changeToTempPassword(email, tempPassword);
            return ApiResponse.onSuccess("인증번호 전송에 성공했습니다.");
    }

    // 닉네임 변경
    @PutMapping("/nickname")
    public ApiResponse<String> updateNickname(@CurrentUser User currentUser,
                                              @RequestBody UserReqDto.NicknameDto nicknameDto) {
        userService.updateNickname(currentUser, nicknameDto);
        return ApiResponse.onSuccess("닉네임이 " + nicknameDto.nickname() + "로 바뀌었습니다.");
    }

    // 비밀번호 확인
    @PostMapping("/password-check")
    public ApiResponse<String> passwordCheckEmail(@CurrentUser User currentUser,
                                                  @RequestBody UserReqDto.PasswordCheckDto passwordCheckDto) {
            userService.checkPassword(currentUser, passwordCheckDto);
        return ApiResponse.onSuccess("비밀번호가 확인되었습니다.");
    }

    // 비밀번호 변경
    @PutMapping("/password")
    public ApiResponse<String> updatePassword(@CurrentUser User currentUser,
                                              @Valid @RequestBody UserReqDto.UpdatePasswordDto updatePasswordDto) {
        userService.updatePassword(currentUser, updatePasswordDto.password());
        return ApiResponse.onSuccess("비밀번호가 변경되었습니다.");
    }


    // Todo : soft delete로 변경 고려
    @DeleteMapping("/delete")
    public ApiResponse<String> deleteUser(@CurrentUser User currentUser) {
        userService.deleteUser(currentUser);
        return ApiResponse.onSuccess(currentUser.getEmail() + "님의 계정이 성공적으로 탈퇴되었습니다.");
    }


    //Swagger용 가짜 컨트롤러
    @PostMapping("/login")
    public ApiResponse<JwtDto> login(@RequestBody UserReqDto.LoginRequestDto loginRequestDto) {
        return null;
    }

    //Swagger용 가짜 컨트롤러
    @PostMapping("/logout")
    public ApiResponse<?> logout() {
        return null;
    }

}
