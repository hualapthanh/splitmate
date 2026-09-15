package com.pm.analyticsservice.controller;

import com.pm.analyticsservice.dto.response.*;
import com.pm.analyticsservice.security.UserPrincipal;
import com.pm.analyticsservice.service.AnalyticsService;
import com.pm.analyticsservice.service.ReportExportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;
    private final ReportExportService reportExportService;

    @GetMapping("/overview")
    public ResponseEntity<AnalyticsOverviewResponse> getOverview(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam(name = "periodMonth", required = false) String periodMonth
    ) {
        AnalyticsOverviewResponse response = analyticsService.getOverview(principal.getUserId(), periodMonth);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/categories")
    public ResponseEntity<CategoryBreakdownResponse> getCategoryBreakdown(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam(name = "periodMonth", required = false) String periodMonth
    ) {
        CategoryBreakdownResponse response = analyticsService.getCategoryBreakdown(principal.getUserId(), periodMonth);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/trends")
    public ResponseEntity<SpendingTrendResponse> getSpendingTrends(
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        SpendingTrendResponse response = analyticsService.getSpendingTrends(principal.getUserId());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/groups/{groupId}")
    public ResponseEntity<GroupAnalyticsResponse> getGroupAnalytics(
            @PathVariable("groupId") UUID groupId,
            @RequestParam(name = "periodMonth", required = false) String periodMonth
    ) {
        GroupAnalyticsResponse response = analyticsService.getGroupAnalytics(groupId, periodMonth);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/export/csv")
    public ResponseEntity<byte[]> exportCsvReport(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam(name = "periodMonth", required = false) String periodMonth
    ) {
        byte[] csvBytes = reportExportService.exportCsvReport(principal.getUserId(), periodMonth);
        String filename = "splitmate-analytics-" + (periodMonth != null ? periodMonth : "report") + ".csv";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csvBytes);
    }

    @GetMapping("/export/pdf")
    public ResponseEntity<byte[]> exportPdfReport(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam(name = "periodMonth", required = false) String periodMonth
    ) {
        byte[] pdfBytes = reportExportService.exportPdfReport(principal.getUserId(), periodMonth);
        String filename = "splitmate-analytics-" + (periodMonth != null ? periodMonth : "report") + ".pdf";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }
}
