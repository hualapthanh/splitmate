package com.pm.userservice.exception;

import com.pm.userservice.common.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final String TRACE_HEADER = "X-Trace-Id";

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ApiResponse<Void>> handleBaseException(BaseException ex, HttpServletRequest request) {
        String traceId = getOrCreateTraceId(request);
        log.error("[Trace: {}] BaseException occurred: code={}, status={}, message={}", 
                traceId, ex.getErrorCode().getCode(), ex.getErrorCode().getStatus(), ex.getMessage());

        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .timestamp(Instant.now())
                .status(ex.getErrorCode().getStatus().value())
                .error(ex.getErrorCode().getStatus().getReasonPhrase())
                .code(ex.getErrorCode().getCode())
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .traceId(traceId)
                .build();

        return new ResponseEntity<>(response, ex.getErrorCode().getStatus());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationException(MethodArgumentNotValidException ex, HttpServletRequest request) {
        String traceId = getOrCreateTraceId(request);
        String validationErrors = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        
        log.error("[Trace: {}] Validation Failed: {}", traceId, validationErrors);

        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .timestamp(Instant.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .code(ErrorCode.USER_001.getCode())
                .message(validationErrors)
                .path(request.getRequestURI())
                .traceId(traceId)
                .build();

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGenericException(Exception ex, HttpServletRequest request) {
        String traceId = getOrCreateTraceId(request);
        log.error("[Trace: {}] Unhandled Exception occurred: ", traceId, ex);

        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .timestamp(Instant.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                .code(ErrorCode.INTERNAL_SERVER_ERROR.getCode())
                .message("An unexpected error occurred")
                .path(request.getRequestURI())
                .traceId(traceId)
                .build();

        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private String getOrCreateTraceId(HttpServletRequest request) {
        String traceId = request.getHeader(TRACE_HEADER);
        if (traceId == null || traceId.trim().isEmpty()) {
            traceId = UUID.randomUUID().toString();
        }
        return traceId;
    }
}
