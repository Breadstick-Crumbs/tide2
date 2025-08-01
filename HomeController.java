package com.tridel.tems_data_service.controller;

import com.tridel.tems_data_service.model.request.StationDtlRequest;
import com.tridel.tems_data_service.model.request.SensorParamViewReq;
import com.tridel.tems_data_service.model.request.StatisticsDataRequest;
import com.tridel.tems_data_service.model.response.GenericResponse;
import com.tridel.tems_data_service.service.HomeService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/data")
@CrossOrigin
@SecurityRequirement(name = "Authorization")
@Tag(name = "Home Controller", description = "Comprise of all the home page APIs.")
public class HomeController {
    HomeService homeService;
    HomeController(HomeService homeService){
        this.homeService = homeService;
    }
    @PostMapping("/loadAllStations")
    public ResponseEntity<Map<String,List<GenericResponse>>> loadAllStations(@RequestBody StationDtlRequest request) {
        return ResponseEntity
                .ok()
                .body(homeService.getHeaderDataDtlsForStations(request.getParams(),request.getStationIds()));
    }
    @PostMapping("/getSensorDataDetailsForHome")
    public ResponseEntity<List<GenericResponse>> getSensorParamValuesForStation(@RequestBody SensorParamViewReq request) {
        return ResponseEntity
                .ok()
                .body(homeService.getSensorParamValuesForStation(request));
    }
    @PostMapping("/getLatestDataForHomeParam")
    public ResponseEntity<Double> getLatestDataForHomeParam(@RequestBody StatisticsDataRequest request) {
        return ResponseEntity
                .ok()
                .body(homeService.getLatestDataForHomeParam(request));
    }


}
