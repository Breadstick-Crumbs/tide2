package com.tridel.tems_data_service.model.request;

import com.tridel.tems_data_service.model.response.StationResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StatisticsParamDataRequest {
    private List<StationResponse> stationIds;
    private List<SensorParamView> graphParameters;
    private List<StatisticsWindroseParam> windRoseParams;
    private String sensorTableCode;
    private String fromDate;
    private String toDate;
}
