package com.stayease.api.security;

import java.util.List;
import java.util.Objects;

import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.stayease.api.entity.User;
import com.stayease.api.services.UserService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class CustomAuthenticationManager implements AuthenticationManager {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    /**
     * Authenticate user.
     *
     * @param authentication Authentication
     */
    @Override
    @Transactional
    public Authentication authenticate(final Authentication authentication) throws AuthenticationException {
        User user = userService.findByEmail(authentication.getName());

        if (Objects.nonNull(authentication.getCredentials())) {
            boolean matches = passwordEncoder.matches(authentication.getCredentials().toString(), user.getPassword());
            if (!matches) {
                log.error("AuthenticationCredentialsNotFoundException occurred for {}", authentication.getName());
                throw new AuthenticationCredentialsNotFoundException("bad_credentials");
            }
        }

        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(user.getRole().name());
        UserDetails userDetails = userService.loadUserByEmail(authentication.getName());
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(userDetails,
            user.getPassword(), List.of(authority));
        SecurityContextHolder.getContext().setAuthentication(auth);

        return auth;
    }
}