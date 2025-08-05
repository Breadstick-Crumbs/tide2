package com.tridel.tems_data_service.controller;


import com.tridel.tems_data_service.model.request.DeleteQCRequest;
import com.tridel.tems_data_service.model.request.EditQCRequest;
import com.tridel.tems_data_service.model.request.ReportRequest;
import com.tridel.tems_data_service.service.DataQAService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dataqa")
@CrossOrigin
@SecurityRequirement(name = "Authorization")
@Tag(name = "Data QA/QC Controller", description = "Comprises of Data QA/QC service")
public class DataQAController {
    DataQAService dataQAService;
    DataQAController(DataQAService dataQAService){
        this.dataQAService = dataQAService;
    }

    @PostMapping("/loadAllQCData")
    public ResponseEntity<String> loadAllQCData(@RequestBody ReportRequest req) {
        return ResponseEntity
                .ok()
                .body(dataQAService.loadAllQCData(req));
    }


    @PostMapping("/editQCData")
    public ResponseEntity<String> editQCData(@RequestBody EditQCRequest req) {
        return ResponseEntity
                .ok()
                .body(dataQAService.editQCData(req));
    }

    @PostMapping("/deleteQCData")
    public ResponseEntity<String> deleteQCData(@RequestBody DeleteQCRequest req) {
        return ResponseEntity
                .ok()
                .body(dataQAService.deleteQCData(req));
    }


}
