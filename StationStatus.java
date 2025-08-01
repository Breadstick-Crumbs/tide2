package com.tridel.tems_data_service.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StationStatus {
    List<CentricDataResponse> station;
    List<Integer> stationId;
}
