package com.tridel.tems_data_service.dao;

import com.tridel.tems_data_service.model.request.ReportRequest;
import org.json.JSONArray;

public interface ReportDao {
    String loadReportsDataOneMinute(ReportRequest reportRequest);

    String getReportByInterval(ReportRequest request);

    //JSONArray getReportsDataByInterval(ReportRequest request);

    String loadReportsDataHourly(ReportRequest reportRequest);

    String getAllSensorReportDataOneMinute(ReportRequest reportRequest);

    JSONArray getClimateReportsDataByInterval(ReportRequest request);

    String getClimateReportByInterval(ReportRequest request);
}
