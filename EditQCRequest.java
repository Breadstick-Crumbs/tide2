package com.tridel.tems_data_service.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EditQCRequest {
    private Integer loggedIn;
    private Integer stationId;
    private String  sensorCode;
    private String  sensorTableCode;
    private String  paramDatetime;
    private List<QCDataEdit> edits;
}
