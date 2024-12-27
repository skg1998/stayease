package com.stayease.api.serviceImpls;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.stayease.api.dto.UpdateUserDto;
import com.stayease.api.dto.UserResponse;
import com.stayease.api.entity.User;
import com.stayease.api.exceptions.NotFoundException;
import com.stayease.api.repository.UserRepository;
import com.stayease.api.security.JwtUserDetails;
import com.stayease.api.services.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found with email: " + email));
        return JwtUserDetails.create(user);
    }

    @Override
    public UserDetails loadUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found with ID: " + id));
        return JwtUserDetails.create(user);
    }

    @Override
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found with ID: " + id));
    }

    @Override
    public User getUser() {
        Authentication authentication = getAuthentication();
        if (authentication.isAuthenticated()) {
            try {
                return findById(getPrincipal(authentication).getId());
            } catch (ClassCastException | NotFoundException e) {
                log.warn("[BASIC_AUTH] User details not found!");
                throw new BadCredentialsException("bad_credentials");
            }
        } else {
            log.warn("[BASIC_AUTH] User not authenticated!");
            throw new BadCredentialsException("bad_credentials");
        }
    }

    private Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    public JwtUserDetails getPrincipal(Authentication authentication) {
        return (JwtUserDetails) authentication.getPrincipal();
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found with email: " + email));
    }

    @Override
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(user -> new UserResponse(user.getId(), user.getEmail(), user.getFirstName(), user.getLastName(), user.getRole()))
                .collect(Collectors.toList());
    }

    @Override
    public void updateProfile(UpdateUserDto updateUserDto) {
        User user = getUser();
        user.setFirstName(updateUserDto.getFirstName());
        user.setLastName(updateUserDto.getLastName());

        userRepository.save(user);
        log.info("User profile updated successfully for user: {}", user.getEmail());
    }

	@Override
	public UserResponse getProfile() {
		User user = getUser();
		return new UserResponse(user.getId(), user.getEmail(), user.getFirstName(), user.getLastName(), user.getRole());
	}

	@Override
	public void deleteUser(Long userId) {
		User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("user not found for given id"));
		userRepository.delete(user);
 	}
}