package com.pm.analyticsservice.service;

import java.util.UUID;

public interface ReportExportService {

    byte[] exportCsvReport(UUID userId, String periodMonth);

    byte[] exportPdfReport(UUID userId, String periodMonth);
}
