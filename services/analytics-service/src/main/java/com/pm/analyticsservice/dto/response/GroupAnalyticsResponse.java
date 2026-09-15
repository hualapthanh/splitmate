package com.pm.analyticsservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupAnalyticsResponse {
    private UUID groupId;
    private String periodMonth;
    private BigDecimal groupTotalSpent;
    private List<MemberContribution> memberContributions;
    private List<CategoryBreakdownResponse.CategoryItem> categoryBreakdown;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MemberContribution {
        private UUID userId;
        private BigDecimal amountSpent;
        private double percentageShare;
        private long transactionCount;
    }
}
