package com.tridel.tems_data_service.service;

import com.tridel.tems_data_service.model.request.CentricDashboardReq;
import com.tridel.tems_data_service.model.request.ReportRequest;
import com.tridel.tems_data_service.model.request.DataRequest;
import com.tridel.tems_data_service.model.response.CentricDataResponse;
import com.tridel.tems_data_service.model.response.HeaderDataStatusResponse;

import java.util.List;

public interface DashboardService {
       HeaderDataStatusResponse getHeaderDataStatusValues(ReportRequest req);

    String loadDashboardDataForStation(DataRequest request);

    String loadDashboardDataForMET(DataRequest request);

    List<CentricDataResponse> loadCentricDashboardData(CentricDashboardReq request);
}
