package com.tridel.tems_data_service.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReportRequest {

    private Integer stationId;
    private String sensorCode;
    private List<SensorParamView> paramList;
    private String sensorTableCode;
    private String fromDate;
    private String toDate;
    private String standardTime;
    private String station;
    private String interval;
    private Integer minIntrvl;
    private List<String> functions;
    private List<Boolean> containsFunctions;
}
