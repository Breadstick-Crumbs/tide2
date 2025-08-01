package com.tridel.tems_data_service.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SummaryMapResponse {
    private String paramName;
    private Integer stationId;
    private Double min;
    private Double max;
    private Double avg;
    private LocalDateTime minDate;
    private LocalDateTime maxDate;

}
