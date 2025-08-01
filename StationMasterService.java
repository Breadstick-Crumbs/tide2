package com.tridel.tems_data_service.service;

import com.tridel.tems_data_service.model.request.SensorParamViewReq;
import com.tridel.tems_data_service.model.request.StationDtlRequest;
import com.tridel.tems_data_service.model.response.CommunicationPojo;
import com.tridel.tems_data_service.model.response.StationStatus;

import java.util.List;
import java.util.Map;

public interface StationMasterService {
    Map<Integer, CommunicationPojo> getHeaderDtlsForComm(StationDtlRequest request);

    String generateLog(List<SensorParamViewReq> request);

    StationStatus getAllOnlineStations();
}
