package com.tridel.tems_data_service.service;

import com.tridel.tems_data_service.dao.ParamDao;
import com.tridel.tems_data_service.dao.StationDao;
import com.tridel.tems_data_service.model.request.SensorParamViewReq;
import com.tridel.tems_data_service.model.request.StationDtlRequest;
import com.tridel.tems_data_service.model.response.CommunicationPojo;
import com.tridel.tems_data_service.model.response.StationStatus;
import com.tridel.tems_data_service.repository.HeaderDataStatusRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class StationMasterServiceImpl implements  StationMasterService{
    ParamDao paramDao;
    StationDao stationDao;
    HeaderDataStatusRepository headerDataStatusRepo;
    StationMasterServiceImpl(ParamDao paramDao,StationDao stationDao,HeaderDataStatusRepository headerDataStatusRepo){
        this.paramDao = paramDao;
        this.stationDao = stationDao;
        this.headerDataStatusRepo = headerDataStatusRepo;
    }
    @Override
    public Map<Integer, CommunicationPojo> getHeaderDtlsForComm(StationDtlRequest request){
        return paramDao.getHeaderDtlsForComm(request);
    }
    @Override
    public String generateLog(List<SensorParamViewReq> request){
        return stationDao.generateLog(request);
    }

    @Override
    public StationStatus getAllOnlineStations() {
        StationStatus stationStatus = new StationStatus();
        stationStatus.setStationId(headerDataStatusRepo.getAllOnlineStations());
        stationStatus.setStation(headerDataStatusRepo.getAllStationStatus());
        return stationStatus;
    }
}
