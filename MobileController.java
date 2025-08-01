package com.tridel.tems_data_service.controller;

import com.tridel.tems_data_service.model.request.ReportRequest;
import com.tridel.tems_data_service.model.request.Request;
import com.tridel.tems_data_service.model.response.MobileReport;
import com.tridel.tems_data_service.service.MobileService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mobileapi")
@CrossOrigin
@SecurityRequirement(name = "Authorization")
@Tag(name = "Mobile Controller", description = "Comprise of all the Mobile API services")
public class MobileController {

    MobileService mobileSvc;

    MobileController(MobileService mobileSvc){this.mobileSvc=mobileSvc;}


    @PostMapping("/getStationStatus")
    public ResponseEntity<String> getStationStatus(@RequestBody Request request) {
            return ResponseEntity.ok()
                    .body(mobileSvc.getStationStatus(request));
    }

    @PostMapping("/loadReportsData")
    public ResponseEntity<List<MobileReport>> loadReportsData(@RequestBody ReportRequest request) {
        return ResponseEntity.ok()
                .body(mobileSvc.loadReportsData(request));
    }

    @PostMapping("/loadReportsMinData")
    public ResponseEntity<List<MobileReport>> loadReportsMinData(@RequestBody ReportRequest request) {
        return ResponseEntity.ok()
                .body(mobileSvc.loadReportsMinData(request));
    }
}
