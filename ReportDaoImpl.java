package com.tridel.tems_data_service.dao;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.tridel.tems_data_service.model.request.ReportRequest;
import com.tridel.tems_data_service.model.request.SensorParamView;
import com.tridel.tems_data_service.model.response.IntervalDataPojo;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Repository;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.tridel.tems_data_service.service.CommonService.getParamStrToQuery;
import static com.tridel.tems_data_service.util.CommonUtil.*;

@Repository
@Slf4j
public class ReportDaoImpl implements ReportDao{
    EntityManager entityManager;
    ReportDaoImpl(EntityManager entityManager){
        this.entityManager= entityManager;
    }
    @Override
    public String loadReportsDataOneMinute(ReportRequest reportRequest) {
        String dataObj = null;
        StringBuilder paramStr = new StringBuilder();
        log.info("loadReportsDataOneMinute entry");
        try{
            String columnName = (reportRequest.getStandardTime().equalsIgnoreCase("LT")?"parameter_datetime":"parameter_datetime_utc");
            getParamStrToQuery(reportRequest.getParamList(), paramStr);

            String queryStr = "SELECT station_id," +
                    "CASE WHEN DATEPART(SECOND, CONVERT(VARCHAR, " + columnName + ", 121)) >= 30 THEN "
                    + "CONVERT(VARCHAR, DATEADD(MINUTE, 1, " + columnName + "), 103) + ' ' + "
                    + "CONVERT(VARCHAR(5), DATEADD(MINUTE, 1, " + columnName + "), 108) ELSE "
                    + "CONVERT(VARCHAR, " + columnName + ", 103) + ' ' + "
                    + "CONVERT(VARCHAR(5), " + columnName + ", 108) END as datetime "
                    + paramStr + " FROM "+ reportRequest.getSensorTableCode()+
                    " where (is_data_removed = 'false' or is_data_removed is NULL) and station_id IN ("
                    + reportRequest.getStation() + ") and "+columnName+" >='" + reportRequest.getFromDate() +
                    "' and "+columnName+" <='" + reportRequest.getToDate() + "'  order by station_id,parameter_datetime asc" ;
            log.info("loadReportsDataOneMinute query "+queryStr);
            List<Object> list = entityManager.createNativeQuery(queryStr).getResultList();
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
            dataObj = objectMapper.writeValueAsString(list);

        }catch(
                NoResultException ignored){
        } catch (
                JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        log.info("loadReportsDataOneMinute exit");
        return dataObj;
    }
    @Override
    public String getReportByInterval(ReportRequest request){
        StringBuilder paramStr = new StringBuilder();
        String dataObj = "";
        List<Boolean> containsList = request.getContainsFunctions();
        List<Object[]> finalList = new ArrayList<>();
        List<String> dataIntervals = new ArrayList<>();
        log.info("getReportByInterval entry");
        try{
            List<String> stationList = List.of(request.getStation().split(","));
            String columnName = (request.getStandardTime().equalsIgnoreCase("LT")?"parameter_datetime":"parameter_datetime_utc");
            getParamStrToQuery(request.getParamList(), paramStr);
            Map<String,Integer> params = request.getParamList().stream().collect(Collectors.toMap(SensorParamView::getDataParamName,SensorParamView::getDisplayRoundTo));
            String queryStr = "SELECT station_id," +
                    "CASE WHEN DATEPART(SECOND, CONVERT(VARCHAR, " + columnName + ", 121)) >= 30 THEN "
                    + "CONVERT(VARCHAR, DATEADD(MINUTE, 1, " + columnName + "), 103) + ' ' + "
                    + "CONVERT(VARCHAR(5), DATEADD(MINUTE, 1, " + columnName + "), 108) ELSE "
                    + "CONVERT(VARCHAR, " + columnName + ", 103) + ' ' + "
                    + "CONVERT(VARCHAR(5), " + columnName + ", 108) END as parameter_datetime "
                    + paramStr + " FROM "+ request.getSensorTableCode()+
                    " where (is_data_removed = 'false' or is_data_removed is NULL) and station_id IN ("
                    + request.getStation() + ") and "+columnName+" >='" + request.getFromDate() +
                    "' and "+columnName+" <='" + request.getToDate() + "'  order by station_id,parameter_datetime asc" ;
            log.info("getReportByInterval query "+queryStr);
            List<Object[]> list = entityManager.createNativeQuery(queryStr).getResultList();
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
            LocalDateTime startDate = LocalDateTime.parse(request.getFromDate(), formatter1);
            LocalDateTime endDate = LocalDateTime.parse(request.getToDate(), formatter1);
            List<String> allIntervals = getIntervals(startDate,endDate, request.getInterval());
            Map<String, List<IntervalDataPojo>> groupedData = list.stream()
                    .map(obj -> new IntervalDataPojo(
                            (Integer) obj[0],
                            convertToLocalDateTime((String) obj[1]),
                            extractParamReport(obj,params.keySet().stream().toList())
                    ))
                    .collect(Collectors.groupingBy(intData -> intData.getStationId() + "_" + getInterval(intData.getTimestamp(), request.getInterval(),startDate)));

            for (Map.Entry<String, List<IntervalDataPojo>> entry : groupedData.entrySet()) {
                String groupKey = entry.getKey();
                List<IntervalDataPojo> pojoList = entry.getValue();

                Map<String, List<Double>> paramValuesMap = new LinkedHashMap<>();

                for (IntervalDataPojo pojo : pojoList) {
                    for (Map.Entry<String, Double> paramEntry : pojo.getParameters().entrySet()) {
                        String paramName = paramEntry.getKey();
                        Double paramValue = paramEntry.getValue();
                        if (paramValue != null) {
                            paramValuesMap.computeIfAbsent(paramName, k -> new ArrayList<>()).add(paramValue);
                        }
                    }
                }
                Map<String, Map<String, String>> paramStats = new LinkedHashMap<>();
                for (Map.Entry<String, List<Double>> paramEntry : paramValuesMap.entrySet())
                    getIntervalDataSet(paramEntry, params, containsList, paramStats);
                String[] row = new String[paramStats.size() * containsList.size() + 2];
                row[0] = groupKey.split("_")[0];
                row[1] = groupKey.split("_")[1];
                dataIntervals.add(row[1]);
                int idx = 2;
                for (Map.Entry<String, Map<String, String>> paramEntry : paramStats.entrySet()) {
                    Map<String, String> stats = paramEntry.getValue();
                    if(containsList.get(0))
                        row[idx++] = stats.get("min");
                    if(containsList.get(1))
                        row[idx++] = stats.get("max");
                    if(containsList.get(2))
                        row[idx++] = stats.get("avg");
                    if(containsList.get(3))
                        row[idx++] = stats.get("sum");
                    if(containsList.get(4))
                        row[idx++] = stats.get("count");
                    if(containsList.get(5))
                        row[idx++] = stats.get("mode");
                    if(containsList.get(6))
                        row[idx++] = stats.get("std_dev");
                }
                finalList.add(row);
            }

            allIntervals.removeAll(dataIntervals);
            stationList.forEach(stn->{
                allIntervals.forEach(interval->{
                    String[] noDataRow = new String[params.keySet().stream().toList().size() * containsList.size() + 2];
                    noDataRow[0] = stn;
                    noDataRow[1] = interval;
                    finalList.add(noDataRow);
                });
            });
            if(!request.getInterval().equalsIgnoreCase("Monthly") && !request.getInterval().equalsIgnoreCase("Yearly"))
                finalList.sort(Comparator.comparing(obj -> (LocalDateTime.parse((String) obj[1], formatter2))));
            else
                finalList.sort(Comparator.comparing(obj -> (String) obj[1]));
            dataObj= objectMapper.writeValueAsString(finalList);
        }catch(NoResultException ignored){
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        log.info("getReportByInterval exit");
        return  dataObj;
    }

    private static void getIntervalDataSet(Map.Entry<String, List<Double>> paramEntry, Map<String, Integer> params, List<Boolean> containsList, Map<String, Map<String, String>> paramStats) {
        String paramName = paramEntry.getKey();
        List<Double> values = paramEntry.getValue();
        if (values.isEmpty()) {
            return;
        }
        Integer displayRoundTo = params.get(paramName);
//                    String pattern = "#." + "#".repeat(displayRoundTo);
        String pattern = displayRoundTo != null && displayRoundTo == 0
                ? "#" : "#." + "#".repeat(displayRoundTo);
        DecimalFormat df = new DecimalFormat(pattern);
        Map<String, String> statsMap = new LinkedHashMap<>();
        DoubleSummaryStatistics stats = values.stream().mapToDouble(Double::doubleValue).summaryStatistics();
        if(containsList.get(0))
            statsMap.put("min", df.format(stats.getMin()));
        if(containsList.get(1))
            statsMap.put("max", df.format(stats.getMax()));
        double average=0.0;
        if(containsList.get(2)) {
            average=stats.getAverage();
            statsMap.put("avg", df.format(average));
        } else {
            average = 0.0;
        }
        if(containsList.get(3))
            statsMap.put("sum", df.format(stats.getSum()));
        if(containsList.get(4))
            statsMap.put("count", String.valueOf(stats.getCount()));
        double variance = 0.0;
        double stdDeviation = 0.0;
        if(containsList.get(6)) {
            double finalAverage = average;
            variance = values.stream()
                    .mapToDouble(v -> Math.pow(v - finalAverage, 2))
                    .average().orElse(0.0);
            stdDeviation = Math.sqrt(variance);
            statsMap.put("std_dev", df.format(stdDeviation));
        }
        Double mode = 0.0;
        if(containsList.get(5)) {
            mode = values.stream()
                    .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                    .entrySet().stream()
                    .max(Comparator.comparing(Map.Entry::getValue))
                    .map(Map.Entry::getKey)
                    .orElse(null);
            statsMap.put("mode", String.valueOf(mode != null ? mode : Double.NaN));
        }
        paramStats.put(paramName, statsMap);
    }

    public static List<String> getIntervals(LocalDateTime start, LocalDateTime end, String intervalType) {
        List<String> intervals = new ArrayList<>();
        switch (intervalType) {
            case "Monthly" -> {
                for (LocalDateTime time = start; !time.isAfter(end); time = time.plusMonths(1)) {
                    intervals.add(time.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH));
                }
            }
            case "Yearly" -> {
                for (LocalDateTime time = start; !time.isAfter(end); time = time.plusYears(1)) {
                    intervals.add(String.valueOf(time.getYear()));
                }
            }
            case "10","30","60" -> {
                try {
                    int intervalMinutes = Integer.parseInt(intervalType);
                    for (LocalDateTime time = start; !time.isAfter(end.plusMinutes(intervalMinutes)); time = time.plusMinutes(intervalMinutes)) {
                        if(intervalType.equals("60")) time = time.withMinute(0);
                        if(time != start) intervals.add(formatter2.format(time));
                    }
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Invalid interval type: " + intervalType);
                }
            }
            case "1440" -> {
                for (LocalDateTime time = start.plusDays(1); !time.isAfter(end); time = time.plusDays(1)) {
                    intervals.add(formatter2.format(time));
                }
            }
            default -> {
                try {
                    int intervalMinutes = Integer.parseInt(intervalType);
                    for (LocalDateTime time = start; !time.isAfter(end.plusMinutes(intervalMinutes)); time = time.plusMinutes(intervalMinutes)) {
                        intervals.add(formatter2.format(time));
                    }
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Invalid interval type: " + intervalType);
                }
            }
        }

        return intervals;
    }

    static LocalDateTime convertToLocalDateTime(String date) {
        return LocalDateTime.parse(date, formatter2);
    }

    static Map<String, Double> extractParams(Object[] obj, List<String> paramList) {
        Map<String, Double> params = new LinkedHashMap<>();
        for (int i = 2; i < obj.length; i++) {
            params.put(paramList.get(i-2), (Double) obj[i-1]);
        }
        return params;
    }

    static Map<String, Double> extractParamReport(Object[] obj, List<String> paramList) {
        Map<String, Double> params = new LinkedHashMap<>();
        for (int i = 2; i < obj.length; i++) {
            params.put(paramList.get(i-2), (Double) obj[i]);
        }
        return params;
    }

    private String getInterval(LocalDateTime timestamp,String interval,LocalDateTime startDate) {
        switch(interval){
            case "Monthly" -> {
                return timestamp.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
            }
            case "Yearly"->{
                return String.valueOf(timestamp.getYear());
            }
            default->{
                int intvl = Integer.parseInt(interval);
                if (intvl == 1440) {
                    LocalDateTime intervalStart = timestamp.withMinute(startDate.getMinute()).withHour(startDate.getHour());
                    if(startDate.getHour()<timestamp.getHour() || (startDate.getHour() == timestamp.getHour() && startDate.getMinute()<timestamp.getMinute()))
                        intervalStart = intervalStart.plusDays(1);
                    return intervalStart.format(formatter2);
                } else {
                    int minute = timestamp.getMinute();
                    int intervalStartMinute = (minute / intvl) * intvl;
                    LocalDateTime intervalStart = timestamp.withMinute(intervalStartMinute).withSecond(0).withNano(0);
                    return intervalStart.plusMinutes(intvl).format(formatter2);
                }
            }
        }

    }

    /*@Override
    public JSONArray getReportsDataByInterval(ReportRequest request) {
        JSONArray array = new JSONArray();
        JSONArray resultArray = new JSONArray();
        JSONArray finalArray = array;
        log.info("getReportsDataByInterval entry");
        request.getParamList().forEach(param->{
            JSONObject paramObj = new JSONObject();
            paramObj.put("parameterName",param.getDataParamName());
            paramObj.put("operation",param.getOperation());
            paramObj.put("calculatedValue",param.getCalculatedValue());
            paramObj.put("displayRoundTo",param.getDisplayRoundTo());
            finalArray.put(paramObj);
        });

       StringBuilder procedure = new StringBuilder("EXEC tems_report_function_interval '"+request.getStation()+"','"+request.getSensorTableCode()+
                "','"+request.getInterval()+"','"+request.getFromDate()+"','"+request.getToDate()+
                "','"+request.getStandardTime()+"','"+array.toString()+"'");
        procedure.trimToSize();
        try{
            List<Object> list = entityManager.createNativeQuery(procedure.toString()).getResultList();
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
            String arrayToJson = objectMapper.writeValueAsString(list);
            resultArray = new JSONArray(arrayToJson);
        }catch(NoResultException ignored){
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        log.info("getReportsDataByInterval exit");
        return resultArray;
    }*/

    @Override
    public String loadReportsDataHourly(ReportRequest reportRequest) {
        String dataObj = null;
        StringBuilder paramStr = new StringBuilder();
        log.info("loadReportsDataHourly entry");
        try{
            String columnName = (reportRequest.getStandardTime().equalsIgnoreCase("LT")?"parameter_datetime":"parameter_datetime_utc");
            getParamStrToQuery(reportRequest.getParamList(), paramStr);
            int minIntrvl = reportRequest.getMinIntrvl();
            String minInterval = minIntrvl + "";
            if (minIntrvl < 10) {
                minInterval = "0" + minIntrvl;
            }
            String minInterval1 = (minIntrvl - 1) + "";
            if ((minIntrvl - 1) < 10) {
                minInterval1 = "0" + (minIntrvl - 1);
            }
            String query = "select * from (SELECT DISTINCT "
                    + "tbl.station_id as station_id, "
                    + "CASE WHEN DATEPART(MINUTE, CONVERT(VARCHAR, " + columnName + ", 108)) = " + minInterval1 + " THEN "
                    + "CONVERT(VARCHAR, DATEADD(MINUTE, 1, " + columnName + "), 103) + ' ' + "
                    + "CONVERT(VARCHAR(5), DATEADD(MINUTE, 1, " + columnName + "), 108) ELSE "
                    + "CONVERT(VARCHAR, " + columnName + ", 103) + ' ' + "
                    + "CONVERT(VARCHAR(5), " + columnName + ", 108) END as parameter_datetime "
                    + paramStr + ", "
                    + "row_number() over (partition by (CASE WHEN DATEPART(MINUTE, CONVERT(VARCHAR, " + columnName + ", 108)) = "
                    + minInterval1 + " THEN "
                    + "CONVERT(VARCHAR, DATEADD(MINUTE, 1, " + columnName + "), 103) + ' ' + "
                    + "CONVERT(VARCHAR(5), DATEADD(MINUTE, 1, " + columnName + "), 108) ELSE "
                    + "CONVERT(VARCHAR, " + columnName + ", 103) + ' ' + "
                    + "CONVERT(VARCHAR(5), " + columnName + ", 108) END) "
                    + "order by " + columnName + ") as row_no "
                    + "from " + reportRequest.getSensorTableCode() + " tbl "
                    + "where (is_data_removed = 'false' or is_data_removed is NULL) "
                    + "and station_id IN (" + reportRequest.getStation() + ") "
                    + "and " + columnName + " >= '" + reportRequest.getFromDate() + "' "
                    + "and " + columnName + " <= '" + reportRequest.getToDate() + "' "
                    + "and (" + columnName + " like '%:" + minInterval + ":%' or "
                    + "(" + columnName + " like '%:" + minInterval1 + ":%' and DATEPART(SECOND, CONVERT(VARCHAR," + columnName + ", 108)) >= 30))) "
                    + "with_rn where row_no = 1";

            List<Object> list = entityManager.createNativeQuery(query).getResultList();
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
            dataObj = objectMapper.writeValueAsString(list);

        }catch(
                NoResultException ignored){
        } catch (
                JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        log.info("loadReportsDataHourly exit");
        return dataObj;
    }

    @Override
    public String getAllSensorReportDataOneMinute(ReportRequest reportRequest) {
        String dataObj = null;
        StringBuilder paramStr = new StringBuilder();
        log.info("getAllSensorReportDataOneMinute entry");
        try{
            String columnName = (reportRequest.getStandardTime().equalsIgnoreCase("LT")?"parameter_datetime":"parameter_datetime_utc");
            getParamStrToQuery(reportRequest.getParamList(), paramStr);

            StringBuilder queryStr = new StringBuilder("select "
                    + "CASE WHEN (DATEPART(SECOND, CONVERT(VARCHAR, " + columnName + ", 121))) >= 30 THEN "
                    + "CONVERT(VARCHAR, DATEADD(MINUTE, 1, " + columnName + "), 103) + ' ' + CONVERT(VARCHAR(5), DATEADD(MINUTE, 1, " + columnName + "), 108) "
                    + "ELSE "
                    + "CONVERT(VARCHAR, " + columnName + ", 103) + ' ' + CONVERT(VARCHAR(5), " + columnName + ", 108) "
                    + "END as parameter_datetime "
                    + paramStr + " from " + reportRequest.getSensorTableCode()
                    + " where (is_data_removed = 'false' or is_data_removed is NULL) "
                    + "and station_id IN (" + reportRequest.getStation()
                    + ") and " + columnName + " >= '" + reportRequest.getFromDate() + "' "
                    + "and " + columnName + " <= '" + reportRequest.getToDate() + "' "
                    + "order by " + columnName + " asc");
            log.info("getAllSensorReportDataOneMinute queryStr"+queryStr.toString());
            List<Object> list = entityManager.createNativeQuery(String.valueOf(queryStr)).getResultList();
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
            dataObj = objectMapper.writeValueAsString(list);

        }catch(
                NoResultException ignored){
        } catch (
                JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        log.info("getAllSensorReportDataOneMinute exit");
        return dataObj;
    }

    @Override
    public JSONArray getClimateReportsDataByInterval(ReportRequest request) {
        JSONArray array = new JSONArray();
        JSONArray resultArray = new JSONArray();
        log.info("getClimateReportsDataByInterval entry");
        request.getParamList().forEach(param->{
            JSONObject paramObj = new JSONObject();
            paramObj.put("parameterName",param.getDataParamName());
            paramObj.put("operation",param.getOperation());
            paramObj.put("calculatedValue",param.getCalculatedValue());
            paramObj.put("displayRoundTo",param.getDisplayRoundTo());
            array.put(paramObj);
        });

        StringBuilder procedure = new StringBuilder("EXEC tems_climate_report_interval '"+request.getStation()+"','"+request.getSensorTableCode()+
                "','"+request.getInterval()+"','"+request.getFromDate()+"','"+request.getToDate()+
                "','"+request.getStandardTime()+"','"+array.toString()+"'");
        procedure.trimToSize();
        try{
            List<Object> list = entityManager.createNativeQuery(procedure.toString()).getResultList();
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
            String arrayToJson = objectMapper.writeValueAsString(list);
            resultArray = new JSONArray(arrayToJson);
        }catch(NoResultException ignored){
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        log.info("getClimateReportsDataByInterval exit");
        return resultArray;
    }

    @Override
    public String getClimateReportByInterval(ReportRequest request) {
        StringBuilder paramStr = new StringBuilder();
        String dataObj = "";
        List<Boolean> containsList = request.getContainsFunctions();
        List<Object[]> dataList = new ArrayList<>();
        List<Object[]> finalList = new ArrayList<>();
        log.info("getReportByInterval entry");
        try{
//            List<String> stationList = List.of(request.getStation().split(","));
            String columnName = (request.getStandardTime().equalsIgnoreCase("LT")?"parameter_datetime":"parameter_datetime_utc");
            getParamStrToQuery(request.getParamList(), paramStr);
            Map<String,Integer> params = request.getParamList().stream().collect(Collectors.toMap(SensorParamView::getDataParamName,SensorParamView::getDisplayRoundTo));
            String queryStr = "SELECT " +
                    "CASE WHEN DATEPART(SECOND, CONVERT(VARCHAR, " + columnName + ", 121)) >= 30 THEN "
                    + "CONVERT(VARCHAR, DATEADD(MINUTE, 1, " + columnName + "), 103) + ' ' + "
                    + "CONVERT(VARCHAR(5), DATEADD(MINUTE, 1, " + columnName + "), 108) ELSE "
                    + "CONVERT(VARCHAR, " + columnName + ", 103) + ' ' + "
                    + "CONVERT(VARCHAR(5), " + columnName + ", 108) END as parameter_datetime "
                    + paramStr + " FROM "+ request.getSensorTableCode()+
                    " where (is_data_removed = 'false' or is_data_removed is NULL) and station_id IN ("
                    + request.getStation() + ") and "+columnName+" >='" + request.getFromDate() +
                    "' and "+columnName+" <='" + request.getToDate() + "'  order by station_id,parameter_datetime asc" ;
            log.info("getReportByInterval query "+queryStr);
            List<Object[]> list = entityManager.createNativeQuery(queryStr).getResultList();
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
            LocalDateTime startDate = LocalDateTime.parse(request.getFromDate(), formatter3);
            LocalDateTime endDate = LocalDateTime.parse(request.getToDate(), formatter3);
            List<String> allIntervals = getClimateIntervals(startDate,endDate, request.getInterval());
            Map<String, List<IntervalDataPojo>> groupedData = list.stream()
                    .map(obj -> new IntervalDataPojo(
                            convertToLocalDateTime((String) obj[0]),
                            extractParams(obj,params.keySet().stream().toList())
                    ))
                    .collect(Collectors.groupingBy(intData -> getClimateInterval(intData.getTimestamp(), request.getInterval())));

            for (Map.Entry<String, List<IntervalDataPojo>> entry : groupedData.entrySet()) {
                String groupKey = entry.getKey();
                List<IntervalDataPojo> pojoList = entry.getValue();

                Map<String, List<Double>> paramValuesMap = new LinkedHashMap<>();

                for (IntervalDataPojo pojo : pojoList) {
                    for (Map.Entry<String, Double> paramEntry : pojo.getParameters().entrySet()) {
                        String paramName = paramEntry.getKey();
                        Double paramValue = paramEntry.getValue();
                        if (paramValue != null) {
                            paramValuesMap.computeIfAbsent(paramName, k -> new ArrayList<>()).add(paramValue);
                        }
                    }
                }
                Map<String, Map<String, String>> paramStats = new LinkedHashMap<>();
                for (Map.Entry<String, List<Double>> paramEntry : paramValuesMap.entrySet())
                    getIntervalDataSet(paramEntry, params, containsList, paramStats);
                String[] row = new String[paramStats.size() * containsList.size() + 2];
                row[0] = groupKey;

                int idx = 1;
                for (Map.Entry<String, Map<String, String>> paramEntry : paramStats.entrySet()) {
                    Map<String, String> stats = paramEntry.getValue();
                    if(containsList.get(0))
                        row[idx++] = stats.get("min");
                    if(containsList.get(1))
                        row[idx++] = stats.get("max");
                    if(containsList.get(2))
                        row[idx++] = stats.get("avg");
                    if(containsList.get(3))
                        row[idx++] = stats.get("sum");
                    if(containsList.get(4))
                        row[idx++] = stats.get("count");
                    if(containsList.get(5))
                        row[idx++] = stats.get("mode");
                    if(containsList.get(6))
                        row[idx++] = stats.get("std_dev");
                }
                dataList.add(row);
            }

            Map<String, Object[]> dataMap = new HashMap<>();
            for (Object[] entry : dataList) {
                dataMap.put((String) entry[0], entry);
            }

            AtomicInteger count = new AtomicInteger(1);
            allIntervals.forEach(intr -> {

                if (dataMap.containsKey(intr)) {
                    if(request.getInterval().equalsIgnoreCase("Monthly")
                            || request.getInterval().equalsIgnoreCase("1440"))
                        dataMap.get(intr)[0] = String.valueOf(count.getAndIncrement());
                    finalList.add(dataMap.get(intr));
                } else {
                    String[] noDataRow = new String[params.keySet().stream().toList().size() * containsList.size() + 2];
                    noDataRow[0] = (request.getInterval().equalsIgnoreCase("Monthly")
                            || request.getInterval().equalsIgnoreCase("1440"))
                            ? String.valueOf(count.getAndIncrement()) : intr;
                    finalList.add(noDataRow);
                }
            });
            dataObj= objectMapper.writeValueAsString(finalList);
        }catch(NoResultException ignored){
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        log.info("getReportByInterval exit");
        return  dataObj;
    }

    public static List<String> getClimateIntervals(LocalDateTime start, LocalDateTime end, String intervalType) {
        List<String> intervals = new ArrayList<>();
        switch (intervalType) {
            case "Yearly" -> {
                for (LocalDateTime time = start; !time.isAfter(end); time = time.plusMonths(1)) {
                    intervals.add(time.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH));
                }
            }
            case "10","30","60" -> {
                try {
                    int intervalMinutes = Integer.parseInt(intervalType);
                    for (LocalDateTime time = start; !time.isAfter(end.plusMinutes(intervalMinutes)); time = time.plusMinutes(intervalMinutes)) {
                        if(intervalType.equals("60")) time = time.withMinute(0);
                        if(time != start) intervals.add(timeFormatter.format(time));
                    }
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Invalid interval type: " + intervalType);
                }
            }
            case "1440", "Monthly" -> {
                for (LocalDateTime time = start; !time.isAfter(end); time = time.plusDays(1)) {
                    intervals.add(formatter2.format(time));
                }
            }
            default -> {
                try {
                    int intervalMinutes = Integer.parseInt(intervalType);
                    for (LocalDateTime time = start; !time.isAfter(end.plusMinutes(intervalMinutes)); time = time.plusMinutes(intervalMinutes)) {
                        intervals.add(formatter2.format(time));
                    }
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Invalid interval type: " + intervalType);
                }
            }
        }

        return intervals;
    }

    private String getClimateInterval(LocalDateTime timestamp,String interval) {
        switch(interval){
            case "Monthly","1440" -> {
                return timestamp.withMinute(0).withHour(0).format(formatter2);
            }
            case "Yearly"->{
                return timestamp.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
            }
            default->{
                int intvl = Integer.parseInt(interval);
                int minute = timestamp.getMinute();
                int intervalStartMinute = (minute / intvl) * intvl;
                LocalDateTime intervalStart = timestamp.withMinute(intervalStartMinute).withSecond(0).withNano(0);
                return intervalStart.plusMinutes(intvl).format(timeFormatter);

            }
        }

    }
}
