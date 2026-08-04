package com.pm.userservice.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    @Builder.Default
    private Instant timestamp = Instant.now();
    private int status;
    private String error;
    private String code;
    private String message;
    private String path;
    private String traceId;
    private T data;

    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .status(200)
                .data(data)
                .build();
    }

    public static <T> ApiResponse<T> success(int status, T data) {
        return ApiResponse.<T>builder()
                .status(status)
                .data(data)
                .build();
    }

    public static ApiResponse<Void> error(int status, String error, String code, String message, String path, String traceId) {
        return ApiResponse.<Void>builder()
                .status(status)
                .error(error)
                .code(code)
                .message(message)
                .path(path)
                .traceId(traceId)
                .build();
    }
}
