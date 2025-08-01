package com.tridel.tems_data_service.dao;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.tridel.tems_data_service.exception.TemsCustomException;
import com.tridel.tems_data_service.model.request.CentricDashboardReq;
import com.tridel.tems_data_service.model.request.ParamPojoReq;
import com.tridel.tems_data_service.model.request.SensorParamView;
import com.tridel.tems_data_service.model.response.CentricDataResponse;
import com.tridel.tems_data_service.model.response.HeaderDataStatusResponse;
import com.tridel.tems_data_service.service.CommonService;
import jakarta.persistence.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.tridel.tems_data_service.service.CommonService.getParamStrToQuery;


@Repository
@Slf4j
public class DashboardDaoImpl implements DashboardDao{
    @PersistenceContext
    EntityManager entityManager;
    private DataSource dataSource;
    DashboardDaoImpl(EntityManager entityManager,DataSource dataSource){
        this.entityManager= entityManager;
        this.dataSource = dataSource;
    }
    @Override
    public HeaderDataStatusResponse getHeaderDataStatusValues(Integer station) {
        HeaderDataStatusResponse response = new HeaderDataStatusResponse();
        try{
            String queryStr = "SELECT parameter_datetime,parameter_stationid FROM tems_header_data_status where parameter_stationid ="+station;
            Query query = entityManager.createNativeQuery(queryStr);
            log.debug("getHeaderDataStatusValues queryStr" + queryStr);
            Object result = query.getSingleResult();
            Object[] row = (Object[]) result;
            response.setStationId((Integer)row[row.length - 1]);
            response.setDateTime((Date)row[row.length - 2]);

        }catch(Exception e){
            if(e instanceof NoResultException)
                return new HeaderDataStatusResponse();
            throw new TemsCustomException("Header Data Status fetch failed");
        }
        return response;
    }
    @Override
    public String loadDashboardDataForStation(String fromDate, String toDate, List<ParamPojoReq> paramReqList, String stationId,String tableCode) {

        String stArray = "";
        log.info("loadDashboardDataForStation entry");
        HeaderDataStatusResponse response = getHeaderDataStatusValues(Integer.valueOf(stationId));
        JSONArray jsonArray = new JSONArray();
        if(response!= null && response.getDateTime() != null) {
            for (ParamPojoReq paramPojoReq : paramReqList) {
                jsonArray.put(paramPojoReq.toJson());
            }
            StringBuilder procedure = new StringBuilder("EXEC tems_daily_dashboard_data_function '" + jsonArray.toString() + "','" + stationId + "','" + fromDate + "','" + toDate + "','" + tableCode + "'");
            System.out.println("procedure" + procedure);
            procedure.trimToSize();
            try {
                List<Object> list = entityManager.createNativeQuery(procedure.toString()).getResultList();
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
                stArray = objectMapper.writeValueAsString(list);

            } catch (NoResultException ignored) {
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
        log.info("loadDashboardDataForStation exit");
        return stArray;
    }
    @Override
    public String loadDashboardDataForMET(List<ParamPojoReq> paramReqList, String stationId, String interval,String tableCode) {
        HeaderDataStatusResponse headerData = getHeaderDataStatusValues(Integer.valueOf(stationId));
        JSONArray jsonArray = new JSONArray();
        String stArray = "";
        if(headerData!= null && headerData.getDateTime() != null) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(headerData.getDateTime());
            String toDate = CommonService.getFormattedDate(cal.getTime());
            int adjustment = switch (interval) {
                case "2" -> -2;
                case "10" -> -10;
                case "Hourly" -> -60;
                default -> 0;
            };
            cal.add(Calendar.MINUTE, adjustment);
            String fromDate = CommonService.getFormattedDate(cal.getTime());
            for (ParamPojoReq paramPojoReq : paramReqList) {
                jsonArray.put(paramPojoReq.toJson());
            }
            StringBuilder procedure = new StringBuilder("EXEC tems_daily_dashboard_data_function '" + jsonArray.toString() + "','" + stationId + "','" + fromDate + "','" + toDate + "','" + tableCode + "'");
            System.out.println("procedure" + procedure);
            procedure.trimToSize();
            try {
                List<Object> list = entityManager.createNativeQuery(procedure.toString()).getResultList();
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
                stArray = objectMapper.writeValueAsString(list);

            } catch (NoResultException ignored) {
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
        return stArray;
    }
    @Override
    public List<CentricDataResponse> getLatestDataForCentricDashboard(CentricDashboardReq req) {
        List<CentricDataResponse> dataResponseList= new ArrayList<>();
        List<SensorParamView> paramList = new ArrayList<>();
        StringBuilder paramStr = new StringBuilder();
        paramList.add(req.getParam());
        String station = StringUtils.join(req.getStationIdList(),",");
        log.info("getLatestDataForCentricDashboard entry");
        try {
            getParamStrToQuery(paramList, paramStr);
            String queryStr = "SELECT station_id," +
                    "CASE WHEN DATEPART(SECOND, CONVERT(VARCHAR, parameter_datetime, 121)) >= 30 THEN "
                    + "CONVERT(VARCHAR, DATEADD(MINUTE, 1, parameter_datetime), 103) + ' ' + "
                    + "CONVERT(VARCHAR(5), DATEADD(MINUTE, 1, parameter_datetime), 108) ELSE "
                    + "CONVERT(VARCHAR, parameter_datetime, 103) + ' ' + "
                    + "CONVERT(VARCHAR(5), parameter_datetime, 108) END as parameter_datetime "
                    + paramStr + " FROM "+ req.getSensorTableCode()+
                    " where (is_data_removed = 'false' or is_data_removed is NULL) and station_id IN ("
                    + station + ") order by station_id,parameter_datetime asc";
            List<Tuple> results = entityManager.createNativeQuery(queryStr, Tuple.class).getResultList();
            dataResponseList=results.stream()
                    .map(tuple -> new CentricDataResponse(tuple.get(0, Integer.class), tuple.get(1, String.class),paramStr.toString(),tuple.get(2, Double.class)))
                    .toList();
        } catch (NoResultException ignored) {
        }
        log.info("getLatestDataForCentricDashboard exit");
        return dataResponseList;
    }


}
