package com.tridel.tems_data_service.dao;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.tridel.tems_data_service.model.request.ParamPojoReq;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Slf4j
public class SummaryDaoImpl implements SummaryDao{
    @PersistenceContext
    EntityManager entityManager;

    @Override
    public String getSummaryData(String fromDate, String toDate, List<ParamPojoReq> paramReqList, String stationIds) {
        String arrayToJson = null;
        log.info("getSummaryData entry");
        JSONArray jsonArray = new JSONArray();
        for (ParamPojoReq paramPojoReq : paramReqList) {
            jsonArray.put(paramPojoReq.toJson());
        }
        StringBuilder procedure = new StringBuilder("EXEC tems_summary_data_function '"+jsonArray.toString()+"','"+stationIds+"','" +fromDate+"','"+toDate+"'");
        System.out.println("procedure"+procedure);
        procedure.trimToSize();
        try{
            List<Object[]> list = entityManager.createNativeQuery(procedure.toString()).getResultList();
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
            arrayToJson = objectMapper.writeValueAsString(list);

        }catch(NoResultException ignored){
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        log.info("getSummaryData exit");
        return arrayToJson;
    }

}
