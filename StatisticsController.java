package com.tridel.tems_data_service.controller;

import com.tridel.tems_data_service.model.request.StatisticsDataRequest;
import com.tridel.tems_data_service.model.request.StatisticsParamDataRequest;
import com.tridel.tems_data_service.service.StatisticsService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/datastat")
@CrossOrigin
@SecurityRequirement(name = "Authorization")
@Tag(name = "Statistics Controller", description = "Comprises of all statistics data service")
public class StatisticsController {
    StatisticsService statisticsService;
    StatisticsController(StatisticsService statisticsService){
        this.statisticsService = statisticsService;
    }
    @PostMapping("/loadParameterDataGraph")
    public ResponseEntity<String> loadParameterDataGraph(@RequestBody StatisticsDataRequest request){
        return ResponseEntity
                .ok()
                .body(statisticsService.loadParameterDataGraph(request));
    }
    @PostMapping("/loadStatisticsData")
    public ResponseEntity<String> loadStatisticsData(@RequestBody StatisticsParamDataRequest request){
        return ResponseEntity
                .ok()
                .body(statisticsService.loadStatisticsData(request));
    }
    @PostMapping("/loadAdvStatisticsData")
    public ResponseEntity<String> loadAdvStatisticsData(@RequestBody StatisticsParamDataRequest request){
        return ResponseEntity
                .ok()
                .body(statisticsService.loadAdvStatisticsData(request));
    }
}
