package com.tridel.tems_data_service.service;


import com.tridel.tems_data_service.model.request.ReportRequest;
import com.tridel.tems_data_service.model.request.Request;
import com.tridel.tems_data_service.model.response.MobileReport;

import java.util.List;

public interface MobileService {
    String getStationStatus(Request request);

    List<MobileReport> loadReportsData(ReportRequest request);

    List<MobileReport> loadReportsMinData(ReportRequest request);
}
