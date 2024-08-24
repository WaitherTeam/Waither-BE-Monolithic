package com.waither.annotation;

import com.waither.domain.user.entity.User;
import com.waither.domain.user.repository.UserRepository;
import com.waither.global.jwt.userdetails.CustomUserDetailsService;
import org.mockito.Mockito;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.util.Optional;

public class WithMockUserSecurityContextFactory implements WithSecurityContextFactory<WithMockUser> {
    @Override
    public SecurityContext createSecurityContext(WithMockUser mockUser) {
        String username = mockUser.email();

        UserRepository userRepository = Mockito.mock(UserRepository.class);
        Mockito.when(userRepository.findByEmail(Mockito.anyString()))
                .thenAnswer(invocation -> {
                    String email = invocation.getArgument(0);
                    if (email.equals(mockUser.email())) {
                        return Optional.of(new User(mockUser.email(), null, mockUser.role()));
                    }
                    return Optional.empty();
                });

        CustomUserDetailsService userDetailsService = new CustomUserDetailsService(userRepository);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(new UsernamePasswordAuthenticationToken(userDetails,
                userDetails.getPassword(), userDetails.getAuthorities()));
        return securityContext;
    }
}
