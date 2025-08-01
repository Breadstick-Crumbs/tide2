package com.tridel.tems_data_service.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SummaryDataRequest {
    private List<ParamPojoReq> paramReqList;
    private String stationIds;
    private String fromDate;
    private String toDate;
}
