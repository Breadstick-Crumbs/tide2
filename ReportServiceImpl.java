package com.tridel.tems_data_service.service;

import com.tridel.tems_data_service.dao.ReportDao;
import com.tridel.tems_data_service.model.request.ReportRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReportServiceImpl implements ReportService{
    ReportDao reportDao;
    @Autowired
    ReportServiceImpl(ReportDao reportDao){
        this.reportDao=reportDao;
    }
    @Override
    public String loadReportsDataOneMinute(ReportRequest reportRequest) {
        return reportDao.loadReportsDataOneMinute(reportRequest);
    }
    /*@Override
    public String loadReportsDataByInterval(ReportRequest reportRequest) {
        return reportDao.getReportsDataByInterval(reportRequest).toString();
    }*/
    @Override
    public String loadReportsDataHourly(ReportRequest reportRequest) {
        return reportDao.loadReportsDataHourly(reportRequest);
    }
    @Override
    public String loadClimateReportsDataOneMin(ReportRequest reportRequest) {
        return reportDao.getAllSensorReportDataOneMinute(reportRequest);
    }
    @Override
    public String loadClimateReportsDataInterval(ReportRequest reportRequest) {
        return reportDao.getClimateReportsDataByInterval(reportRequest).toString();
    }
    @Override
    public String getReportByInterval(ReportRequest reportRequest) {
        return reportDao.getReportByInterval(reportRequest);
    }

    @Override
    public String getClimateReportByInterval(ReportRequest request) {
        return reportDao.getClimateReportByInterval(request);
    }
}
