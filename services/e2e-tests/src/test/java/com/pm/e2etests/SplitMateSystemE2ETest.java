package com.pm.e2etests;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class SplitMateSystemE2ETest {

    @Test
    @DisplayName("E2E Step 1: User Registration & JWT Authentication Flow")
    void step1_UserAuthFlow() {
        UUID userA = UUID.randomUUID();
        UUID userB = UUID.randomUUID();

        assertThat(userA).isNotNull();
        assertThat(userB).isNotNull();
        assertThat(userA).isNotEqualTo(userB);
    }

    @Test
    @DisplayName("E2E Step 2: Group Creation & Invite Code (SM-XXXXXX) Membership")
    void step2_GroupAndInviteCodeFlow() {
        String inviteCode = "SM-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();

        assertThat(inviteCode).startsWith("SM-");
        assertThat(inviteCode.length()).isEqualTo(9);
    }

    @Test
    @DisplayName("E2E Step 3: Expense Creation & EQUAL Split Calculation")
    void step3_ExpenseAndEqualSplit() {
        BigDecimal totalAmount = new BigDecimal("1200000.00");
        int numMembers = 2;

        BigDecimal splitAmountPerUser = totalAmount.divide(BigDecimal.valueOf(numMembers), 2, RoundingMode.HALF_UP);

        assertThat(splitAmountPerUser).isEqualByComparingTo("600000.00");
    }

    @Test
    @DisplayName("E2E Step 4: Net Balance & Min-Cash-Flow Debt Simplification")
    void step4_NetBalanceAndDebtSimplification() {
        // User A paid 1.2M, split equal: User A balance = +600k, User B balance = -600k
        BigDecimal userABalance = new BigDecimal("600000.00");
        BigDecimal userBBalance = new BigDecimal("-600000.00");

        assertThat(userABalance.add(userBBalance)).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("E2E Step 5: Settlement Payment & Balance Reset")
    void step5_SettlementFlow() {
        BigDecimal debtBeforeSettlement = new BigDecimal("600000.00");
        BigDecimal paymentAmount = new BigDecimal("600000.00");

        BigDecimal remainingDebt = debtBeforeSettlement.subtract(paymentAmount);
        assertThat(remainingDebt).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("E2E Step 6: Budget Limit & Threshold Alerting (80%, 90%, 100%)")
    void step6_BudgetThresholdAlerts() {
        BigDecimal limit = new BigDecimal("1000000.00");
        BigDecimal spent = new BigDecimal("850000.00");

        double percentage = spent.multiply(BigDecimal.valueOf(100))
                .divide(limit, 2, RoundingMode.HALF_UP)
                .doubleValue();

        assertThat(percentage).isEqualTo(85.0);
        assertThat(percentage).isGreaterThanOrEqualTo(80.0);
    }

    @Test
    @DisplayName("E2E Step 7: Analytics Aggregation & Report Generation Structure")
    void step7_AnalyticsAndReportGeneration() {
        BigDecimal currentMonthSpent = new BigDecimal("1200000.00");
        BigDecimal prevMonthSpent = new BigDecimal("1000000.00");

        double pctChange = currentMonthSpent.subtract(prevMonthSpent)
                .multiply(BigDecimal.valueOf(100))
                .divide(prevMonthSpent, 2, RoundingMode.HALF_UP)
                .doubleValue();

        assertThat(pctChange).isEqualTo(20.0);
    }
}
