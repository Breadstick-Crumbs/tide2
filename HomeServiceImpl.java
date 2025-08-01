package com.tridel.tems_data_service.service;

import com.tridel.tems_data_service.dao.ParamDao;
import com.tridel.tems_data_service.model.request.SensorParamViewReq;
import com.tridel.tems_data_service.model.request.StatisticsDataRequest;
import com.tridel.tems_data_service.model.response.GenericResponse;
import com.tridel.tems_data_service.repository.HeaderDataStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class HomeServiceImpl implements HomeService{
    HeaderDataStatusRepository headerDataRepository;
    ParamDao paramDao;
    @Autowired
    HomeServiceImpl(HeaderDataStatusRepository headerDataRepository,ParamDao paramDao){
        this.headerDataRepository = headerDataRepository;
        this.paramDao = paramDao;
    }
    @Override
    public Map<String,List<GenericResponse>> getHeaderDataDtlsForStations(String params, List<Integer> stationIdList){
        return headerDataRepository.getHeaderDataStatusValuesForParams(params,stationIdList);
    }
    @Override
    public List<GenericResponse> getSensorParamValuesForStation(SensorParamViewReq req){
        return paramDao.getSensorParamValuesForStation(req);
    }
    @Override
    public Double getLatestDataForHomeParam(StatisticsDataRequest req){
        return paramDao.getLatestDataForHomeParam(req);
    }


}
