package com.waither.domain.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class UserReqDto {
    public record LoginRequestDto(
            @NotBlank(message = "[ERROR] 이메일 입력은 필수입니다.")
            @Schema(description = "email", example = "test@email.com")
            @Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+.[A-Za-z]{2,6}$", message = "[ERROR] 이메일 형식에 맞지 않습니다.")
            String email,

            @NotBlank(message = "[ERROR] 비밀번호 입력은 필수 입니다.")
            @Schema(description = "email", example = "test1234!!")
            @Size(min = 8, message = "[ERROR] 비밀번호는 최소 8자리 이이어야 합니다.")
            @Pattern(regexp = "^(?=.*[a-z])(?=.*\\d)(?=.*[!@#$%^&*]).{8,64}$", message = "[ERROR] 비밀번호는 8자 이상, 64자 이하이며 특수문자 한 개를 포함해야 합니다.")
            String password
    ) { }

    // 로그인과 같은 형식이지만, 확장성(회원가입 할 떄 추가 정보를 받을 수도 있음)을 위해 나누어 놓았습니다.
    public record SignUpRequestDto(
            @NotBlank(message = "[ERROR] 이메일 입력은 필수입니다.")
            @Schema(description = "email", example = "test@email.com")
            @Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+.[A-Za-z]{2,6}$", message = "[ERROR] 이메일 형식에 맞지 않습니다.")
            String email,

            @NotBlank(message = "[ERROR] 비밀번호 입력은 필수 입니다.")
            @Schema(description = "email", example = "test1234!!")
            @Size(min = 8, message = "[ERROR] 비밀번호는 최소 8자리 이이어야 합니다.")
            @Pattern(regexp = "^(?=.*[a-z])(?=.*\\d)(?=.*[!@#$%^&*]).{8,64}$", message = "[ERROR] 비밀번호는 8자 이상, 64자 이하이며 특수문자 한 개를 포함해야 합니다.")
            String password
    ) { }

    public record EmailVerificationDto(
            String email,
            String authCode
    ) { }

    public record NicknameDto(
            String nickname
    ) { }

    public record PasswordCheckDto(
            String password
    ) { }

    public record UpdatePasswordDto(
            @NotBlank(message = "[ERROR] 비밀번호 입력은 필수 입니다.")
            @Size(min = 8, message = "[ERROR] 비밀번호는 최소 8자리 이이어야 합니다.")
            @Schema(description = "email", example = "test1234!!")
            @Pattern(regexp = "^(?=.*[a-z])(?=.*\\d)(?=.*[!@#$%^&*]).{8,64}$", message = "[ERROR] 비밀번호는 8자 이상, 64자 이하이며 특수문자 한 개를 포함해야 합니다.")
            String password
    ) { }

}
