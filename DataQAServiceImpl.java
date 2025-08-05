package com.tridel.tems_data_service.service;

import com.tridel.tems_data_service.dao.DataQADao;
import com.tridel.tems_data_service.exception.TemsBadRequestException;
import com.tridel.tems_data_service.model.request.DeleteQCRequest;
import com.tridel.tems_data_service.model.request.EditQCRequest;
import com.tridel.tems_data_service.model.request.QCDataEdit;
import com.tridel.tems_data_service.model.request.ReportRequest;
import io.micrometer.common.util.StringUtils;
import com.tridel.tems_data_service.util.CommonUtil;

import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class DataQAServiceImpl implements DataQAService {

    DataQADao dataQADao;
    DataQAServiceImpl(DataQADao dataQADao){
        this.dataQADao = dataQADao;
    }


    @Override
    public String loadAllQCData  (ReportRequest request){
        return dataQADao.loadAllQCData(request);
    }

    @Override @Transactional
    public String editQCData(EditQCRequest req) {
        dataQADao.editQCData(req);
        return "rows Updated";
    }

    @Override
    @Transactional
    public String deleteQCData(DeleteQCRequest req) {

        if (StringUtils.isBlank(req.getLoggedIn()))
            throw new IllegalArgumentException(CommonUtil.USER_NOT_FOUND);
        if (req.getStationId() == null)
            throw new IllegalArgumentException(CommonUtil.STN_NOT_FOUND);

        /*––––– derive the table name only when the caller didn't send it –––––*/
        if (StringUtils.isBlank(req.getSensorTableCode())) {
            if (StringUtils.isBlank(req.getSensorCode()))
                throw new IllegalArgumentException("Sensor code not found");

            // A simple generic rule – identical to what you use in edit-QC
            req.setSensorTableCode("sensor_" + req.getSensorCode().toLowerCase());
            // …or a switch/map if you need special names
            // req.setSensorTableCode(TABLE_MAP.get(req.getSensorCode()));
        }

        int rows = dataQADao.deleteQCData(req);

        return rows == 0
                ? "No matching rows – nothing deleted"
                : "Row deleted successfully";
    }
}



