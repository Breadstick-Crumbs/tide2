package com.tridel.tems_data_service.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QCDataEdit {
    private String  paramCode;
    private String  newValue;
    private Boolean isValid;
}