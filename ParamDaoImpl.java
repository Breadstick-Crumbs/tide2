package com.tridel.tems_data_service.dao;

import com.tridel.tems_data_service.exception.TemsCustomException;
import com.tridel.tems_data_service.model.request.SensorParamViewReq;
import com.tridel.tems_data_service.model.request.StationDtlRequest;
import com.tridel.tems_data_service.model.request.StatisticsDataRequest;
import com.tridel.tems_data_service.model.response.CommunicationPojo;
import com.tridel.tems_data_service.model.response.GenericResponse;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.Query;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

import static com.tridel.tems_data_service.util.CommonUtil.FETCH_SENSOR_FAILED;

@Repository
@Slf4j
public class ParamDaoImpl implements ParamDao{
    EntityManager entityManager;
    ParamDaoImpl(EntityManager entityManager){
        this.entityManager= entityManager;
    }

    @Override
    public List<GenericResponse> getSensorParamValuesForStation(SensorParamViewReq req) {
        String[] paramsSeparate = req.getParams().split(",");
        List<GenericResponse> sensorData = new ArrayList<>();
        try {
            log.info("getSensorParamValuesForStation entry");
            String queryStr = "SELECT " + req.getParams() + ",parameter_datetime,station_id FROM " + req.getSensorTableCode() + " where station_id = " + req.getStationId();
            if(req.isWeather())
                queryStr = queryStr+" AND CAST(parameter_datetime AS DATE) = CAST(GETDATE() AS DATE)";
            Query query = entityManager.createNativeQuery(queryStr);
            log.debug("getSensorParamValuesForStation query" + queryStr);
            List<Object[]> result = query.getResultList();
            String[] finalParamsSeparate = paramsSeparate;
            result.forEach(row -> {
                for (int i = 0; i < finalParamsSeparate.length; i++) {
                    GenericResponse cell = new GenericResponse();
                    if (row[i] != null) {
                        cell.setCName(finalParamsSeparate[i]);
                        cell.setCVal(row[i].toString());
                        sensorData.add(cell);
                    }
                }
                GenericResponse dateCell = new GenericResponse();
                dateCell.setCName("stationId");
                dateCell.setCVal(row[row.length - 1].toString());
                sensorData.add(dateCell);
                dateCell = new GenericResponse();
                dateCell.setCName("paramDate");
                dateCell.setCVal(row[row.length - 2].toString().substring(0, 19));
                sensorData.add(dateCell);
            });
            log.info("getSensorParamValuesForStation exit");
        }catch(Exception e){
            log.debug("getSensorParamValuesForStation exception "+e.getMessage());
            throw new TemsCustomException(FETCH_SENSOR_FAILED);
        }
        paramsSeparate = null;
        return sensorData;

    }
    @Override
    public Double getLatestDataForHomeParam(StatisticsDataRequest req) {
        Double data = null;
        log.info("getLatestDataForHomeParam entry");
        try {
            StringBuilder query = new StringBuilder("SELECT " + req.getParamDtls().getDataParamName() + " FROM " + req.getSensorTableCode()+
                    " where station_id = " + req.getStationId() );
            query.trimToSize();
            log.debug("getLatestDataForHomeParam query"+query.toString());
            Query q = entityManager.createNativeQuery(query.toString(), Double.class);
            data = (Double) q.getSingleResult();
            query.delete(0, query.length());
            query = null;
        } catch (NoResultException ignored) {
        }
        log.info("getLatestDataForHomeParam exit");
        return data;
    }
    @Override
    public Map<Integer, CommunicationPojo> getHeaderDtlsForComm(StationDtlRequest req) {
        Map<Integer, CommunicationPojo> communicationPojoMap = new HashMap<>();
        try {
            log.info("getSensorParamValuesForStation entry");
            String stations = req.getStationIds().stream().map(String::valueOf).collect(Collectors.joining(","));
            String queryStr = "SELECT " + req.getParams() + ",parameter_datetime,parameter_stationid FROM tems_header_data_status where parameter_stationid IN (" + stations+")";
            Query query = entityManager.createNativeQuery(queryStr);
            log.debug("getSensorParamValuesForStation query" + queryStr);
            List<Object[]> result = query.getResultList();
            for (Object[] row : result) {
                CommunicationPojo communicationPojo = new CommunicationPojo();
                String batteryVolt = (String) row[0];
                Date dateValue = (Date) row[1];
                Integer stationId = (Integer) row[2];
                communicationPojo.setBatteryVoltage(batteryVolt);
                communicationPojo.setParameterDatetime(dateValue);
                communicationPojoMap.computeIfAbsent(stationId, k -> communicationPojo);
            }
        }catch(Exception e){
            log.debug("getSensorParamValuesForStation exception "+e.getMessage());
            throw new TemsCustomException(FETCH_SENSOR_FAILED);
        }
        return communicationPojoMap;
    }

}
