package com.tridel.tems_data_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name="tems_sensor_data_d",uniqueConstraints={@UniqueConstraint(columnNames = {"station_id", "parameter_datetime"})})
public class WaterQualityData {
	@Id
	@GeneratedValue(strategy= GenerationType.IDENTITY)
	@Column(name = "sensor_data_d_id",nullable = false)
	private Integer sensorDDataId;
	
	@Column(name="station_id")
	private Integer stationId;

	@Column(name="parameter_datetime")
	private Date dateTime;

	@Column(name="parameter_datetime_utc")
	private Date utcDateTime;
	
	@Column(name="parameter_recv_datetime")
	private Date receivedDateTime;

	@Column(name="param1")
	private Double param1;

	@Column(name="param2")
	private Double param2;

	@Column(name="param3")
	private Double param3;

	@Column(name="param4")
	private Double param4;

	@Column(name="param5")
	private Double param5;

	@Column(name="param6")
	private Double param6;

	@Column(name="param7")
	private Double param7;

	@Column(name="param8")
	private Double param8;

	@Column(name="param9")
	private Double param9;

	@Column(name="param10")
	private Double param10;

	@Column(name="param11")
	private Double param11;

	@Column(name="param12")
	private Double param12;

	@Column(name="param13")
	private Double param13;

	@Column(name="param14")
	private Double param14;

	@Column(name="param15")
	private Double param15;

	@Column(name="param16")
	private Double param16;

	@Column(name="qaqc_check")
	private Integer qaqcCkeck;
	
	@Column(name="prev_rec_id")
	private Integer prevRecId;

	@Column(name="is_valid")
	private Boolean isValid;

	@Column(name="qaqc_checked_datetime")
	private Date qaqcCheckedDate;

	@Column(name="is_data_removed")
	private Boolean isDataRemoved;

	@OneToOne
	@JoinColumn(name="header_data_id", referencedColumnName="header_data_id")
	private HeaderData headerDataId;

	@Column(name="communication_type_id", columnDefinition="integer default 1")
	private Integer communicationTypeId;

}
