package com.pm.apigateway.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebExceptionHandler;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@Order(-2)
@RequiredArgsConstructor
public class GlobalGatewayExceptionHandler implements WebExceptionHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        ServerHttpResponse response = exchange.getResponse();

        if (response.isCommitted()) {
            return Mono.error(ex);
        }

        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        String code = ErrorCode.INTERNAL_SERVER_ERROR.getCode();
        String message = ex.getMessage();

        if (ex instanceof ResponseStatusException rse) {
            status = HttpStatus.valueOf(rse.getStatusCode().value());
            if (status == HttpStatus.NOT_FOUND) {
                code = ErrorCode.GW_001.getCode();
                message = "Route Not Found";
            } else if (status == HttpStatus.UNAUTHORIZED) {
                code = ErrorCode.GW_003.getCode();
            } else if (status == HttpStatus.TOO_MANY_REQUESTS) {
                code = ErrorCode.GW_005.getCode();
                message = "Rate Limit Exceeded";
            } else if (status == HttpStatus.SERVICE_UNAVAILABLE) {
                code = ErrorCode.GW_006.getCode();
                message = "Service Unavailable";
            }
        }

        response.setStatusCode(status);

        String traceId = exchange.getRequest().getHeaders().getFirst("X-Correlation-Id");
        if (traceId == null) {
            traceId = exchange.getRequest().getHeaders().getFirst("X-Trace-Id");
        }

        Map<String, Object> errorAttributes = new HashMap<>();
        errorAttributes.put("timestamp", OffsetDateTime.now().toString());
        errorAttributes.put("status", status.value());
        errorAttributes.put("error", status.getReasonPhrase());
        errorAttributes.put("code", code);
        errorAttributes.put("message", message);
        errorAttributes.put("path", exchange.getRequest().getPath().value());
        errorAttributes.put("traceId", traceId != null ? traceId : "");

        log.error("Gateway Error: status={}, code={}, path={}, message={}", status, code, exchange.getRequest().getPath(), message);

        byte[] bytes;
        try {
            bytes = objectMapper.writeValueAsBytes(errorAttributes);
        } catch (JsonProcessingException e) {
            bytes = "{\"error\":\"Internal Server Error\"}".getBytes();
        }

        DataBuffer buffer = response.bufferFactory().wrap(bytes);
        return response.writeWith(Mono.just(buffer));
    }
}
