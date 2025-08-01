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
@Table(name="tems_sensor_data_log")
public class SensorDataLog {
	
	@Id
	@GeneratedValue(strategy= GenerationType.IDENTITY)
	@Column(name = "sensor_data_log_id",nullable = false)
	private Integer sensorDataLogId;

	@Column(name="station_id")
	private Integer stationId;
	
	@Column(name="received_datetime")
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
	
	@Column(name="param13")
	private String param13;

	@Column(name="param14")
	private String param14;

	@Column(name="param15")
	private String param15;

	@Column(name="param16")
	private String param16;

	@Column(name="param17")
	private String param17;

	@Column(name="param18")
	private String param18;

	@Column(name="param19")
	private String param19;

	@Column(name="param20")
	private String param20;

	@Column(name="param21")
	private String param21;

	@Column(name="param22")
	private String param22;

	@Column(name="param23")
	private String param23;

	@Column(name="param24")
	private String param24;

	@Column(name="param25")
	private String param25;

	@Column(name="param26")
	private String param26;

	@Column(name="param27")
	private String param27;

	@Column(name="param28")
	private String param28;

	@Column(name="param29")
	private String param29;

	@Column(name="param30")
	private String param30;

	@Column(name="param31")
	private String param31;

	@Column(name="param32")
	private String param32;

	@Column(name="param33")
	private String param33;

	@Column(name="param34")
	private String param34;

	@Column(name="param35")
	private String param35;

	@Column(name="param36")
	private String param36;

	@Column(name="param37")
	private String param37;

	@Column(name="param38")
	private String param38;

	@Column(name="param39")
	private String param39;

	@Column(name="param40")
	private String param40;

	@Column(name="param41")
	private String param41;

	@Column(name="param42")
	private String param42;

	@Column(name="param43")
	private String param43;

	@Column(name="param44")
	private String param44;

	@Column(name="param45")
	private String param45;

	@Column(name="param46")
	private String param46;

	@Column(name="param47")
	private String param47;

	@Column(name="param48")
	private String param48;

	@Column(name="param49")
	private String param49;

	@Column(name="param50")
	private String param50;

	@Column(name="param51")
	private String param51;

	@Column(name="param52")
	private String param52;

	@Column(name="param53")
	private String param53;

	@Column(name="param54")
	private String param54;

	@Column(name="param55")
	private String param55;

	@Column(name="param56")
	private String param56;

	@Column(name="param57")
	private String param57;

	@Column(name="param58")
	private String param58;

	@Column(name="param59")
	private String param59;

	@Column(name="param60")
	private String param60;

	@Column(name="param61")
	private String param61;

	@Column(name="param62")
	private String param62;

	@Column(name="param63")
	private String param63;

	@Column(name="param64")
	private String param64;

	@Column(name="param65")
	private String param65;

	@Column(name="param66")
	private String param66;

	@Column(name="param67")
	private String param67;

	@Column(name="param68")
	private String param68;

	@Column(name="param69")
	private String param69;

	@Column(name="param70")
	private String param70;

	@Column(name="param71")
	private String param71;

	@Column(name="param72")
	private String param72;

	@Column(name="param73")
	private String param73;

	@Column(name="param74")
	private String param74;

	@Column(name="param75")
	private String param75;

	@Column(name="param76")
	private String param76;

	@Column(name="param77")
	private String param77;

	@Column(name="param78")
	private String param78;

	@Column(name="param79")
	private String param79;

	@Column(name="param80")
	private String param80;

	@Column(name="param81")
	private String param81;

	@Column(name="param82")
	private String param82;

	@Column(name="param83")
	private String param83;

	@Column(name="param84")
	private String param84;

	@Column(name="param85")
	private String param85;

	@Column(name="param86")
	private String param86;

	@Column(name="param87")
	private String param87;

	@Column(name="param88")
	private String param88;

	@Column(name="param89")
	private String param89;

