package com.pm.apigateway.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pm.apigateway.exception.ErrorCode;
import com.pm.apigateway.security.JwtTokenValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

    private final JwtTokenValidator jwtTokenValidator;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public static final String HEADER_USER_ID = "X-User-Id";
    public static final String HEADER_USER_EMAIL = "X-User-Email";
    public static final String HEADER_USER_ROLE = "X-User-Role";

    private static final List<String> PUBLIC_ENDPOINTS = Arrays.asList(
            "/api/v1/auth/register",
            "/api/v1/auth/login",
            "/api/v1/auth/refresh",
            "/actuator/**",
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui.html"
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getPath().value();
        HttpMethod method = request.getMethod();

        // 1. Bypass public endpoints
        if (isPublicEndpoint(path, method)) {
            return chain.filter(exchange);
        }

        // 2. Check Authorization header
        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (!StringUtils.hasText(authHeader) || !authHeader.startsWith("Bearer ")) {
            log.warn("Missing or invalid Authorization header for path: {}", path);
            return onError(exchange, ErrorCode.GW_003, "Missing Authorization Bearer token");
        }

        String token = authHeader.substring(7);

        // 3. Validate Token
        if (!jwtTokenValidator.validateAccessToken(token)) {
            log.warn("Invalid JWT Access Token for path: {}", path);
            return onError(exchange, ErrorCode.GW_002, "Invalid or expired Access Token");
        }

        // 4. Extract Claims and mutate downstream headers
        String userId = jwtTokenValidator.getUserIdFromToken(token);
        String email = jwtTokenValidator.getEmailFromToken(token);
        String role = jwtTokenValidator.getRoleFromToken(token);

        ServerHttpRequest mutatedRequest = request.mutate()
                .header(HEADER_USER_ID, userId != null ? userId : "")
                .header(HEADER_USER_EMAIL, email != null ? email : "")
                .header(HEADER_USER_ROLE, role != null ? role : "USER")
                .build();

        return chain.filter(exchange.mutate().request(mutatedRequest).build());
    }

    private boolean isPublicEndpoint(String path, HttpMethod method) {
        // Public profile endpoint check: GET /api/v1/users/{id}
        if (method == HttpMethod.GET && path.matches("^/api/v1/users/[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$")) {
            return true;
        }

        for (String publicPath : PUBLIC_ENDPOINTS) {
            if (pathMatcher.match(publicPath, path)) {
                return true;
            }
        }
        return false;
    }

    private Mono<Void> onError(ServerWebExchange exchange, ErrorCode errorCode, String customMessage) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(errorCode.getStatus());
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        String traceId = exchange.getRequest().getHeaders().getFirst("X-Correlation-Id");

        Map<String, Object> errorBody = new HashMap<>();
        errorBody.put("timestamp", OffsetDateTime.now().toString());
        errorBody.put("status", errorCode.getStatus().value());
        errorBody.put("error", errorCode.getStatus().getReasonPhrase());
        errorBody.put("code", errorCode.getCode());
        errorBody.put("message", customMessage);
        errorBody.put("path", exchange.getRequest().getPath().value());
        errorBody.put("traceId", traceId != null ? traceId : "");

        byte[] bytes;
        try {
            bytes = objectMapper.writeValueAsBytes(errorBody);
        } catch (JsonProcessingException e) {
            bytes = "{\"error\":\"Unauthorized\"}".getBytes();
        }

        DataBuffer buffer = response.bufferFactory().wrap(bytes);
        return response.writeWith(Mono.just(buffer));
    }

    @Override
    public int getOrder() {
        return -1; // Execute right after CorrelationIdGlobalFilter
    }
}
