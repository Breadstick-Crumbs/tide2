package com.tridel.tems_data_service.dao;

import com.tridel.tems_data_service.model.request.SensorParamViewReq;

import java.util.List;

public interface StationDao {
    String generateLog(List<SensorParamViewReq> sensors);
}
