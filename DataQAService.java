package com.tridel.tems_data_service.service;


import com.tridel.tems_data_service.model.request.DeleteQCRequest;
import com.tridel.tems_data_service.model.request.EditQCRequest;
import com.tridel.tems_data_service.model.request.ReportRequest;

public interface DataQAService {
    String loadAllQCData(ReportRequest req);
    String editQCData(EditQCRequest req);
    String deleteQCData(DeleteQCRequest req);



}
