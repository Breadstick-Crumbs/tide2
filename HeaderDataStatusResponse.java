package com.tridel.tems_data_service.model.response;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
public class HeaderDataStatusResponse {
    private String gpsLatitude;
    private String gpsLongitude;
    private Date dateTime;
    //private Boolean switchState;
    private Integer stationId;

    public HeaderDataStatusResponse(String gpsLatitude, String gpsLongitude, Date dateTime, Integer stationId) {
        this.gpsLatitude = gpsLatitude;
        this.gpsLongitude = gpsLongitude;
        this.dateTime = dateTime;
        this.stationId = stationId;
    }
}
