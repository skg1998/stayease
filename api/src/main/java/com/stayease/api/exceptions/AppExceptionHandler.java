package com.stayease.api.exceptions;

import java.util.HashMap;
import java.util.Map;

import org.apache.coyote.BadRequestException;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import com.stayease.api.dto.AppResponse;
import com.stayease.api.dto.ErrorResponse;

import jakarta.validation.UnexpectedTypeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class AppExceptionHandler {
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public final ResponseEntity<AppResponse> handleHttpRequestMethodNotSupported(
        final HttpRequestMethodNotSupportedException e) {
        log.error(e.toString(), e.getMessage());
        return build(HttpStatus.METHOD_NOT_ALLOWED, "method_not_supported");
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public final ResponseEntity<AppResponse> handleHttpMessageNotReadable(final HttpMessageNotReadableException e) {
        log.error(e.toString(), e.getMessage());
        return build(HttpStatus.BAD_REQUEST, "malformed_json_request");
    }

    @ExceptionHandler(BindException.class)
    public final ResponseEntity<AppResponse> handleBindException(final BindException e) {
        log.error(e.toString(), e.getMessage());
        Map<String, String> errors = new HashMap<>();

        e.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String message = error.getDefaultMessage();
            errors.put(fieldName, message);
        });

        return build(HttpStatus.UNPROCESSABLE_ENTITY, "validation_error", errors);
    }

    @ExceptionHandler({
        BadRequestException.class,
        MultipartException.class,
        MissingServletRequestPartException.class,
        HttpMediaTypeNotSupportedException.class,
        MethodArgumentTypeMismatchException.class,
        IllegalArgumentException.class,
        ConstraintViolationException.class,
        UnexpectedTypeException.class,
        MissingRequestHeaderException.class,
        CipherException.class,
    })
    public final ResponseEntity<AppResponse> handleBadRequestException(final Exception e) {
        log.error(e.toString(), e.getMessage());
        return build(HttpStatus.BAD_REQUEST, e.getCause() != null ? e.getCause().getMessage() : e.getMessage());
    }

    @ExceptionHandler(IllegalStateException.class)
    public final ResponseEntity<AppResponse> handleIllegalStateException(final IllegalStateException e) {
        log.error(e.toString(), e.getMessage());
        return build(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler({
        TokenExpiredException.class
    })
    public final ResponseEntity<AppResponse> handleTokenExpiredRequestException(
        final TokenExpiredException e) {
        log.error(e.toString(), e.getMessage());
        return build(HttpStatus.UNAUTHORIZED, e.getCause() != null ? e.getCause().getMessage() : e.getMessage());
    }

    @ExceptionHandler(NotFoundException.class)
    public final ResponseEntity<AppResponse> handleNotFoundException(final NotFoundException e) {
        log.error(e.toString(), e.getMessage());
        return build(HttpStatus.NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler({
        InternalAuthenticationServiceException.class,
        BadCredentialsException.class,
        AuthenticationCredentialsNotFoundException.class
    })
    public final ResponseEntity<AppResponse> handleBadCredentialsException(final Exception e) {
        log.error(e.toString(), e.getMessage());
        return build(HttpStatus.UNAUTHORIZED, e.getMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public final ResponseEntity<AppResponse> handleAccessDeniedException(final Exception e) {
        log.error(e.toString(), e.getMessage());
        return build(HttpStatus.FORBIDDEN, e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public final ResponseEntity<AppResponse> handleAllExceptions(final Exception e) {
        log.error("Exception: {}", e.getStackTrace());
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "server_error");
    }

    /**
     * Build error response.
     *
     * @param httpStatus HttpStatus enum to response status field
     * @param message    String for response message field
     * @param errors     Map for response errors field
     * @return ResponseEntity
     */
    private ResponseEntity<AppResponse> build(final HttpStatus httpStatus,
                                                final String message,
                                                final Map<String, String> errors) {
        
    	ErrorResponse errorResponse = new ErrorResponse(message, errors);
        AppResponse response = new AppResponse(false, null, errorResponse);
        return response.toResponseEntity(httpStatus);
    }

    /**
     * Build error response.
     *
     * @param httpStatus HttpStatus enum to response status field
     * @param message    String for response message field
     * @return ResponseEntity
     */
    private ResponseEntity<AppResponse> build(final HttpStatus httpStatus, final String message) {
        return build(httpStatus, message, new HashMap<>());
    }
}