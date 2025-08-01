package com.tridel.tems_data_service.service;

import com.tridel.tems_data_service.model.request.ReportRequest;

public interface ReportService {
    String loadReportsDataOneMinute(ReportRequest reportRequest);

    //String loadReportsDataByInterval(ReportRequest reportRequest);

    String loadReportsDataHourly(ReportRequest reportRequest);

    String loadClimateReportsDataOneMin(ReportRequest reportRequest);

    String loadClimateReportsDataInterval(ReportRequest reportRequest);

    String getReportByInterval(ReportRequest reportRequest);

    String getClimateReportByInterval(ReportRequest request);
}
