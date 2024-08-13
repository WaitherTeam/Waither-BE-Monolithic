package com.waither.domain.user.service.commandService;

import com.waither.domain.user.converter.RegionConverter;
import com.waither.domain.user.converter.SettingConverter;
import com.waither.domain.user.converter.SurveyConverter;
import com.waither.domain.user.converter.UserConverter;
import com.waither.domain.user.dto.converter.AccountConverter;
import com.waither.domain.user.dto.request.UserReqDto;
import com.waither.domain.user.dto.response.KakaoResDto;
import com.waither.domain.user.entity.*;
import com.waither.domain.user.entity.enums.Season;
import com.waither.domain.user.repository.UserRepository;
import com.waither.global.exception.CustomException;
import com.waither.global.jwt.dto.JwtDto;
import com.waither.global.jwt.userdetails.PrincipalDetails;
import com.waither.global.jwt.util.JwtUtil;
import com.waither.global.response.UserErrorCode;
import com.waither.global.utils.RedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private static final String AUTH_CODE_PREFIX = "AuthCode_";
    private static final String VERIFIED_PREFIX = "Verified_";

    private final EmailService emailService;

    private final RedisUtil redisUtil;
    private final JwtUtil jwtUtil;

    @Value("${spring.mail.auth-code-expiration-millis}")
    private long authCodeExpirationMillis;

    // 회원가입
    public void signup(UserReqDto.SignUpRequestDto requestDto) {
        // Todo : 다시 풀기
//        if (!verifiedAccounts(requestDto.email())) {
//            throw new CustomException(ErrorCode.INVALID_Account);
//        }
        User newUser = UserConverter.toUser(requestDto, passwordEncoder);
        UserRegion newUserRegion = RegionConverter.createRegion();
        Setting newSetting = SettingConverter.createSetting();

        // 모든 계절에 대한 UserData 생성
        List<UserData> userDataList = Arrays.stream(Season.values())
                .map(season -> {
                    UserData userData = SurveyConverter.createUserData(season);
                    userData.setUser(newUser);
                    return userData;
                })
                .toList();

        // 모든 계절에 대한 UserMedian 생성
        List<UserMedian> userMedianList = userDataList.stream()
                .map(userData -> {
                    UserMedian userMedian = SurveyConverter.createUserMedian(userData);
                    userMedian.setUser(newUser);
                    return userMedian;
                })
                .toList();

        // 연관관계 설정
        newSetting.setUserRegion(newUserRegion);
        newUser.setSetting(newSetting);
        newUser.setUserData(userDataList);
        newUser.setUserMedian(userMedianList);

        // 초기값 Kafka 전송 -> 삭제
        userRepository.save(newUser);
    }

    // 회원가입 (카카오)
    public void signup(KakaoResDto.UserInfoResponseDto userInfo) {

        User newUser = AccountConverter.toUser(userInfo);

        Setting defaultSetting = deafultSettings();
        UserRegion defaultUserRegion = deafultRegion();

        defaultSetting.setUserRegion(defaultUserRegion);
        newUser.setSetting(defaultSetting);
        userRepository.save(newUser);
    }

    // OAuth용 토큰 발급
    public JwtDto provideTokenForOAuth(String email) {
        PrincipalDetails principalDetails = new PrincipalDetails(email, null, "ROLE_USER");
        return new JwtDto(
                jwtUtil.createJwtAccessToken(principalDetails),
                jwtUtil.createJwtRefreshToken(principalDetails));
    }

    // 재발급
    public JwtDto reissueToken(String refreshToken) {
        jwtUtil.isRefreshToken(refreshToken);
        return jwtUtil.reissueToken(refreshToken);
    }
    // 인증 번호 전송

    public void sendAuthCodeToEmail(String email) {
        this.checkDuplicatedEmail(email);

        String authCode = this.createAuthCode();
        String title = "[☀️Waither☀️] 이메일 인증 번호 : {" + authCode + "}";
        emailService.sendEmail(email, title, "authEmail", authCode);

        log.info(email);
        // 이메일 인증 요청 시 인증 번호 Redis에 저장 ( key = "AuthCode " + Email / value = AuthCode )
        redisUtil.save(AUTH_CODE_PREFIX + email,
                authCode, authCodeExpirationMillis, TimeUnit.MILLISECONDS);
    }
    // 임시 비밀번호 보내기

    public String sendTempPassword(String email) {
        // 전송
        String tempPassword = this.createTemporaryPassword();
        String title = "[☀️Waither☀️] 임시 비밀번호 : {" + tempPassword + "}";
        emailService.sendEmail(email, title, "tempPasswordEmail", tempPassword);

        return tempPassword;
    }
    // 회원가입 하려는 이메일로 이미 가입한 회원이 있는지 확인하는 메서드.
    // 만약 해당 이메일을 가진 회원이 존재하면 예외를 발생.

    private void checkDuplicatedEmail(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent()) {
            throw new CustomException(UserErrorCode.USER_ALREADY_EXIST);
        }
    }
    public boolean isUserRegistered(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        return user.isPresent();
    }

    // 회원 존재하는 지 확인

    public void checkUserExists(String email) {
        if (!userRepository.existsByEmail(email)) {
            throw new CustomException(UserErrorCode.USER_NOT_FOUND);
        }
    }
    // 인증 번호 생성하는 메서드

    public String createAuthCode() {
        int lenth = 6;
        try {
            Random random = SecureRandom.getInstanceStrong();
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < lenth; i++) {
                builder.append(random.nextInt(10));
            }
            return builder.toString();
        } catch (NoSuchAlgorithmException e) {
            log.debug("MemberService.createCode() exception occur");
            throw new CustomException(UserErrorCode.NO_SUCH_ALGORITHM);
        }
    }
    //랜덤함수로 임시비밀번호 구문 만들기

    public String createTemporaryPassword() {
        char[] charSet = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F',
                'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z' };
        Random random = new Random();

        // 문자 세트에서 랜덤으로 10개의 문자를 선택하여 하나의 문자열로 결합
        return random.ints(10, 0, charSet.length)
                .mapToObj(idx -> String.valueOf(charSet[idx]))
                .collect(Collectors.joining());
    }
    // 인증 코드를 검증하는 메서드.

    public void verifyCode(String email, String authCode) {
        this.checkDuplicatedEmail(email);

        // 이메일 인증 요청하지 않았거나, 이메일 인증 요청한 후 유효기간 경과한 경우
        if (!(redisUtil.hasKey(AUTH_CODE_PREFIX + email))) {
            throw new CustomException(UserErrorCode.AUTH_CODE_EXPIRED);
        }
        // 인증 번호 일치
        else if (redisUtil.get(AUTH_CODE_PREFIX + email).toString().equals(authCode)) {
            // 인증 성공 시 인증된 상태로 변경
            redisUtil.save(VERIFIED_PREFIX + email,
                    "verifiedAccounts", authCodeExpirationMillis, TimeUnit.MILLISECONDS);
        }
        // 그 외의 경우, 코드 잘못됨.
        else {
            throw new CustomException(UserErrorCode.INVALID_CODE);
        }
    }
    // signup 과정 전에 인증된 email인지 확인하는 메서드

    public boolean verifiedAccounts(String email) {
        // 인증하지 않았거나, 인증 완료까지는 했지만 너무 시간이 경과한 경우
        if(!redisUtil.hasKey(VERIFIED_PREFIX + email)) {
            throw new CustomException(UserErrorCode.AUTH_CODE_EXPIRED);
        }
        // hasKey(VERIFIED_PREFIX + email) 만 통과 하면 -> 인증 완료한 것
        return true;
    }
    // 임시 비밀번호로 비밀번호를 변경

    public void changeToTempPassword(String email, String tempPassword) {
        // 이메일로 사용자 조회
        userRepository.findByEmail(email).get().setPassword(passwordEncoder.encode(tempPassword));
    }
    // 현재 비밀번호 체크

    public void checkPassword(User user, String currentPassword) {
        // 현재 비밀번호가 일치하는지 확인
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new CustomException(UserErrorCode.CURRENT_PASSWORD_NOT_EQUAL);
        }
    }
    // 비밀번호 변경

    public void updatePassword(User user, String newPassword) {
        if (passwordEncoder.matches(newPassword, user.getPassword())) {
            throw new CustomException(UserErrorCode.CURRENT_PASSWORD_EQUAL);
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
    // 닉네임 변경

    public void updateNickname(User user, String nickanme) {
        user.setNickname(nickanme);

        // Kafka 전송 -> 삭제

        userRepository.save(user);
    }
    // 회원 삭제

    public void deleteUser(User user){
        userRepository.delete(user);
    }

    private Setting deafultSettings() {
        return Setting.builder()
                .climateAlert(true)
                .userAlert(true)
                .snowAlert(true)
                .windAlert(true)
                .windDegree(10)
                .regionReport(true)
                .precipitation(true)
                .wind(true)
                .dust(true)
                .weight(0.0)
                .build();
    }

    private UserRegion deafultRegion() {
        return UserRegion.builder()
                .regionName("서울시")
                .longitude(37.5665)
                .latitude(126.9780)
                .build();
    }
}