	@Column(name="param90")
	private String param90;

	@Column(name="param91")
	private String param91;

	@Column(name="param92")
	private String param92;

	@Column(name="param93")
	private String param93;

	@Column(name="param94")
	private String param94;

	@Column(name="param95")
	private String param95;

	@Column(name="param96")
	private String param96;

	@Column(name="param97")
	private String param97;

	@Column(name="param98")
	private String param98;

	@Column(name="param99")
	private String param99;

	@Column(name="param100")
	private String param100;

	@Column(name="param101")
	private String param101;

	@Column(name="param102")
	private String param102;

	@Column(name="param103")
	private String param103;

	@Column(name="param104")
	private String param104;

	@Column(name="param105")
	private String param105;

	@Column(name="param106")
	private String param106;

	@Column(name="param107")
	private String param107;

	@Column(name="param108")
	private String param108;

	@Column(name="param109")
	private String param109;

	@Column(name="param110")
	private String param110;

	@Column(name="param111")
	private String param111;

	@Column(name="param112")
	private String param112;

	@Column(name="param113")
	private String param113;

	@Column(name="param114")
	private String param114;

	@Column(name="param115")
	private String param115;

	@Column(name="param116")
	private String param116;

	@Column(name="param117")
	private String param117;

	@Column(name="param118")
	private String param118;

	@Column(name="param119")
	private String param119;

	@Column(name="param120")
	private String param120;

	@Column(name="param121")
	private String param121;

	@Column(name="param122")
	private String param122;

	@Column(name="param123")
	private String param123;

	@Column(name="param124")
	private String param124;

	@Column(name="param125")
	private String param125;

	@Column(name="param126")
	private String param126;

	@Column(name="param127")
	private String param127;

	@Column(name="param128")
	private String param128;

	@Column(name="param129")
	private String param129;

	@Column(name="param130")
	private String param130;

	@Column(name="param131")
	private String param131;

	@Column(name="param132")
	private String param132;

	@Column(name="param133")
	private String param133;

	@Column(name="param134")
	private String param134;

	@Column(name="param135")
	private String param135;

	@Column(name="param136")
	private String param136;

	@Column(name="param137")
	private String param137;

	@Column(name="param138")
	private String param138;

	@Column(name="param139")
	private String param139;

	@Column(name="param140")
	private String param140;

	@Column(name="param141")
	private String param141;

	@Column(name="param142")
	private String param142;

	@Column(name="param143")
	private String param143;

	@Column(name="param144")
	private String param144;

	@Column(name="param145")
	private String param145;

	@Column(name="param146")
	private String param146;

	@Column(name="param147")
	private String param147;

	@Column(name="param148")
	private String param148;

	@Column(name="param149")
	private String param149;

	@Column(name="param150")
	private String param150;

	@Column(name="param151")
	private String param151;

	@Column(name="param152")
	private String param152;

	@Column(name="param153")
	private String param153;

	@Column(name="param154")
	private String param154;

	@Column(name="param155")
	private String param155;

	@Column(name="param156")
	private String param156;

	@Column(name="param157")
	private String param157;

	@Column(name="param158")
	private String param158;

	@Column(name="param159")
	private String param159;

	@Column(name="param160")
	private String param160;

	@Column(name="param161")
	private String param161;

	@Column(name="param162")
	private String param162;

	@Column(name="param163")
	private String param163;

	@Column(name="param164")
	private String param164;

	@Column(name="param165")
	private String param165;

	@Column(name="param166")
	private String param166;

	@Column(name="param167")
	private String param167;

	@Column(name="param168")
	private String param168;

	@Column(name="param169")
	private String param169;

	@Column(name="param170")
	private String param170;

	@Column(name="param171")
	private String param171;

	@Column(name="param172")
	private String param172;

	@Column(name="param173")
	private String param173;

	@Column(name="param174")
	private String param174;

	@Column(name="param175")
	private String param175;

}
