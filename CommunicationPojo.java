package com.tridel.tems_data_service.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommunicationPojo {
    private String batteryVoltage;
    private Date parameterDatetime;
}
