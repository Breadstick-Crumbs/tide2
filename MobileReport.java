package com.tridel.tems_data_service.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MobileReport {
	private Double value;
	private String paramName;
	private String dateTime;
	private String unit;

	public LocalDateTime getParsedDateTime() {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
		return LocalDateTime.parse(dateTime, formatter);
	}
}
