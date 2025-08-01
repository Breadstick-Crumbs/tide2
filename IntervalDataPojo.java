package com.tridel.tems_data_service.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class IntervalDataPojo {
    Integer stationId;
    LocalDateTime timestamp;
    Map<String, Double> parameters;

    public IntervalDataPojo(LocalDateTime timestamp, Map<String, Double> parameters) {
        this.timestamp = timestamp;
        this.parameters = parameters;
    }

    @Override
    public String toString() {
        return "IntervalDataPojo{stationId='" + stationId + "', timestamp=" + timestamp + ", parameters=" + parameters + "}";
    }
}
