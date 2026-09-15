package com.pm.analyticsservice.service.impl;

import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.pm.analyticsservice.dto.response.AnalyticsOverviewResponse;
import com.pm.analyticsservice.dto.response.CategoryBreakdownResponse;
import com.pm.analyticsservice.entity.AnalyticsExpenseRecord;
import com.pm.analyticsservice.exception.AnalyticsException;
import com.pm.analyticsservice.repository.AnalyticsExpenseRecordRepository;
import com.pm.analyticsservice.service.AnalyticsService;
import com.pm.analyticsservice.service.ReportExportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportExportServiceImpl implements ReportExportService {

    private final AnalyticsExpenseRecordRepository recordRepository;
    private final AnalyticsService analyticsService;

    private static final DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM");

    @Override
    @Transactional(readOnly = true)
    public byte[] exportCsvReport(UUID userId, String periodMonth) {
        String targetMonth = (periodMonth != null && !periodMonth.isBlank())
                ? periodMonth
                : LocalDate.now().format(MONTH_FORMATTER);

        List<AnalyticsExpenseRecord> records = recordRepository.findByUserIdAndPeriodMonth(userId, targetMonth);

        StringBuilder sb = new StringBuilder();
        // Add UTF-8 BOM for Excel compatibility
        sb.append("\uFEFF");
        sb.append("Date,Category,Amount (VND),Expense ID\n");

        for (AnalyticsExpenseRecord rec : records) {
            sb.append(rec.getExpenseDate()).append(",");
            sb.append(rec.getCategory()).append(",");
            sb.append(rec.getAmount()).append(",");
            sb.append(rec.getExpenseId()).append("\n");
        }

        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] exportPdfReport(UUID userId, String periodMonth) {
        String targetMonth = (periodMonth != null && !periodMonth.isBlank())
                ? periodMonth
                : LocalDate.now().format(MONTH_FORMATTER);

        AnalyticsOverviewResponse overview = analyticsService.getOverview(userId, targetMonth);
        CategoryBreakdownResponse breakdown = analyticsService.getCategoryBreakdown(userId, targetMonth);
        List<AnalyticsExpenseRecord> records = recordRepository.findByUserIdAndPeriodMonth(userId, targetMonth);

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document();
            PdfWriter.getInstance(document, out);
            document.open();

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            Paragraph title = new Paragraph("SplitMate Financial Analytics Report", titleFont);
            title.setSpacingAfter(10);
            document.add(title);

            Font subFont = FontFactory.getFont(FontFactory.HELVETICA, 12);
            document.add(new Paragraph("Period Month: " + targetMonth, subFont));
            document.add(new Paragraph("Total Spent: " + overview.getCurrentMonthTotalSpent() + " VND", subFont));
            document.add(new Paragraph("Total Transactions: " + overview.getCurrentMonthTransactions(), subFont));
            document.add(new Paragraph("Top Category: " + overview.getTopSpendingCategory() + " (" + overview.getTopSpendingCategoryAmount() + " VND)", subFont));
            document.add(new Paragraph("Change vs Prev Month: " + overview.getPercentageChange() + "%", subFont));
            document.add(new Paragraph(" "));

            // Category Breakdown Table
            Font sectionFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14);
            Paragraph catTitle = new Paragraph("Category Breakdown", sectionFont);
            catTitle.setSpacingAfter(10);
            document.add(catTitle);

            PdfPTable catTable = new PdfPTable(3);
            catTable.addCell("Category");
            catTable.addCell("Amount (VND)");
            catTable.addCell("Percentage (%)");

            if (breakdown.getCategories() != null) {
                for (CategoryBreakdownResponse.CategoryItem item : breakdown.getCategories()) {
                    catTable.addCell(item.getCategory());
                    catTable.addCell(item.getAmount().toString());
                    catTable.addCell(String.format("%.2f%%", item.getPercentage()));
                }
            }
            document.add(catTable);
            document.add(new Paragraph(" "));

            // Recent Expenses Table
            Paragraph recTitle = new Paragraph("Expense Transactions", sectionFont);
            recTitle.setSpacingAfter(10);
            document.add(recTitle);

            PdfPTable recTable = new PdfPTable(3);
            recTable.addCell("Date");
            recTable.addCell("Category");
            recTable.addCell("Amount (VND)");

            for (AnalyticsExpenseRecord record : records) {
                recTable.addCell(record.getExpenseDate().toString());
                recTable.addCell(record.getCategory());
                recTable.addCell(record.getAmount().toString());
            }
            document.add(recTable);

            document.close();
            return out.toByteArray();
        } catch (Exception e) {
            log.error("Failed to generate PDF report for userId {} and month {}", userId, targetMonth, e);
            throw new AnalyticsException("Failed to generate PDF report: " + e.getMessage());
        }
    }
}
