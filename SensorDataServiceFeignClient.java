package com.tridel.tems_data_service.service;

import com.tridel.tems_data_service.model.response.ParameterListPojo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(name="tems-sensor-service")
public interface SensorDataServiceFeignClient {
    @GetMapping("/api/param/getParamList")
    List<ParameterListPojo> getParamListDataFromSensorDataService();
}
