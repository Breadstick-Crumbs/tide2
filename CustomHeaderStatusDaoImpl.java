package com.tridel.tems_data_service.dao;

import com.tridel.tems_data_service.exception.TemsCustomException;
import com.tridel.tems_data_service.model.response.GenericResponse;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

import static com.tridel.tems_data_service.util.CommonUtil.LOAD_STATIONS_FAILED;

@Repository
@Slf4j
public class CustomHeaderStatusDaoImpl implements CustomHeaderStatusDao{
    EntityManager entityManager;
    CustomHeaderStatusDaoImpl(EntityManager entityManager){
        this.entityManager= entityManager;

    }
    @Override
    public Map<String,List<GenericResponse>> getHeaderDataStatusValuesForParams(String columns,List<Integer> stations) {
        log.info("getHeaderDataStatusValuesForParams entry");
        Map<String, List<GenericResponse>> paramMap = new HashMap<>();
        try {
            String[] paramsSeparate = columns.split(",");
            String stationsString = stations.stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining(","));
            String queryStr = "SELECT " + columns + ",parameter_datetime,parameter_stationid FROM tems_header_data_status where parameter_stationid in (" + stationsString + ")";
            Query query = entityManager.createNativeQuery(queryStr);
            log.debug("getHeaderDataStatusValuesForParams queryStr" + queryStr);
            List<Object[]> result = query.getResultList();
            result.forEach(row -> {
                List<GenericResponse> parameterList = new ArrayList<>();
                for (int i = 0; i < paramsSeparate.length; i++) {
                    GenericResponse cell = new GenericResponse();
                    if (row[i] != null) {
                        cell.setCName(paramsSeparate[i]);
                        cell.setCVal(row[i].toString());
                        parameterList.add(cell);
                    }
                }
                GenericResponse dateCell = new GenericResponse();
                dateCell.setCName("stationId");
                dateCell.setCVal(row[row.length - 1].toString());
                parameterList.add(dateCell);
                dateCell = new GenericResponse();
                dateCell.setCName("paramDate");
                dateCell.setCVal(row[row.length - 2].toString().substring(0, 19));
                parameterList.add(dateCell);
                paramMap.put(row[row.length - 1].toString(), parameterList);
            });
            log.info("getHeaderDataStatusValuesForParams exit");
        }catch(Exception e){
            throw new TemsCustomException(LOAD_STATIONS_FAILED);
        }
        return paramMap;
    }


}
