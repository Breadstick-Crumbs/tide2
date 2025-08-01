package com.tridel.tems_data_service.service;

import com.tridel.tems_data_service.model.request.SensorParamView;
import jakarta.persistence.*;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.tridel.tems_data_service.util.CommonUtil.dateFormat2;

@Service
public class CommonService {
    @PersistenceContext
    EntityManager entityManager;
    public List<String> executeQueryStr(String query) {
        List<String> response = new ArrayList<>();
        try {
            Query q = entityManager.createNativeQuery(query, String.class);
            response = q.getResultList();

        } catch (NoResultException ignored) {
        }
        return response;
    }
    public Double executeQueryStringForDouble(String query) {
        Double response = null;
        try {
            Query q = entityManager.createNativeQuery(query);
            response = (Double) q.getSingleResult();
        } catch (NoResultException ignored) {
        }
        return response;
    }
    public List<Tuple> executeQueryString(String query) {
        List<Tuple> response = new ArrayList<>();
        try {
            Query q = entityManager.createNativeQuery(query, Tuple.class);
            response = q.getResultList();

        } catch (NoResultException ignored) {
        }
        return response;
    }

    public Map<Integer, List<Double>> executeQueryStrBatch(String query) {
        Map<Integer, List<Double>> stationDataMap = new HashMap<>();
        try {
            Query q = entityManager.createNativeQuery(query);
            List<Object[]> resultList = q.getResultList();

            for (Object[] row : resultList) {
                Integer stationId = (Integer) row[0];
                Double dataValue = (Double) row[1];

                stationDataMap.computeIfAbsent(stationId, k -> new ArrayList<>()).add(dataValue);
            }

        } catch (NoResultException ignored) {
        }

        return stationDataMap;
    }
    public static void getParamStrToQuery(List<SensorParamView> paramList, StringBuilder paramStr) {
        for (SensorParamView pm : paramList) {
            String operation = pm.getOperation();
            Integer roundTo = pm.getDisplayRoundTo() != null ? pm.getDisplayRoundTo() : 1;
            String paramToCreate;
            if (operation != null && operation.equalsIgnoreCase("(* 1.8)+32")) {
                paramToCreate = String.format(",ROUND((%s * 1.8) + 32, %d) as %s", pm.getDataParamName(), roundTo, pm.getDataParamName());
            } else {
                String operationPart = (operation != null) ? pm.getOperation() + pm.getCalculatedValue() : "";
                paramToCreate = String.format(",ROUND((%s %s), %d) as %s", pm.getDataParamName(), operationPart, roundTo, pm.getDataParamName());
            }
            paramStr.append(paramToCreate);
        }
    }

    public static String getFormattedDate(Date date) {
        return dateFormat2.format(date);
    }
}
