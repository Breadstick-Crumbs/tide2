package com.tridel.tems_data_service.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StatisticsDataRequest {
    private Integer stationId;
    private Integer loggedIn;
    private String sensorCode;
    private SensorParamView paramDtls;
    private String sensorTableCode;
    private String fromDate;
    private String toDate;
}
