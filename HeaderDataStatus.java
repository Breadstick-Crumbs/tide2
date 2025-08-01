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
@Table(name="tems_header_data_status",uniqueConstraints={@UniqueConstraint(columnNames = {"parameter_stationid"})},
		indexes = @Index(name = "header_status_idx", columnList = "parameter_stationid"))
public class HeaderDataStatus {
	@Id
	@GeneratedValue(strategy= GenerationType.IDENTITY)
	@Column(name = "header_data_status_id")
	private Integer headerDataStatusId;

	@Column(name="parameter_stationid")
	private Integer stationId;

	@Column(name="parameter_datetime")
	private Date dateTime;

	@Column(name="parameter_datetime_utc")
	private Date dateTimeUtc;

	@Column(name="parameter_recv_datetime")
	private Date receivedDateTime;

	@Column(name="param1")
	private String param1;

	@Column(name="param2")
	private String param2;

	@Column(name="param3")
	private String param3;

	@Column(name="param4")
	private String param4;

	@Column(name="param5")
	private String param5;

	@Column(name="param6")
	private String param6;

	@Column(name="param7")
	private String param7;

	@Column(name="param8")
	private String param8;

	@Column(name="param9")
	private String param9;

	@Column(name="param10")
	private String param10;

	@Column(name="param11")
	private String param11;

	@Column(name="param12")
	private String param12;

	@Column(name="data_flag")
	private Boolean dataFlag;

	@Column(name="communication_type_id", columnDefinition = "integer default 1")
	private Integer communicationTypeId;

}
