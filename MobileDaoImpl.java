package com.tridel.tems_data_service.dao;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.tridel.tems_data_service.model.request.ReportRequest;
import com.tridel.tems_data_service.model.request.SensorParamView;
import com.tridel.tems_data_service.model.response.IntervalDataPojo;
import com.tridel.tems_data_service.model.response.MobileReport;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.tridel.tems_data_service.dao.ReportDaoImpl.*;
import static com.tridel.tems_data_service.service.CommonService.getParamStrToQuery;
import static com.tridel.tems_data_service.util.CommonUtil.*;

@Repository
@Slf4j
public class MobileDaoImpl implements MobileDao {

    EntityManager entityManager;

    MobileDaoImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public List<MobileReport> loadReportData(ReportRequest request) {
        StringBuilder paramStr = new StringBuilder();
        List<MobileReport> resp = new ArrayList<>();
        List<String> dataIntervals = new ArrayList<>();

        Map<String, SensorParamView> paramViewMap = request.getParamList().stream().collect(Collectors.toMap(SensorParamView::getDataParamName, obj -> obj));

        String columnName = (request.getStandardTime().equalsIgnoreCase("LT") ? "parameter_datetime" : "parameter_datetime_utc");
        getParamStrToQuery(request.getParamList(), paramStr); //creating param list for select query
        String queryStr = "SELECT " +
                "CASE WHEN DATEPART(SECOND, CONVERT(VARCHAR, " + columnName + ", 121)) >= 30 THEN "
                + "CONVERT(VARCHAR, DATEADD(MINUTE, 1, " + columnName + "), 103) + ' ' + "
                + "CONVERT(VARCHAR(5), DATEADD(MINUTE, 1, " + columnName + "), 108) ELSE "
                + "CONVERT(VARCHAR, " + columnName + ", 103) + ' ' + "
                + "CONVERT(VARCHAR(5), " + columnName + ", 108) END as parameter_datetime "
                + paramStr + " FROM " + request.getSensorTableCode() +
                " where (is_data_removed = 'false' or is_data_removed is NULL) and station_id IN ("
                + request.getStation() + ") and " + columnName + " >='" + request.getFromDate() +
                "' and " + columnName + " <='" + request.getToDate() + "'  order by station_id,parameter_datetime asc";
        log.info("getReportByInterval query " + queryStr);

        List<Object[]> list = entityManager.createNativeQuery(queryStr).getResultList();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        LocalDateTime startDate = LocalDateTime.parse(request.getFromDate(), formatter3);
        LocalDateTime endDate = LocalDateTime.parse(request.getToDate(), formatter3);
        if (Objects.equals(request.getInterval(), "1")) {
            return list.stream()
                    .flatMap(row -> IntStream.range(0, Math.min(row.length - 1, request.getParamList().size()))
                            .mapToObj(i -> {
                                SensorParamView template = request.getParamList().get(i);
                                return new MobileReport((Double) row[i + 1], template.getParamName(), (String) row[0], template.getUnitSymbol());
                            })
                    )
                    .toList();
        } else {
            return getMobileReportsIntervals(request, startDate, endDate, list, request.getParamList(), dataIntervals, paramViewMap, resp);
        }
    }

    @Override
    public List<MobileReport> loadReportMinData(ReportRequest request) {
        StringBuilder paramStr = new StringBuilder();
        String columnName = (request.getStandardTime().equalsIgnoreCase("LT") ? "parameter_datetime" : "parameter_datetime_utc");
        getParamStrToQuery(request.getParamList(), paramStr); //creating param list for select query

        int minIntrvl = request.getMinIntrvl();
        String minInterval = minIntrvl + "";
        if (minIntrvl < 10) {
            minInterval = "0" + minIntrvl;
        }
        String minInterval1 = (minIntrvl - 1) + "";
        if ((minIntrvl - 1) < 10) {
            minInterval1 = "0" + (minIntrvl - 1);
        }
        String query = "select * from (SELECT DISTINCT "
//                + "tbl.station_id as station_id, "
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
                + "from " + request.getSensorTableCode() + " tbl "
                + "where (is_data_removed = 'false' or is_data_removed is NULL) "
                + "and station_id IN (" + request.getStation() + ") "
                + "and " + columnName + " >= '" + request.getFromDate() + "' "
                + "and " + columnName + " <= '" + request.getToDate() + "' "
                + "and (" + columnName + " like '%:" + minInterval + ":%' or "
                + "(" + columnName + " like '%:" + minInterval1 + ":%' and DATEPART(SECOND, CONVERT(VARCHAR," + columnName + ", 108)) >= 30))) "
                + "with_rn where row_no = 1";

        List<Object[]> list = entityManager.createNativeQuery(query).getResultList();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        return list.stream()
                .flatMap(row -> IntStream.range(0, Math.min(row.length - 1, request.getParamList().size()))
                        .mapToObj(i -> {
                            SensorParamView template = request.getParamList().get(i);
                            return new MobileReport((Double) row[i + 1], template.getParamName(), (String) row[0], template.getUnitSymbol());
                        })
                )
                .toList();

    }

