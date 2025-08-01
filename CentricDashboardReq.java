package com.tridel.tems_data_service.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CentricDashboardReq {
    private List<Integer> stationIdList;
    private SensorParamView param;
    private String sensorTableCode;
}
