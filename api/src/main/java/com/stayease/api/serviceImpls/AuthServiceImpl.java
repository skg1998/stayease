package com.stayease.api.serviceImpls;

import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import com.stayease.api.dto.LoginDto;
import com.stayease.api.dto.RegisterDto;
import com.stayease.api.dto.TokenExpiresInResponse;
import com.stayease.api.dto.TokenResponse;
import com.stayease.api.entity.User;
import com.stayease.api.enumeration.Role;
import com.stayease.api.exceptions.NotFoundException;
import com.stayease.api.repository.UserRepository;
import com.stayease.api.security.JwtTokenProvider;
import com.stayease.api.security.JwtUserDetails;
import com.stayease.api.services.AuthService;
import com.stayease.api.services.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService{
	private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;

    @Override
    public void register(RegisterDto registerDto) throws BindException {
    	BindingResult bindingResult = new BeanPropertyBindingResult(registerDto, "request");
		
		userRepository.findByEmail(registerDto.getEmail())
        .ifPresent(user -> {
            log.error("User with email: {} already exists", registerDto.getEmail());
            bindingResult.addError(new FieldError(bindingResult.getObjectName(), "email", "unique_email"));
        });
		
		if (bindingResult.hasErrors()) {
            throw new BindException(bindingResult);
        }

        User user = new User();
        user.setEmail(registerDto.getEmail());
        user.setPassword(passwordEncoder.encode(registerDto.getPassword()));
        user.setFirstName(registerDto.getFirstName());
        user.setLastName(registerDto.getLastName());
        user.setRole(registerDto.getRole());

        userRepository.save(user);
        log.info("User registered successfully with email: {}", user.getEmail());
    }

    @Override
    public TokenResponse login(LoginDto loginDto) {
        // Manually create the token and authenticate using an injected AuthenticationManager bean if needed
        String email = loginDto.getEmail();
        String password = loginDto.getPassword();

        log.info("Login request received: {}", email);

        String badCredentialsMessage = "bad_credentials";
        Role role = null;
        try {
            User user = userService.findByEmail(email);
            role = user.getRole();
        } catch (NotFoundException e) {
            log.error("User not found with email: {}", email);
            throw new AuthenticationCredentialsNotFoundException(badCredentialsMessage);
        }

        log.info("Login request now {}", email, password);
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(email, password);
        try {
            Authentication authentication = authenticationManager.authenticate(authenticationToken);
            JwtUserDetails jwtUserDetails = jwtTokenProvider.getPrincipal(authentication);
            return generateTokens(jwtUserDetails.getId(), role.name());
        } catch (NotFoundException e) {
            log.error("Authentication failed for email: {}", email);
            throw new AuthenticationCredentialsNotFoundException(badCredentialsMessage);
        }
    }
    
    private TokenResponse generateTokens(Long id, String role) {
		String token = jwtTokenProvider.generateJwt(id);

        log.info("Token generated for user: {}", id);

        return TokenResponse.builder()
        	.userId(id)
        	.role(role)
            .token(token)
            .expiresIn(
                TokenExpiresInResponse.builder()
                    .token(jwtTokenProvider.getTokenExpiresIn())
                    .refreshToken(jwtTokenProvider.getRefreshTokenExpiresIn())
                    .build()
            )
            .build();
	}
}
