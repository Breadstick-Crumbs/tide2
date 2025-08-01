package com.tridel.tems_data_service.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ParameterListPojo {
    private Integer paramId;
    private String paramName;
    private String paramCode;
    private Double min;
    private Double max;
    private Integer displayRoundTo;
    private String unitSymbol;
    private String sensorCode;
    private String sensorTableName;
    private String sensorTableCode;
    private String parameterDisplayName;
    private String operation;
    private String calculatedValue;
}
