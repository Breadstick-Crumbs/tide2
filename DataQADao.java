package com.tridel.tems_data_service.dao;

import com.tridel.tems_data_service.model.request.DeleteQCRequest;
import com.tridel.tems_data_service.model.request.EditQCRequest;
import com.tridel.tems_data_service.model.request.ReportRequest;
import com.tridel.tems_data_service.model.request.SensorParamViewReq;

public interface DataQADao {

    String loadAllQCData(ReportRequest request);

    String editQCData(EditQCRequest req);

    int deleteQCData(DeleteQCRequest req);



}
