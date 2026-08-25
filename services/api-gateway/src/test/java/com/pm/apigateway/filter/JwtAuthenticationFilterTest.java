package com.pm.apigateway.filter;

import com.pm.apigateway.security.JwtTokenValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtTokenValidator jwtTokenValidator;

    @Mock
    private GatewayFilterChain chain;

    @InjectMocks
    private JwtAuthenticationFilter filter;

    @BeforeEach
    void setUp() {
        lenient().when(chain.filter(any())).thenReturn(Mono.empty());
    }

    @Test
    @DisplayName("filter() should bypass public auth register endpoint without JWT")
    void filter_publicEndpoint_bypassesJwt() {
        MockServerHttpRequest request = MockServerHttpRequest.post("/api/v1/auth/register").build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        StepVerifier.create(filter.filter(exchange, chain)).verifyComplete();

        verify(chain).filter(exchange);
        verifyNoInteractions(jwtTokenValidator);
    }

    @Test
    @DisplayName("filter() should bypass GET public profile endpoint without JWT")
    void filter_publicProfile_bypassesJwt() {
        String randomUserId = UUID.randomUUID().toString();
        MockServerHttpRequest request = MockServerHttpRequest.get("/api/v1/users/" + randomUserId).build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        StepVerifier.create(filter.filter(exchange, chain)).verifyComplete();

        verify(chain).filter(exchange);
        verifyNoInteractions(jwtTokenValidator);
    }

    @Test
    @DisplayName("filter() should return 401 Unauthorized for protected path missing Authorization header")
    void filter_protectedPath_missingHeader_returns401() {
        MockServerHttpRequest request = MockServerHttpRequest.get("/api/v1/users/me").build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        StepVerifier.create(filter.filter(exchange, chain)).verifyComplete();

        assertEquals(HttpStatus.UNAUTHORIZED, exchange.getResponse().getStatusCode());
        verify(chain, never()).filter(any());
    }

    @Test
    @DisplayName("filter() should return 401 Unauthorized for invalid JWT token")
    void filter_protectedPath_invalidToken_returns401() {
        MockServerHttpRequest request = MockServerHttpRequest.get("/api/v1/users/me")
                .header(HttpHeaders.AUTHORIZATION, "Bearer invalid.jwt.token")
                .build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        when(jwtTokenValidator.validateAccessToken("invalid.jwt.token")).thenReturn(false);

        StepVerifier.create(filter.filter(exchange, chain)).verifyComplete();

        assertEquals(HttpStatus.UNAUTHORIZED, exchange.getResponse().getStatusCode());
        verify(chain, never()).filter(any());
    }

    @Test
    @DisplayName("filter() should inject user headers and forward request for valid JWT")
    void filter_protectedPath_validToken_injectsHeaders() {
        String validToken = "valid.jwt.token";
        String sampleUserId = UUID.randomUUID().toString();

        MockServerHttpRequest request = MockServerHttpRequest.get("/api/v1/users/me")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + validToken)
                .build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        when(jwtTokenValidator.validateAccessToken(validToken)).thenReturn(true);
        when(jwtTokenValidator.getUserIdFromToken(validToken)).thenReturn(sampleUserId);
        when(jwtTokenValidator.getEmailFromToken(validToken)).thenReturn("john.doe@example.com");
        when(jwtTokenValidator.getRoleFromToken(validToken)).thenReturn("USER");

        StepVerifier.create(filter.filter(exchange, chain)).verifyComplete();

        verify(chain).filter(argThat(mutatedExchange -> {
            HttpHeaders headers = mutatedExchange.getRequest().getHeaders();
            return sampleUserId.equals(headers.getFirst(JwtAuthenticationFilter.HEADER_USER_ID)) &&
                   "john.doe@example.com".equals(headers.getFirst(JwtAuthenticationFilter.HEADER_USER_EMAIL)) &&
                   "USER".equals(headers.getFirst(JwtAuthenticationFilter.HEADER_USER_ROLE));
        }));
    }
}
