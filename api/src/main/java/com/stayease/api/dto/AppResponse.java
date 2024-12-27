package com.stayease.api.dto;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@SuppressWarnings("rawtypes")
public class AppResponse{
    private boolean success;
	private SuccessResponse successResponse;
    private ErrorResponse errorResponse;

    public AppResponse(boolean success, SuccessResponse successResponse, ErrorResponse errorResponse) {
        this.success = success;
        this.successResponse = successResponse;
        this.errorResponse = errorResponse;
    }

    public boolean isSuccess() {
        return success;
    }

    public SuccessResponse getSuccessResponse() {
        return successResponse;
    }

    public ErrorResponse getErrorResponse() {
        return errorResponse;
    }

    public ResponseEntity<AppResponse> toResponseEntity(HttpStatus httpStatus) {
        return new ResponseEntity<>(this, httpStatus);
    }
}