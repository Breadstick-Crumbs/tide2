package com.tridel.tems_data_service.controller;


import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dataupload")
@CrossOrigin
@SecurityRequirement(name = "Authorization")
@Tag(name = "Data upload Controller", description = "Comprises of Data upload service")
public class DataUploadController {
//    SummaryService summaryService;
//    DataQAController(SummaryService summaryService){
//        this.summaryService = summaryService;
//    }
//    @PostMapping("/loadSummaryData")
//    public ResponseEntity<String> loadSummaryData(@RequestBody DataRequest request){
//        return ResponseEntity
//                .ok()
//                .body(summaryService.loadSummaryData(request));
//    }
}
