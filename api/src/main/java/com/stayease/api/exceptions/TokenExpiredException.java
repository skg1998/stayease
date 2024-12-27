package com.stayease.api.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class TokenExpiredException extends RuntimeException {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public TokenExpiredException() {
        super("Token is expired!");
    }

    public TokenExpiredException(final String message) {
        super(message);
    }
}