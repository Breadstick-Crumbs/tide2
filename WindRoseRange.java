package com.tridel.tems_data_service.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WindRoseRange {
	private Integer windRoseRangeId;
	private Double scaleMin;
	private Double scaleMax;
}
