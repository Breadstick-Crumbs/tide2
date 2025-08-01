package com.tridel.tems_data_service.controller;

import com.tridel.tems_data_service.model.request.SensorParamViewReq;
import com.tridel.tems_data_service.model.request.StationDtlRequest;
import com.tridel.tems_data_service.model.response.CommunicationPojo;
import com.tridel.tems_data_service.model.response.StationStatus;
import com.tridel.tems_data_service.service.StationMasterService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/stnmaster")
@CrossOrigin
@SecurityRequirement(name = "Authorization")
@Tag(name = "Station Master Controller", description = "Comprises of all Station master related services")
public class StationMasterController {
    StationMasterService stationMasterService;
    StationMasterController(StationMasterService stationMasterService){
        this.stationMasterService = stationMasterService;
    }
    @PostMapping("/getStationCommunicationDetails")
    public ResponseEntity<Map<Integer, CommunicationPojo>> getStationCommunicationDetails(@RequestBody StationDtlRequest request) {
        return ResponseEntity
                .ok()
                .body(stationMasterService.getHeaderDtlsForComm(request));
    }
    @PostMapping("/generateLog")
    public ResponseEntity<String> generateLog(@RequestBody List<SensorParamViewReq> request) {
        return ResponseEntity
                .ok()
                .body(stationMasterService.generateLog(request));
    }

    @GetMapping("/onlineStations")
    public ResponseEntity<StationStatus> getAllOnlineStations() {
        return ResponseEntity
                .ok()
                .body(stationMasterService.getAllOnlineStations());
    }
}
