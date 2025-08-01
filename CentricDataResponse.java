package com.tridel.tems_data_service.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CentricDataResponse {
    private Integer stationId;
    private String date;
    private String dataParamName;
    private Double data;

    public CentricDataResponse(Integer stationId, String date) {
        this.stationId = stationId;
        this.date = date;
    }
}
