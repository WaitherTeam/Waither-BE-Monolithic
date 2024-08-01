package com.waither.domain.user.entity;

import com.waither.userservice.entity.enums.UserStatus;
import com.waither.userservice.entity.type.AuthType;
import com.waither.userservice.global.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "user")
@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // OAuth ID
    @Column(name = "auth_id")
    private Long authId;

    // 유저 이메일
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    // 유저 비밀번호
    @Column(name = "password")
    private String password;

    // 유저 닉네임
    @Column(name = "nickname", nullable = false)
    private String nickname;

    // 회원 가입 타입
    @Enumerated(EnumType.STRING)
    private AuthType authType;

    // 유저 상태 (active / 휴면 / 탈퇴 등)
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private UserStatus status;

    // 권한
    @Column(name = "role", nullable = false)
    private String role; //ROLE_USER or ROLE_ADMIN

    // Todo: OAuth 에서 이미지 가져오기
    // 프로필 이미지
    private String image;

    // 사용자 맞춤 서비스 허용 여부
    @Column(name = "custom", nullable = false)
    private boolean custom;

    // Mapping
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "setting_id", unique = true)
    private Setting setting;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserData> UserData;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserMedian> UserMedian;

    // 비밀번호 변경
    public void setPassword(String password) {
        this.password = password;
    }

    // 닉네임 변경
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    // 사용자 맞춤형 데이터 변경
    public void setCustom(boolean custom) {
        this.custom = custom;
    }


    // 연관관계 설정
    public void setSetting(Setting setSetting) {
        setting = setSetting;
    }

    public void setUserData(List<UserData> userData) {
        UserData = userData;
    }

    public void setUserMedian(List<UserMedian> userMedian) {
        UserMedian = userMedian;
    }
}
