package com.tridel.tems_data_service.dao;

import com.tridel.tems_data_service.model.request.SensorParamViewReq;
import com.tridel.tems_data_service.model.request.StationDtlRequest;
import com.tridel.tems_data_service.model.request.StatisticsDataRequest;
import com.tridel.tems_data_service.model.response.CommunicationPojo;
import com.tridel.tems_data_service.model.response.GenericResponse;

import java.util.List;
import java.util.Map;

public interface ParamDao {
    List<GenericResponse> getSensorParamValuesForStation(SensorParamViewReq req);

    Double getLatestDataForHomeParam(StatisticsDataRequest req);
    Map<Integer, CommunicationPojo> getHeaderDtlsForComm(StationDtlRequest req);
}
