package com.waither.global.jwt.userdetails;

import com.waither.userservice.entity.User;
import com.waither.userservice.global.exception.CustomException;
import com.waither.userservice.global.response.ErrorCode;
import com.waither.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PrincipalDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        Optional<User> userEntity = userRepository.findByEmail(email);
        if (userEntity.isPresent()) {
            User user = userEntity.get();
            return new PrincipalDetails(user.getEmail(),user.getPassword(), user.getRole());
        }
        throw new CustomException(ErrorCode.USER_NOT_FOUND);
    }
}