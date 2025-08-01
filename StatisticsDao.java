package com.tridel.tems_data_service.dao;

import com.tridel.tems_data_service.model.request.*;
import com.tridel.tems_data_service.model.response.StationResponse;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.ParseException;

public interface StatisticsDao {
    String loadParameterDataGraph(StatisticsDataRequest request);

    JSONArray getGraphData(StatisticsParamDataRequest request);

    JSONObject loadWindRose(String fDate, String tDate, StatisticsWindroseParam pm, StationResponse station) throws ParseException;
}
