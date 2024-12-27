package com.stayease.api.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class TokenExpiresInResponse{
	private Long token;
	private Long refreshToken;
}