package com.tridel.tems_data_service.dao;

import com.tridel.tems_data_service.model.request.CentricDashboardReq;
import com.tridel.tems_data_service.model.request.ParamPojoReq;
import com.tridel.tems_data_service.model.response.CentricDataResponse;
import com.tridel.tems_data_service.model.response.HeaderDataStatusResponse;

import java.util.List;

public interface DashboardDao {
    HeaderDataStatusResponse getHeaderDataStatusValues(Integer station);

    String loadDashboardDataForStation(String fromDate, String toDate, List<ParamPojoReq> paramReqList, String stationId,String tableCode);

    String loadDashboardDataForMET(List<ParamPojoReq> paramReqList, String stationId, String interval,String tableCode);

    List<CentricDataResponse> getLatestDataForCentricDashboard(CentricDashboardReq req);
}
