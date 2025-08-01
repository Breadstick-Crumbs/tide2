package com.tridel.tems_data_service.dao;

import com.tridel.tems_data_service.model.request.SensorParamViewReq;
import com.tridel.tems_data_service.service.CommonService;
import jakarta.persistence.Tuple;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
@Slf4j
public class StationDaoImpl implements StationDao{
    CommonService commonService;
    StationDaoImpl(CommonService commonService){
        this.commonService = commonService;
    }
    @Override
    public String generateLog(List<SensorParamViewReq> sensors) {
        JSONArray arr = new JSONArray();
        if (!sensors.isEmpty()) {
            String id = String.valueOf(sensors.getFirst().getStationId());
            StringBuilder query = new StringBuilder("select CASE WHEN DATEPART(SECOND, CONVERT(VARCHAR, hd.parameter_datetime, 121)) >= 30 THEN  CONVERT(VARCHAR, DATEADD(MINUTE, 1, hd.parameter_datetime), 103) + ' ' +\n" +
                    "CONVERT(VARCHAR(5), DATEADD(MINUTE, 1, hd.parameter_datetime), 108) ELSE  CONVERT(VARCHAR, hd.parameter_datetime, 103) + ' '+ CONVERT(VARCHAR(5), hd.parameter_datetime, 108) END as parameter_datetime");
            for (SensorParamViewReq stationSensor : sensors) {
                query.append(", (case when (select count(*) from ").append(stationSensor.getSensorTableCode()).append(" where header_data_id = hd.header_data_id) is not null then 'Ok' else '-' end) as '").append(stationSensor.getSensorCode()).append("'");
            }
            query.append(" from tems_header_data hd inner join tems_header_data_status hld on hld.parameter_stationid = hd.parameter_stationid where hd.parameter_datetime >= DATEADD(HOUR, -24, (select h.parameter_datetime from tems_header_data_status h where h.parameter_stationid = hd.parameter_stationid)) and hd.parameter_datetime <= (select h.parameter_datetime from tems_header_data_status h where h.parameter_stationid = hd.parameter_stationid) and hd.parameter_stationid = ").append(id);
            List<Tuple> tuples = commonService.executeQueryString(query.toString());
            if(!tuples.isEmpty()){
                for (Tuple tup: tuples) {
                    JSONObject object = new JSONObject();
                    object.put("DateTime", tup.get(0));
                    for (int i=0; i< sensors.size(); i++) {
                        object.put(sensors.get(i).getSensorCode(),tup.get(i+1));
                    }
                    arr.put(object);
                }
            }else{
                JSONObject object = new JSONObject();
                object.put("Message", "No logs found for the station");
                arr.put(object);
            }
        }
        return arr.toString();
    }
}
