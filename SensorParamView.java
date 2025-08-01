package com.tridel.tems_data_service.model.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SensorParamView {

    private Integer paramId;
    private String paramName;
    private String paramCode;
    private Double min;
    private Double max;
    private Double warn;
    private Double danger;
    private Integer homeOrder;
    private Integer displayRoundTo;
    private Boolean appParamEnabled;
    private Integer paramUnitId;
    private Integer unitId;
    private String unitSymbol;
    private Integer sensorId;
    private String sensorCode;
    private String sensorName;
    private Integer sensorOrder;
    private String sensorTableName;
    private String sensorTableCode;
    private Boolean notifyFlag;
    private Integer graphYAxisMin;
    private String parameterDisplayName;
    private String data;
    private String loggedIn;
    private String operation;
    private String calculatedValue;
    private String dataParamName;




}
