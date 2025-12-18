package com.badsector.qerb.shared.web.exception;

import com.badsector.qerb.shared.web.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 1. Wrong Password or Email (Return 401)
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse<Void>> handleBadCredentials(BadCredentialsException e) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED) // 401
                .body(ApiResponse.error("Invalid email or password."));
    }

    // 2. Account Not Verified (Return 403)
    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<ApiResponse<Void>> handleDisabledException(DisabledException e) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN) // 403
                .body(ApiResponse.error("Account is not verified. Please check your email."));
    }

    // 3. General Business Errors (User not found, etc.) - IllegalArgumentException
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgument(IllegalArgumentException e) {
        return ResponseEntity
                .badRequest() // 400
                .body(ApiResponse.error(e.getMessage()));
    }

    // 4. Validation Errors (@Valid - Empty fields, regex mismatch, etc.)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationErrors(MethodArgumentNotValidException e) {
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );

        return ResponseEntity
                .badRequest() // 400
                .body(ApiResponse.error(errors, "Validation failed"));
    }

    // 5. Unexpected Errors
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGeneralException(Exception e) {
        return ResponseEntity
                .internalServerError() // 500
                .body(ApiResponse.error("An unexpected error occurred: " + e.getMessage()));
    }
}