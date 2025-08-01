package com.tridel.tems_data_service.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SensorParamViewReq {

    private Integer stationId;
    private String sensorCode;
    private String params;
    private String sensorTableCode;
    private String fromDate;
    private String toDate;
    private String standardTime;
    private boolean isWeather;
}
