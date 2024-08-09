package com.waither.global.jwt.userdetails;

import com.waither.domain.user.entity.User;
import com.waither.domain.user.repository.UserRepository;
import com.waither.global.exception.CustomException;
import com.waither.global.response.UserErrorCode;
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
        //Custom Exception 던져질 수 있는지??
        throw new CustomException(UserErrorCode.USER_NOT_FOUND);
    }
}