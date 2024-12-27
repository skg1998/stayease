package com.stayease.api.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stayease.api.dto.AppResponse;
import com.stayease.api.dto.LoginDto;
import com.stayease.api.dto.RegisterDto;
import com.stayease.api.dto.SuccessResponse;
import com.stayease.api.dto.TokenResponse;
import com.stayease.api.services.AuthService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
	private final AuthService authService;
	
	@PostMapping("/register")
    public ResponseEntity<AppResponse> register(@Valid @RequestBody RegisterDto registerDto) throws BindException {
		authService.register(registerDto);
        return responseMaker("user register successfully", HttpStatus.CREATED, null);
    }
	
	@PostMapping("/login")
    public ResponseEntity<AppResponse> login(@Valid @RequestBody LoginDto loginDto) {
		TokenResponse userResponse = authService.login(loginDto);
        return responseMaker("user login successfully", HttpStatus.OK, userResponse);
    }
	
	private <T> ResponseEntity<AppResponse> responseMaker(String message, HttpStatus httpStatus, T data){
		SuccessResponse<T> successResponse = new SuccessResponse<>(message, data);
        AppResponse response = new AppResponse(true, successResponse, null);
        return response.toResponseEntity(httpStatus);
	}
}
