package com.stayease.api.serviceImpls;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import com.stayease.api.dto.UpdateUserDto;
import com.stayease.api.dto.UserResponse;
import com.stayease.api.entity.User;
import com.stayease.api.enumeration.Role;
import com.stayease.api.repository.UserRepository;
import com.stayease.api.security.JwtUserDetails;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private UserServiceImpl userService;

    private User mockUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setEmail("test@example.com");
        mockUser.setFirstName("John");
        mockUser.setLastName("Doe");
        mockUser.setRole(Role.CUSTOMER);
    }

    @Test
    void testLoadUserByEmail() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(mockUser));

        UserDetails userDetails = userService.loadUserByEmail("test@example.com");

        assertNotNull(userDetails);
        assertEquals(mockUser.getEmail(), userDetails.getUsername());
        verify(userRepository, times(1)).findByEmail("test@example.com");
    }

    @Test
    void testLoadUserById() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));

        UserDetails userDetails = userService.loadUserById(1L);

        assertNotNull(userDetails);
        assertEquals(mockUser.getId(), ((JwtUserDetails) userDetails).getId());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void testFindById() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));

        User user = userService.findById(1L);

        assertNotNull(user);
        assertEquals(mockUser.getId(), user.getId());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void testGetUser() {
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(JwtUserDetails.create(mockUser));
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));

        User user = userService.getUser();

        assertNotNull(user);
        assertEquals(mockUser.getId(), user.getId());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void testFindByEmail() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(mockUser));

        User user = userService.findByEmail("test@example.com");

        assertNotNull(user);
        assertEquals(mockUser.getEmail(), user.getEmail());
        verify(userRepository, times(1)).findByEmail("test@example.com");
    }

    @Test
    void testGetAllUsers() {
        List<User> mockUsers = Arrays.asList(mockUser);
        when(userRepository.findAll()).thenReturn(mockUsers);

        List<UserResponse> userResponses = userService.getAllUsers();

        assertNotNull(userResponses);
        assertEquals(1, userResponses.size());
        assertEquals(mockUser.getEmail(), userResponses.get(0).getEmail());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void testUpdateProfile() {
        UpdateUserDto updateUserDto = new UpdateUserDto();
        updateUserDto.setFirstName("Jane");
        updateUserDto.setLastName("Smith");

        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(JwtUserDetails.create(mockUser));
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));

        userService.updateProfile(updateUserDto);

        assertEquals("Jane", mockUser.getFirstName());
        assertEquals("Smith", mockUser.getLastName());
        verify(userRepository, times(1)).save(mockUser);
    }

    @Test
    void testGetProfile() {
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(JwtUserDetails.create(mockUser));
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));

        UserResponse profile = userService.getProfile();

        assertNotNull(profile);
        assertEquals(mockUser.getEmail(), profile.getEmail());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void testDeleteUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));

        userService.deleteUser(1L);

        verify(userRepository, times(1)).delete(mockUser);
    }
}
