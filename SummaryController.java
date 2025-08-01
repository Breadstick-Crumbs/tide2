package com.tridel.tems_data_service.controller;


import com.tridel.tems_data_service.model.request.DataRequest;
import com.tridel.tems_data_service.service.SummaryService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/datasummary")
@CrossOrigin
@SecurityRequirement(name = "Authorization")
@Tag(name = "Summary Controller", description = "Comprises of summary service")
public class SummaryController {
    SummaryService summaryService;
    SummaryController(SummaryService summaryService){
        this.summaryService = summaryService;
    }
    @PostMapping("/loadSummaryData")
    public ResponseEntity<String> loadSummaryData(@RequestBody DataRequest request){
        return ResponseEntity
                .ok()
                .body(summaryService.loadSummaryData(request));
    }
}
