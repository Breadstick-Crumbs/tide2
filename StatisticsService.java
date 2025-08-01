package com.tridel.tems_data_service.service;

import com.tridel.tems_data_service.model.request.StatisticsDataRequest;
import com.tridel.tems_data_service.model.request.StatisticsParamDataRequest;

public interface StatisticsService {
    String loadParameterDataGraph(StatisticsDataRequest request);

    String loadStatisticsData(StatisticsParamDataRequest request);

    String loadAdvStatisticsData(StatisticsParamDataRequest request);
}
