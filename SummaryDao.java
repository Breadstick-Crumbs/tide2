package com.tridel.tems_data_service.dao;

import com.tridel.tems_data_service.model.request.ParamPojoReq;

import java.util.List;

public interface SummaryDao {
    String getSummaryData(String fromDate, String toDate, List<ParamPojoReq> req, String stationIds);
}
