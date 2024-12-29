package com.stayease.api.serviceImpls;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindException;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import com.stayease.api.dto.LoginDto;
import com.stayease.api.dto.RegisterDto;
import com.stayease.api.dto.TokenResponse;
import com.stayease.api.entity.User;
import com.stayease.api.enumeration.Role;
import com.stayease.api.exceptions.NotFoundException;
import com.stayease.api.repository.UserRepository;
import com.stayease.api.security.JwtTokenProvider;
import com.stayease.api.security.JwtUserDetails;
import com.stayease.api.services.UserService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

class AuthServiceImplTest {

    @InjectMocks
    private AuthServiceImpl authService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegisterSuccess() throws BindException {
        RegisterDto registerDto = new RegisterDto("test@example.com", "password", "John", "Doe", Role.CUSTOMER);
        when(userRepository.findByEmail(registerDto.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(registerDto.getPassword())).thenReturn("encoded_password");

        authService.register(registerDto);

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testRegisterEmailAlreadyExists() {
        RegisterDto registerDto = new RegisterDto("test@example.com", "password", "John", "Doe", Role.CUSTOMER);
        when(userRepository.findByEmail(registerDto.getEmail())).thenReturn(Optional.of(new User()));

        BindException exception = assertThrows(BindException.class, () -> authService.register(registerDto));

        assertTrue(exception.getBindingResult().hasFieldErrors("email"));
    }

    @Test
    void testLoginSuccess() {
        LoginDto loginDto = new LoginDto("test@example.com", "password");
        User user = new User();
        user.setId(1L);
        user.setRole(Role.CUSTOMER);

        JwtUserDetails jwtUserDetails = JwtUserDetails.create(user);

        when(userService.findByEmail(loginDto.getEmail())).thenReturn(user);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(mock(Authentication.class));
        when(jwtTokenProvider.getPrincipal(any(Authentication.class))).thenReturn(jwtUserDetails);
        when(jwtTokenProvider.generateJwt(user.getId())).thenReturn("token");
        when(jwtTokenProvider.getTokenExpiresIn()).thenReturn(3600L);

        TokenResponse response = authService.login(loginDto);

        assertEquals("token", response.getToken());
        assertEquals(1L, response.getUserId());
        assertEquals("CUSTOMER", response.getRole());
    }


    @Test
    void testLoginUserNotFound() {
        LoginDto loginDto = new LoginDto("test@example.com", "password");
        when(userService.findByEmail(loginDto.getEmail())).thenThrow(new NotFoundException("User not found"));

        AuthenticationCredentialsNotFoundException exception = assertThrows(AuthenticationCredentialsNotFoundException.class, () -> authService.login(loginDto));

        assertEquals("bad_credentials", exception.getMessage());
    }

    @Test
    void testLoginAuthenticationFailure() {
        LoginDto loginDto = new LoginDto("test@example.com", "password");
        User user = new User();
        user.setId(1L);
        user.setRole(Role.CUSTOMER);

        when(userService.findByEmail(loginDto.getEmail())).thenReturn(user);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenThrow(new NotFoundException("Authentication failed"));

        AuthenticationCredentialsNotFoundException exception = assertThrows(AuthenticationCredentialsNotFoundException.class, () -> authService.login(loginDto));

        assertEquals("bad_credentials", exception.getMessage());
    }
}
