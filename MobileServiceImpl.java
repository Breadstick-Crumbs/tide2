package com.tridel.tems_data_service.service;

import com.tridel.tems_data_service.dao.MobileDao;
import com.tridel.tems_data_service.exception.TemsBadRequestException;
import com.tridel.tems_data_service.model.request.ReportRequest;
import com.tridel.tems_data_service.model.request.Request;
import com.tridel.tems_data_service.model.response.MobileReport;
import com.tridel.tems_data_service.repository.HeaderDataStatusRepository;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@Slf4j
public class MobileServiceImpl implements MobileService{

    HeaderDataStatusRepository headerStatusRepo;
    MobileDao mobileDao;

    MobileServiceImpl(HeaderDataStatusRepository headerStatusRepo, MobileDao mobileDao){
        this.headerStatusRepo = headerStatusRepo;
        this.mobileDao = mobileDao;
    }

    @Override
    public String getStationStatus(Request request) {
        if(request.getStationIds().isEmpty()  )
            throw new TemsBadRequestException("Station not found");
        
        JSONObject object = new JSONObject();
        int onlineSt = headerStatusRepo.getAllOnlineStationCount(request.getStationIds());
        object.put("onlineStation", onlineSt);
        object.put("offlineStation",request.getStationIds().size() - onlineSt);
        return object.toString();
    }

    @Override
    public List<MobileReport> loadReportsData(ReportRequest request) {
        return mobileDao.loadReportData(request);
    }

    @Override
    public List<MobileReport> loadReportsMinData(ReportRequest request) {
        return mobileDao.loadReportMinData(request);
    }
}