package com.tridel.tems_data_service.util;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;

public class CommonUtil {
    public static final SimpleDateFormat dateFormat1= new SimpleDateFormat("yyyy-MM-dd HH:mm");
    public static final SimpleDateFormat dateFormat2= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static final DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    public static final DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    public static final DateTimeFormatter formatter3 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public static final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
    public static final String LOAD_STATIONS_FAILED="Load Station details failed";
    public static final String FETCH_SENSOR_FAILED="Fetch Sensor details failed";
}
