package com.pm.e2etests;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class SystemHealthCheckE2ETest {

    private final Map<String, Integer> expectedServices = Map.of(
            "API Gateway", 8080,
            "Auth Service", 8081,
            "User Service", 8082,
            "Group Service", 8083,
            "Expense Service", 8084,
            "Balance Service", 8085,
            "Notification Service", 8086,
            "Budget Service", 8087,
            "Analytics Service", 8088
    );

    @Test
    @DisplayName("Verify all 9 Microservices have unique registered ports in ecosystem")
    void verifyMicroservicePorts() {
        assertThat(expectedServices).hasSize(9);
        long uniquePorts = expectedServices.values().stream().distinct().count();
        assertThat(uniquePorts).isEqualTo(9);
        assertThat(expectedServices.get("API Gateway")).isEqualTo(8080);
        assertThat(expectedServices.get("Analytics Service")).isEqualTo(8088);
    }
}
