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
@Table(name="tems_sensor_data_status_b",uniqueConstraints={@UniqueConstraint(columnNames = {"station_id", "parameter_datetime"})}
		,  indexes = @Index(name = "current_idx", columnList = "station_id, parameter_datetime"))
public class CurrentDataStatus {

	@Id
	@GeneratedValue(strategy= GenerationType.IDENTITY)
	@Column(name = "status_data_b_id",nullable = false)
	private Integer sensorBdataId;
	
	@Column(name="station_id")
	private Integer stationId;

	@ManyToOne
	@JoinColumn(name="header_data_status_id", referencedColumnName="header_data_status_id")
	private HeaderDataStatus headerDataId;

	@Column(name="communication_type_id", columnDefinition="integer default 1")
	private Integer communicationTypeId;

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

	@Column(name="param17")
	private Double param17;

	@Column(name="param18")
	private Double param18;

	@Column(name="param19")
	private Double param19;

	@Column(name="param20")
	private Double param20;

	@Column(name="param21")
	private Double param21;

	@Column(name="param22")
	private Double param22;

	@Column(name="param23")
	private Double param23;

	@Column(name="param24")
	private Double param24;

	@Column(name="param25")
	private Double param25;

	@Column(name="param26")
	private Double param26;

	@Column(name="param27")
	private Double param27;

	@Column(name="param28")
	private Double param28;

	@Column(name="param29")
	private Double param29;

	@Column(name="param30")
	private Double param30;

	@Column(name="param31")
	private Double param31;

	@Column(name="param32")
	private Double param32;

	@Column(name="param33")
	private Double param33;

	@Column(name="param34")
	private Double param34;

	@Column(name="param35")
	private Double param35;

	@Column(name="param36")
	private Double param36;

	@Column(name="param37")
	private Double param37;

	@Column(name="param38")
	private Double param38;

	@Column(name="param39")
	private Double param39;

	@Column(name="param40")
	private Double param40;

	@Column(name="is_data_removed")
	private Boolean isDataRemoved;
	
	@Column(name="qaqc_check")
	private Integer qaqcCkeck;
	
	@Column(name="prev_rec_id")
	private Integer prevRecId;
	
	@Column(name="is_valid")
	private Boolean isValid;

	@Column(name="qaqc_checked_datetime")
	private Date qaqcCheckedDate;



}
