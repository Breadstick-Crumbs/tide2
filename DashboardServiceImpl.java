package com.tridel.tems_data_service.service;

import com.tridel.tems_data_service.dao.DashboardDao;
import com.tridel.tems_data_service.model.request.CentricDashboardReq;
import com.tridel.tems_data_service.model.request.ReportRequest;
import com.tridel.tems_data_service.model.request.DataRequest;
import com.tridel.tems_data_service.model.response.CentricDataResponse;
import com.tridel.tems_data_service.model.response.HeaderDataStatusResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class DashboardServiceImpl implements DashboardService{
    DashboardDao dashboardDao;
    DashboardServiceImpl(DashboardDao dashboardDao){
        this.dashboardDao = dashboardDao;
    }
    @Override
    public HeaderDataStatusResponse getHeaderDataStatusValues(ReportRequest req){
        return dashboardDao.getHeaderDataStatusValues(req.getStationId());
    }
    @Override
    public String loadDashboardDataForStation(DataRequest request) {
        return dashboardDao.loadDashboardDataForStation(request.getFromDate(),request.getToDate(),request.getParamReqList(),request.getStationIds(), request.getTableCode());
    }
    @Override
    public String loadDashboardDataForMET(DataRequest request) {
        return dashboardDao.loadDashboardDataForMET(request.getParamReqList(),request.getStationIds(),request.getInterval(),request.getTableCode());
    }
    @Override
    public List<CentricDataResponse> loadCentricDashboardData(CentricDashboardReq request) {
        return dashboardDao.getLatestDataForCentricDashboard(request);
    }
}
