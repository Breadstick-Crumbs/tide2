package com.tridel.tems_data_service.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.json.JSONObject;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ParamPojoReq {
    private String parameterName;
    private String operation;
    private BigDecimal calculatedValue;
    private Integer displayRoundTo;
    private Boolean isAverageParam;
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("parameterName", parameterName);
        json.put("operation", operation);
        json.put("calculatedValue", calculatedValue);
        json.put("displayRoundTo", displayRoundTo);
        json.put("isAverageParam", isAverageParam);
        return json;
    }
}
