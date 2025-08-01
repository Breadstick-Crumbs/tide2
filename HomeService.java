package com.tridel.tems_data_service.service;

import com.tridel.tems_data_service.model.request.SensorParamViewReq;
import com.tridel.tems_data_service.model.request.StatisticsDataRequest;
import com.tridel.tems_data_service.model.response.GenericResponse;

import java.util.List;
import java.util.Map;

public interface HomeService {
    Map<String,List<GenericResponse>> getHeaderDataDtlsForStations(String params, List<Integer> stationIds);

    List<GenericResponse> getSensorParamValuesForStation(SensorParamViewReq req);

    Double getLatestDataForHomeParam(StatisticsDataRequest req);
}
