package com.pm.budgetservice;

import com.pm.budgetservice.event.BudgetEventPublisher;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
@ActiveProfiles("test")
class BudgetServiceApplicationTests {

    @MockitoBean
    private BudgetEventPublisher budgetEventPublisher;

    @Test
    void contextLoads() {
    }
}
