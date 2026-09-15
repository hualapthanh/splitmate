package com.pm.analyticsservice.controller;

import com.pm.analyticsservice.dto.response.AnalyticsOverviewResponse;
import com.pm.analyticsservice.dto.response.CategoryBreakdownResponse;
import com.pm.analyticsservice.exception.GlobalExceptionHandler;
import com.pm.analyticsservice.security.UserPrincipal;
import com.pm.analyticsservice.service.AnalyticsService;
import com.pm.analyticsservice.service.ReportExportService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AnalyticsControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AnalyticsService analyticsService;

    @Mock
    private ReportExportService reportExportService;

    @InjectMocks
    private AnalyticsController analyticsController;

    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();

        HandlerMethodArgumentResolver principalResolver = new HandlerMethodArgumentResolver() {
            @Override
            public boolean supportsParameter(MethodParameter parameter) {
                return parameter.hasParameterAnnotation(AuthenticationPrincipal.class)
                        && parameter.getParameterType().equals(UserPrincipal.class);
            }

            @Override
            public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                          NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
                return UserPrincipal.builder()
                        .userId(userId)
                        .email("user@example.com")
                        .role("USER")
                        .build();
            }
        };

        mockMvc = MockMvcBuilders.standaloneSetup(analyticsController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setCustomArgumentResolvers(principalResolver)
                .build();
    }

    @Test
    @DisplayName("GET /api/v1/analytics/overview - Get Overview")
    void getOverview_ReturnsOk() throws Exception {
        AnalyticsOverviewResponse overview = AnalyticsOverviewResponse.builder()
                .currentPeriodMonth("2026-09")
                .currentMonthTotalSpent(new BigDecimal("5000000.00"))
                .currentMonthTransactions(10)
                .previousPeriodMonth("2026-08")
                .previousMonthTotalSpent(new BigDecimal("4000000.00"))
                .percentageChange(25.0)
                .topSpendingCategory("FOOD")
                .topSpendingCategoryAmount(new BigDecimal("3000000.00"))
                .build();

        when(analyticsService.getOverview(eq(userId), any())).thenReturn(overview);

        mockMvc.perform(get("/api/v1/analytics/overview")
                        .param("periodMonth", "2026-09"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currentPeriodMonth").value("2026-09"))
                .andExpect(jsonPath("$.currentMonthTotalSpent").value(5000000.00))
                .andExpect(jsonPath("$.percentageChange").value(25.0))
                .andExpect(jsonPath("$.topSpendingCategory").value("FOOD"));
    }

    @Test
    @DisplayName("GET /api/v1/analytics/export/csv - Export CSV Report")
    void exportCsvReport_ReturnsCsvFile() throws Exception {
        byte[] csvBytes = "Date,Category,Amount (VND),Expense ID\n2026-09-05,FOOD,1000.00,uuid".getBytes(StandardCharsets.UTF_8);

        when(reportExportService.exportCsvReport(eq(userId), eq("2026-09"))).thenReturn(csvBytes);

        mockMvc.perform(get("/api/v1/analytics/export/csv")
                        .param("periodMonth", "2026-09"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"splitmate-analytics-2026-09.csv\""))
                .andExpect(content().contentType("text/csv"));
    }

    @Test
    @DisplayName("GET /api/v1/analytics/export/pdf - Export PDF Report")
    void exportPdfReport_ReturnsPdfFile() throws Exception {
        byte[] pdfBytes = "%PDF-1.4 test content".getBytes(StandardCharsets.UTF_8);

        when(reportExportService.exportPdfReport(eq(userId), eq("2026-09"))).thenReturn(pdfBytes);

        mockMvc.perform(get("/api/v1/analytics/export/pdf")
                        .param("periodMonth", "2026-09"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"splitmate-analytics-2026-09.pdf\""))
                .andExpect(content().contentType(MediaType.APPLICATION_PDF));
    }
}
