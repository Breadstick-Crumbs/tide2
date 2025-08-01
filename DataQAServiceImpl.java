package com.tridel.tems_data_service.service;

import com.tridel.tems_data_service.dao.DataQADao;
import com.tridel.tems_data_service.exception.TemsBadRequestException;
import com.tridel.tems_data_service.model.request.EditQCRequest;
import com.tridel.tems_data_service.model.request.QCDataEdit;
import com.tridel.tems_data_service.model.request.ReportRequest;
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



}
