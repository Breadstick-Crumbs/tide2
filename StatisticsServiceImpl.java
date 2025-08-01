package com.tridel.tems_data_service.service;

import com.tridel.tems_data_service.dao.StatisticsDao;
import com.tridel.tems_data_service.exception.TemsCustomException;
import com.tridel.tems_data_service.model.request.StatisticsDataRequest;
import com.tridel.tems_data_service.model.request.StatisticsParamDataRequest;
import com.tridel.tems_data_service.model.request.StatisticsWindroseParam;
import com.tridel.tems_data_service.model.response.StationResponse;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.text.ParseException;

@Service
public class StatisticsServiceImpl implements StatisticsService{
    StatisticsDao statisticsDao;
    StatisticsServiceImpl(StatisticsDao statisticsDao){
        this.statisticsDao = statisticsDao;

    }
    @Override
    public String loadParameterDataGraph(StatisticsDataRequest request) {
        return statisticsDao.loadParameterDataGraph(request);
    }

    @Override
    public String loadStatisticsData(StatisticsParamDataRequest request)  {
        StringBuilder response = new StringBuilder();
        JSONObject object = new JSONObject();
        try {
            if (request.getFromDate() == null || request.getToDate() == null)
                throw new TemsCustomException("Date not found");
            if (request.getStationIds() == null)
                throw new TemsCustomException("Station not found");

            if (request.getGraphParameters() != null) object.put("graph", statisticsDao.getGraphData(request));
            if (request.getWindRoseParams() != null) object.put("windrose", getWindRose(request));
            response = new StringBuilder(object.toString());
            response.trimToSize();

        }catch(Exception e){
            throw new TemsCustomException("Load Statistics data failed");
        }
        return response.toString();
    }

    private JSONArray getWindRose(StatisticsParamDataRequest request) throws ParseException {
        JSONArray arr = new JSONArray();
        for (StationResponse st : request.getStationIds()) {
            JSONArray starr = new JSONArray();
            JSONObject obj = new JSONObject();
            for (StatisticsWindroseParam pm : request.getWindRoseParams()) {
                JSONObject ob = statisticsDao.loadWindRose(request.getFromDate(), request.getToDate(), pm,st);
//                JSONObject ob = statisticsDao.loadWindRose(request.getFromDate(), request.getToDate(), pm.getParamName(),pm.getParameterOne(),pm.getParameterTwo(),pm.getWindRoseRanges(),st);
                arr.put(ob);

            }
//            obj.put("name",st.getStationName());
//            starr.put(obj);
//            arr.put(starr);
        }
        return arr;
    }
    @Override
    public String loadAdvStatisticsData(StatisticsParamDataRequest request)  {
        StringBuilder response = null;
        try {
            JSONArray array = statisticsDao.getGraphData(request);
            response = new StringBuilder(array.toString());
            response.trimToSize();
        }catch(Exception e){
            throw new TemsCustomException("Load Advanced Statistics data failed");
        }
        return response.toString();
    }
}
