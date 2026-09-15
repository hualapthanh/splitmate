package com.pm.analyticsservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryBreakdownResponse {
    private String periodMonth;
    private BigDecimal totalSpent;
    private List<CategoryItem> categories;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CategoryItem {
        private String category;
        private BigDecimal amount;
        private double percentage;
        private long transactionCount;
    }
}
