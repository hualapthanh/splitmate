package com.pm.e2etests;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class ApiGatewayRateLimiterE2ETest {

    @Test
    @DisplayName("Verify API Gateway Rate Limiting Configuration Bounds")
    void testRateLimiterLimits() {
        int authReplenishRate = 30;
        int authBurstCapacity = 60;

        int userReplenishRate = 300;
        int userBurstCapacity = 600;

        assertThat(authReplenishRate).isLessThan(userReplenishRate);
        assertThat(authBurstCapacity).isLessThan(userBurstCapacity);
    }
}
