package com.tridel.tems_data_service.controller;

import com.tridel.tems_data_service.model.request.CentricDashboardReq;
import com.tridel.tems_data_service.model.request.ReportRequest;
import com.tridel.tems_data_service.model.request.DataRequest;
import com.tridel.tems_data_service.model.response.CentricDataResponse;
import com.tridel.tems_data_service.model.response.HeaderDataStatusResponse;
import com.tridel.tems_data_service.service.DashboardService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/datadashboard")
@CrossOrigin
@SecurityRequirement(name = "Authorization")
@Tag(name = "Dashboard Controller", description = "Comprise of all the Dashboard APIs.")
public class DashboardController {
    DashboardService dashboardService;
    DashboardController(DashboardService dashboardService){
        this.dashboardService = dashboardService;
    }
    @PostMapping("/loadHeaderData")
    public ResponseEntity<HeaderDataStatusResponse> loadHeaderData(@RequestBody ReportRequest request) {
        return ResponseEntity
                .ok()
                .body(dashboardService.getHeaderDataStatusValues(request));
    }
    @PostMapping("/loadDashboardDataForStation")
    public ResponseEntity<String> loadDashboardDataForStation(@RequestBody DataRequest request){
        return ResponseEntity
                .ok()
                .body(dashboardService.loadDashboardDataForStation(request));
    }
    @PostMapping("/loadDashboardDataForMET")
    public ResponseEntity<String> loadDashboardDataForMET(@RequestBody DataRequest request){
        return ResponseEntity
                .ok()
                .body(dashboardService.loadDashboardDataForMET(request));
    }
    @PostMapping("/loadCentricDashboardData")
    public ResponseEntity<List<CentricDataResponse>> loadCentricDashboardData(@RequestBody CentricDashboardReq request){
        return ResponseEntity
                .ok()
                .body(dashboardService.loadCentricDashboardData(request));
    }
}