    private List<MobileReport> getMobileReportsIntervals(ReportRequest request, LocalDateTime startDate, LocalDateTime endDate, List<Object[]> list, List<SensorParamView> paramList, List<String> dataIntervals, Map<String, SensorParamView> paramViewMap, List<MobileReport> resp) {
//        Map<String, Integer> params = request.getParamList().stream().collect(Collectors.toMap(SensorParamView::getDataParamName, spm -> spm.getDisplayRoundTo()!=null ? spm.getDisplayRoundTo() : 0));
        List<String> allIntervals = getIntervals(startDate, endDate, request.getInterval());
        Map<String, List<IntervalDataPojo>> groupedData = list.stream()
                .map(obj -> new IntervalDataPojo(
                        convertToLocalDateTime((String) obj[0]),
                        extractParams(obj, paramList.stream().map(SensorParamView::getDataParamName).toList())
                ))
                .collect(Collectors.groupingBy(intData -> getInterval(intData.getTimestamp(), request.getInterval(), startDate)));
        for (Map.Entry<String, List<IntervalDataPojo>> entry : groupedData.entrySet()) {
            String groupKey = entry.getKey();
            dataIntervals.add(groupKey);
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

            for (Map.Entry<String, List<Double>> paramEntry : paramValuesMap.entrySet()) {
                DoubleSummaryStatistics stats = paramEntry.getValue().stream().mapToDouble(Double::doubleValue).summaryStatistics();
                SensorParamView spv = paramViewMap.get(paramEntry.getKey());
                BigDecimal bd = BigDecimal.valueOf(stats.getAverage()).setScale(spv.getDisplayRoundTo(), RoundingMode.HALF_UP);
                MobileReport report = new MobileReport(bd.doubleValue(), spv.getParamName(), groupKey, spv.getUnitSymbol());
                resp.add(report);
            }
        }

        if (!request.getInterval().equalsIgnoreCase("Monthly")) {
            allIntervals.removeAll(dataIntervals);
            allIntervals.forEach(interval ->
                    request.getParamList().forEach(pm -> {
                        MobileReport report = new MobileReport(null, pm.getParamName(), interval, pm.getUnitSymbol());
                        resp.add(report);
                    })
            );
            if (request.getInterval().equalsIgnoreCase("Yearly"))
                resp.sort(Comparator.comparing(MobileReport::getDateTime));
            else
                resp.sort(Comparator.comparing(MobileReport::getParsedDateTime));
        } else {
            List<String> intrvl = allIntervals;
            allIntervals.removeAll(dataIntervals);
            allIntervals.forEach(interval ->
                    request.getParamList().forEach(pm -> {
                        MobileReport report = new MobileReport(null, pm.getParamName(), interval, pm.getUnitSymbol());
                        resp.add(report);
                    })
            );
            resp.sort(Comparator.comparing(record -> intrvl.indexOf(record.getDateTime())));
        }
        return resp;
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
            case "1440" -> {
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

    private String getInterval(LocalDateTime timestamp, String interval, LocalDateTime startDate) {
        switch (interval) {
            case "Monthly" -> {
                return timestamp.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
            }
            case "Yearly" -> {
                return String.valueOf(timestamp.getYear());
            }
            default -> {
                int intvl = Integer.parseInt(interval);
                if (intvl == 1440) {
                    //LocalDateTime intervalStart = startDate;
                    return timestamp.withMinute(0).withHour(0).format(formatter2);
                } else {
                    int minute = timestamp.getMinute();
                    int intervalStartMinute = (minute / intvl) * intvl;
                    LocalDateTime intervalStart = timestamp.withMinute(intervalStartMinute).withSecond(0).withNano(0);
                    return intervalStart.plusMinutes(intvl).format(formatter2);
                }
            }
        }

    }
}
