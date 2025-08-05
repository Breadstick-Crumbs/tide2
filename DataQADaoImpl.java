package com.tridel.tems_data_service.dao;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.tridel.tems_data_service.model.request.*;
import com.tridel.tems_data_service.service.CommonService;
import io.micrometer.common.util.StringUtils;
import jakarta.persistence.*;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Repository;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@Slf4j
public class DataQADaoImpl implements DataQADao{
    @PersistenceContext
    private EntityManager em;
    CommonService commonService;
    EntityManager entityManager;
    DataQADaoImpl(CommonService commonService, EntityManager entityManager){
        this.commonService = commonService;
        this.entityManager = entityManager;
    }


    @Override
    public String loadAllQCData(ReportRequest request) {
        String dataObj = null;
        StringBuilder paramStr = new StringBuilder();
        log.info("loadAllQCData entry");
        try {
            String columnName = "parameter_datetime";
            CommonService.getParamStrToQuery(request.getParamList(), paramStr);

            StringBuilder queryStr = new StringBuilder();
            queryStr.append("SELECT station_id, ").append(columnName).append(" ")
                    .append(paramStr)
                    .append(" FROM ").append(request.getSensorTableCode())
                    .append(" where (is_data_removed = 'false' or is_data_removed is NULL)")
                    .append(" and station_id = '").append(request.getStationId()).append("'")
                    .append(" and ").append(columnName).append(" >='").append(request.getFromDate()).append("'")
                    .append(" and ").append(columnName).append(" <='").append(request.getToDate()).append("'")
                    .append(" order by station_id,").append(columnName).append(" asc");

            log.info("loadAllQCData query " + queryStr);

            List<Object> list = entityManager
                    .createNativeQuery(queryStr.toString())
                    .getResultList();

            ObjectMapper mapper = new ObjectMapper();
            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
            mapper.enable(SerializationFeature.INDENT_OUTPUT);
            dataObj = mapper.writeValueAsString(list);

        } catch (NoResultException ignored) {
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        log.info("loadAllQCData exit");
        return dataObj;
    }

    @Override
    public String editQCData(EditQCRequest req) {

        Map<String, QCDataEdit> byParam = req.getEdits().stream()
                .collect(Collectors.toMap(QCDataEdit::getParamCode, e -> e, (a,b) -> b));

        StringBuilder setSql = new StringBuilder();
        Map<String,Object> params = new HashMap<>();

        byParam.values().forEach(e -> {
            if (e.getNewValue() != null) {
                setSql.append(e.getParamCode())
                        .append(" = :v_").append(e.getParamCode()).append(", ");
                params.put("v_" + e.getParamCode(), e.getNewValue());
            }
            if (e.getIsValid() != null) {
                String flag = e.getParamCode() + "_is_valid";
                setSql.append(flag)
                        .append(" = :iv_").append(e.getParamCode()).append(", ");
                params.put("iv_" + e.getParamCode(), e.getIsValid());
            }
        });

        if (setSql.isEmpty()) return "{\"status\":\"NO_CHANGE\"}";

        setSql.setLength(setSql.length() - 2);

        String sql = "UPDATE " + req.getSensorTableCode() +
                " SET " + setSql +
                " WHERE station_id = :stationId" +
                "   AND parameter_datetime = :dt";

        var q = entityManager.createNativeQuery(sql);
        params.forEach(q::setParameter);

        q.setParameter("stationId", req.getStationId());
        q.setParameter("dt",        req.getParamDatetime());

        int rows = q.executeUpdate();
        return "{\"rowsUpdated\":" + rows + "}";
    }

    @Override
    @Transactional
    public int deleteQCData(DeleteQCRequest req) {

        StringBuilder sql = new StringBuilder()
                .append("UPDATE ")
                .append(req.getSensorTableCode())
                .append(" SET is_data_removed = 1, qaqc_check = 2")
                .append(" WHERE station_id = :stationId")
                .append("   AND parameter_datetime = :dt");

        Query q = em.createNativeQuery(sql.toString())
                .setParameter("stationId", req.getStationId())
                .setParameter("dt",        req.getParameterDateTime());

        int rows = q.executeUpdate();

        log.info("User={} deleted QC row (table={}, station={}, dt={}) -> {} rows",
                req.getLoggedIn(), req.getSensorTableCode(),
                req.getStationId(), req.getParameterDateTime(), rows);

        return rows;
    }
}


