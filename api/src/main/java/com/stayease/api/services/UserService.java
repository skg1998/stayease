package com.stayease.api.services;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import com.stayease.api.dto.UpdateUserDto;
import com.stayease.api.dto.UserResponse;
import com.stayease.api.entity.User;
import com.stayease.api.security.JwtUserDetails;

public interface UserService {
	public UserResponse getProfile();
	public void deleteUser(Long userId);
	public List<UserResponse> getAllUsers();
	void updateProfile(UpdateUserDto updateUserDto);

	public User findByEmail(String name);
	public UserDetails loadUserByEmail(String name);
	public User findById(Long userIdFromToken);
	public JwtUserDetails getPrincipal(Authentication authentication);
	public UserDetails loadUserById(Long id);
	public User getUser();

}
