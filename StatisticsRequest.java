package com.tridel.tems_data_service.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StatisticsRequest {

    private String loggedIn;
    private List<SensorParamView> parameters;
    private List<String> windRoseParam;
    private List<Integer> stationId;
    private String fromDate;
    private String toDate;
    private String sensorTableCode;
}
