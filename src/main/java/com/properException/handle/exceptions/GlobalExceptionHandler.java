package com.properException.handle.exceptions;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.*;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import com.properException.handle.dto.ApiResponse;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleResourceNotFound(ResourceNotFoundException ex) {
        ApiResponse<Void> resp = ApiResponse.<Void>builder()
            .success(false)
            .message(ex.getMessage())
            .data(null)
            .statusCode(HttpStatus.NOT_FOUND.value()) 
            .build();
        return new ResponseEntity<>(resp, HttpStatus.NOT_FOUND);
    }

    // <<< NEW: handle @Valid errors >>> DTO ke ander jo field ke ander proper field nahi send karo ge tab ye sare field scan kare ga error
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();
        for (FieldError err : ex.getBindingResult().getFieldErrors()) {
            errors.put(err.getField(), err.getDefaultMessage());
        }

        ApiResponse<Map<String, String>> resp = ApiResponse.<Map<String, String>>builder()
            .success(false)
            .message("Validation failed")
            .data(errors)
            .statusCode(HttpStatus.BAD_REQUEST.value())
            .build();

        return new ResponseEntity<>(resp, HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(ResourceValidationException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleDuplicateExceptions(
            ResourceValidationException ex) {

        ApiResponse<Map<String, String>> resp = ApiResponse.<Map<String, String>>builder()
            .success(false)
            .message(ex.getMessage())
            .data(ex.getErrors())
            .statusCode(HttpStatus.BAD_REQUEST.value())
            .build();

        return new ResponseEntity<>(resp, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleAll(Exception ex) {
        ApiResponse<Void> resp = ApiResponse.<Void>builder()
            .success(false)
            .message("Please check request data / request path")
            .statusCode(HttpStatus.BAD_REQUEST.value())
            .data(null)
            .build();
        return new ResponseEntity<>(resp, HttpStatus.BAD_REQUEST);
    }
}
