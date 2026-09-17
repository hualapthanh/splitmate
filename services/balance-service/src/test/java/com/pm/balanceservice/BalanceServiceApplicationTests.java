package com.pm.balanceservice;

import com.pm.balanceservice.event.SettlementEventPublisher;
import com.pm.balanceservice.mapper.BalanceMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
@ActiveProfiles("test")
class BalanceServiceApplicationTests {

    @MockitoBean
    private SettlementEventPublisher settlementEventPublisher;

    @MockitoBean
    private BalanceMapper balanceMapper;

    @Test
    void contextLoads() {
    }
}
