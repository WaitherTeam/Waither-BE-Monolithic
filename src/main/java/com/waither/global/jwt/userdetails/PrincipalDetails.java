package com.waither.global.jwt.userdetails;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class PrincipalDetails implements UserDetails {

    private final String email;
    private final String password;
    private final String roles;


    // 해당 User의 권한을 리턴 하는 곳
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(roles));
        return authorities;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    // 계정 만료되지 않음
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    // 계정 잠겨있지 않음
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    // 비밀번호 갈아낄 때 되지 않음
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    // Todo : 1년 간 로그인 없을 시 "휴면"으로
    //  (현재 시간 - loginDate) > 1년 -> return false; 로 설정
    // 활성화 되어있음
    @Override
    public boolean isEnabled() {
        // 사이트에서 1년 동안 회원이 로그인을 안하면 -> 휴면 계정으로 전환하는 로직이 있다고 치자
        // user entity의 field에 "Timestamp loginDate"를 하나 만들어주고
        // (현재 시간 - loginDate) > 1년 -> return false; 로 설정
        return true;
    }
}
