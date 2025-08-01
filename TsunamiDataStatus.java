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
@Table(name="tems_sensor_data_status_f",uniqueConstraints={@UniqueConstraint(columnNames = {"station_id", "parameter_datetime"})})
public class TsunamiDataStatus {
	@Id
	@GeneratedValue(strategy= GenerationType.IDENTITY)
	@Column(name = "status_data_f_id",nullable = false)
	private Integer sensorFDataId;
	
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

	@ManyToOne
	@JoinColumn(name="header_data_status_id", referencedColumnName="header_data_status_id")
	private HeaderDataStatus headerDataId;

	@Column(name="communication_type_id", columnDefinition="integer default 1")
	private Integer communicationTypeId;

}
