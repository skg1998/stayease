package com.stayease.api.services;

import org.springframework.validation.BindException;

import com.stayease.api.dto.LoginDto;
import com.stayease.api.dto.RegisterDto;
import com.stayease.api.dto.TokenResponse;

public interface AuthService {
	public void register(RegisterDto registerDto) throws BindException;
	public TokenResponse login(LoginDto loginDto);
}
