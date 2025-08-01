package com.tridel.tems_data_service.controller;

import com.tridel.tems_data_service.model.request.ReportRequest;
import com.tridel.tems_data_service.service.ReportService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/datareport")
@CrossOrigin
@SecurityRequirement(name = "Authorization")
@Tag(name = "Report Controller", description = "Comprises of all reports related services")
public class ReportController {

    ReportService reportService;

    ReportController(ReportService reportService){
        this.reportService = reportService;
    }
    @PostMapping("/loadReportsDataOneMinute")
    public ResponseEntity<String> loadReportsDataOneMinute(@RequestBody ReportRequest request){
        return ResponseEntity
                .ok()
                .body(reportService.loadReportsDataOneMinute(request));
    }
    /*@PostMapping("/loadReportsDataByInterval")
    public ResponseEntity<String> loadReportsDataByInterval(@RequestBody ReportRequest request){
        return ResponseEntity
                .ok()
                .body(reportService.loadReportsDataByInterval(request));
    }*/
    @PostMapping("/loadReportsDataHourly")
    public ResponseEntity<String> loadReportsDataHourly(@RequestBody ReportRequest request){
        return ResponseEntity
                .ok()
                .body(reportService.loadReportsDataHourly(request));
    }
    @PostMapping("/loadClimateReportsDataOneMin")
    public ResponseEntity<String> loadClimateReportsDataOneMin(@RequestBody ReportRequest request){
        return ResponseEntity
                .ok()
                .body(reportService.loadClimateReportsDataOneMin(request));
    }
    @PostMapping("/loadClimateReportsDataInterval")
    public ResponseEntity<String> loadClimateReportsDataInterval(@RequestBody ReportRequest request){
        return ResponseEntity
                .ok()
                .body(reportService.loadClimateReportsDataInterval(request));
    }
    @PostMapping("/getReportByInterval")
    public ResponseEntity<String> getReportByInterval(@RequestBody ReportRequest request){
        return ResponseEntity
                .ok()
                .body(reportService.getReportByInterval(request));
    }

    @PostMapping("/getClimateReportByInterval")
    public ResponseEntity<String> getClimateReportByInterval(@RequestBody ReportRequest request){
        return ResponseEntity
                .ok()
                .body(reportService.getClimateReportByInterval(request));
    }

}
