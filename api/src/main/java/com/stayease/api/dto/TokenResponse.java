package com.stayease.api.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Setter
@Getter
@SuperBuilder
public class TokenResponse {
	private Long userId;
	private String role;
	private String token;
	private TokenExpiresInResponse expiresIn;
}