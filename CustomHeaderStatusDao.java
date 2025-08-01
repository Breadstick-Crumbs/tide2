package com.tridel.tems_data_service.dao;

import com.tridel.tems_data_service.model.response.GenericResponse;

import java.util.List;
import java.util.Map;

public interface CustomHeaderStatusDao {
    Map<String,List<GenericResponse>> getHeaderDataStatusValuesForParams(String columns, List<Integer> stations);


}
