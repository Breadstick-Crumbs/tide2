package com.tridel.tems_data_service.dao;

import com.tridel.tems_data_service.model.request.ReportRequest;
import com.tridel.tems_data_service.model.response.MobileReport;

import java.util.List;

public interface MobileDao {
    List<MobileReport> loadReportData(ReportRequest request);

    List<MobileReport> loadReportMinData(ReportRequest request);
}
