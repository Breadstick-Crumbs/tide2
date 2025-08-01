package com.tridel.tems_data_service.dao;

import com.tridel.tems_data_service.entity.HeaderDataStatus;
import com.tridel.tems_data_service.exception.TemsCustomException;
import com.tridel.tems_data_service.model.request.*;
import com.tridel.tems_data_service.model.response.StationResponse;
import com.tridel.tems_data_service.repository.HeaderDataStatusRepository;
import com.tridel.tems_data_service.service.CommonService;
import jakarta.persistence.Tuple;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.tridel.tems_data_service.util.CommonUtil.dateFormat1;
import static com.tridel.tems_data_service.util.CommonUtil.dateFormat2;

@Repository
@Slf4j
public class StatisticsDaoImpl implements StatisticsDao{
    HeaderDataStatusRepository headerDataStatusRepo;
    CommonService commonService;
    StatisticsDaoImpl(HeaderDataStatusRepository headerDataStatusRepo,CommonService commonService){
        this.headerDataStatusRepo = headerDataStatusRepo;
        this.commonService = commonService;

    }
    public String loadParameterDataGraph(StatisticsDataRequest request) {
        JSONObject object = new JSONObject();
        log.info("loadParameterDataGraph entry");
        try {
            if (request.getParamDtls() != null && request.getParamDtls().getParamId() != null) {
                SensorParamView paramView = request.getParamDtls();
                JSONObject obj = new JSONObject();
                HeaderDataStatus dataStatus = headerDataStatusRepo.findFirstByStationIdOrderByDateTimeDesc(request.getStationId());
                object.put("unit", paramView.getUnitSymbol());
                object.put("graphYAxisMin", paramView.getGraphYAxisMin());

                if (dataStatus != null && dataStatus.getHeaderDataStatusId() != null) {
                    Date date = dataStatus.getDateTime();
                    StringBuilder toDateStr = new StringBuilder(dateFormat2.format(date));
                    Calendar calendar = Calendar.getInstance();
                    Date toDate = dateFormat2.parse(toDateStr.toString());
                    calendar.setTime(toDate);
                    calendar.add(Calendar.DAY_OF_YEAR, -3);
                    Date fromDate = calendar.getTime();
                    StringBuilder fromDateStr = new StringBuilder(dateFormat2.format(fromDate));
                    fromDateStr.trimToSize();
                    SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy");
                    String formattedFromDate = sdf1.format(fromDate);
                    String formattedToDate = sdf1.format(toDate);
                    String formattedDateRange = "(" + formattedFromDate + " - " + formattedToDate + ")";
                    String paramToCreate;
                    StringBuilder qr = new StringBuilder(" from " + request.getSensorTableCode() +
                            " where station_id =" + request.getStationId() + " and is_valid='true' and parameter_datetime>= '"
                            + fromDateStr.toString() + "' and parameter_datetime <='" + toDateStr.toString() + "' order by parameter_datetime desc");
                    List<String> dateTime = commonService.executeQueryStr("select CONVERT(VARCHAR(16),parameter_datetime, 121)" + qr.toString());
                    if (paramView.getOperation() != null && paramView.getOperation().equalsIgnoreCase("(* 1.8)+32")) {
                        paramToCreate = String.format("ROUND((%s * 1.8) + 32, %d) ", paramView.getDataParamName(), paramView.getDisplayRoundTo());
                    } else {
                        String operationPart = (paramView.getOperation() != null) ? paramView.getOperation() + paramView.getCalculatedValue() : "";
                        paramToCreate = String.format("ROUND((%s %s), %d) ", paramView.getDataParamName(), operationPart, paramView.getDisplayRoundTo());
                    }
                    List<String> dataList = commonService.executeQueryStr("select Cast("+paramToCreate+" as VARCHAR) " + qr.toString());
                    object.put("dateTime", dateTime);
                    obj.put("name", paramView.getParamName() + " " + formattedDateRange);
                    obj.put("data", dataList);
                } else{
//                    object.put("dateTime", dateTime);
                    obj.put("name", paramView.getParamName());
                    obj.put("data", new ArrayList<>());
                }
                object.put("series", obj);
                dataStatus = null;
            }
        }catch(Exception e){
            throw new TemsCustomException("Load parameter data graph has failed");
        }
        StringBuilder response = new StringBuilder(object.toString());
        response.trimToSize();
        log.info("loadParameterDataGraph exit");
        return response.toString();
    }
    @Override
    public JSONArray getGraphData(StatisticsParamDataRequest request) {
        JSONArray array = new JSONArray();
        try {
            List<Integer> stationIds = request.getStationIds().stream().map(StationResponse::getStationId).toList();
            String stations = request.getStationIds().stream().map(stn->String.valueOf(stn.getStationId())).collect(Collectors.joining(","));
            String dateTimeQuery = "select DISTINCT " +
                    " CASE WHEN DATEPART(SECOND, CONVERT(VARCHAR,parameter_datetime, 121)) >= 30 " +
                    " then CONVERT(VARCHAR(16),DATEADD(MINUTE,1,parameter_datetime), 121) " +
                    " else CONVERT(VARCHAR(16),parameter_datetime, 121) end as parameter_datetime from " +
                    request.getSensorTableCode() + " where station_id in ("
                    + stations + ") and is_valid='true' and parameter_datetime>= '"
                    + request.getFromDate() + "' and parameter_datetime <='"
                    + request.getToDate() + "' order by parameter_datetime";

            List<String> dateTime = commonService.executeQueryStr(dateTimeQuery);

            String dateRange = "dt.parameter_datetime >= '" + request.getFromDate().replace("T", " ") + "' and dt.parameter_datetime <= '" + request.getToDate().replace("T", " ") + "'";

            for (SensorParamView pm : request.getGraphParameters()) {
                if (pm == null || pm.getParamId() == null) continue;
                JSONObject object = new JSONObject();
                JSONArray arr = new JSONArray();
                object.put("unit", pm.getUnitSymbol());
                object.put("min", pm.getMin());
                object.put("max", pm.getMax());
                object.put("paramName", pm.getParamName());
                object.put("dateTime", dateTime);
                object.put("graphYAxisMin", pm.getGraphYAxisMin());
                if (!dateTime.isEmpty()) {

                    String dataQuery = "select dt.station_id,dt." + pm.getDataParamName() +"  from " + request.getSensorTableCode()
                                        +" st JOIN "+request.getSensorTableCode()+" dt ON st.station_id in (" + stations + ") AND st.parameter_datetime = dt.parameter_datetime"
                                        +" WHERE dt.station_id in (" + stations + ") and dt.is_valid = 'true' and " + dateRange + " order by dt.parameter_datetime";
                    Map<Integer, List<Double>> stationDataMap = commonService.executeQueryStrBatch(dataQuery);
                    for (StationResponse st : request.getStationIds()) {
                        JSONObject obj = new JSONObject();
                        List<Double> dataList = stationDataMap.getOrDefault(st.getStationId(), Collections.emptyList());
                        List<Double> newdataList = new ArrayList<>();
                        dataList.forEach(data->{
                            BigDecimal bd =BigDecimal.ZERO;
                            if(pm.getOperation()!= null && data!=null) {
                                if (pm.getOperation().equalsIgnoreCase("(* 1.8)+32")) {
                                    data = (data * 1.8) + 32;
                                } else {
                                    data = applyOperation(data.toString(), pm.getOperation(), new BigDecimal(pm.getCalculatedValue()));
                                }
                                bd = new BigDecimal(data).setScale(pm.getDisplayRoundTo(), RoundingMode.HALF_UP);
                                newdataList.add(bd.doubleValue());
                            }else
                                newdataList.add(null);

                        });

                        obj.put("data", newdataList.isEmpty() ? new ArrayList<>() : newdataList);
                        obj.put("name", st.getStationName());
                        arr.put(obj);
                    }
                } else {
                    for (StationResponse st : request.getStationIds()) {
                        JSONObject obj = new JSONObject();
                        obj.put("data", new ArrayList<>());
                        obj.put("name", st.getStationName());
                        arr.put(obj);
                    }
                }
                object.put("series", arr);
                array.put(object);
            }
        } catch (Exception ex) {
            log.info("Exception occurred in loadStatistics graph api " + ex.getMessage());
            throw new TemsCustomException("Load Statistics graph failed");
        }
        return array;
    }
    @Override
    public JSONObject loadWindRose(String fDate, String tDate, StatisticsWindroseParam pm, StationResponse station) throws ParseException {
            JSONObject obj = new JSONObject();
        obj.put("paramName",pm.getParamName()+" - "+station.getStationName());
        List<String> rangeList = pm.getWindRoseRanges().stream().map(e -> e.getScaleMin()!=null ? e.getScaleMin()+" < "+e.getScaleMax() : String.valueOf(e.getScaleMax())).toList();
        obj.put("range", rangeList);
        Date fromDate = dateFormat1.parse(fDate.replace("T", " "));
        Date toDate = dateFormat1.parse(tDate.replace("T", " "));
        long dateBeforeInMs = fromDate.getTime();
        long dateAfterInMs = toDate.getTime();
        long timeDiff = Math.abs(dateAfterInMs - dateBeforeInMs);
        long daysDiff = TimeUnit.DAYS.convert(timeDiff, TimeUnit.MILLISECONDS);
        try {
            if (daysDiff <= 183) {
                JSONArray array = new JSONArray();
                JSONArray totals = new JSONArray();
                int i = 0;
                Double grandTotal;
                double nTotal = 0;
                double nneTotal = 0;
                double neTotal = 0;
                double eneTotal = 0;
                double eTotal = 0;
                double eseTotal = 0;
                double seTotal = 0;
                double sseTotal = 0;
                double sTotal = 0;
                double sswTotal = 0;
                double swTotal = 0;
                double wswTotal = 0;
                double wTotal = 0;
                double wnwTotal = 0;
                double nwTotal = 0;
                double nnwTotal = 0;
                SensorParamView pmS = pm.getParameterOne();
                obj.put("unit", pmS.getUnitSymbol());
                SensorParamView pmD = pm.getParameterTwo();
                if (pmS != null) {
                    String grandTotalQuery = "SELECT SUM("+pmS.getDataParamName()+") FROM "+pmS.getSensorTableName()
                            +" WHERE parameter_datetime >= '"+fDate.replace("T", " ")
                            +"' AND parameter_datetime <= '"+tDate.replace("T", " ")
                            +"' AND station_id='"+station.getStationId()+"'";
                    grandTotal = commonService.executeQueryStringForDouble(grandTotalQuery);
                    grandTotal = grandTotal!=null ? grandTotal : 0;

                    String wdDataQuery = "select " + pmS.getDataParamName() + "," + pmD.getDataParamName() + " from "
                            + pmS.getSensorTableName() + " where station_id=" + station.getStationId()
                            + " and parameter_datetime >='" + dateFormat2.format(fromDate).replace("T", " ")
                            +"' and parameter_datetime <='" + dateFormat2.format(toDate).replace("T", " ")
                            + "' and is_valid = 'true' order by parameter_datetime desc";
                    List<Tuple> wdData = commonService.executeQueryString(wdDataQuery);

                    if (!pm.getWindRoseRanges().isEmpty()) {
                        for (WindRoseRange windRoseRange : pm.getWindRoseRanges()) {
                            if (windRoseRange != null) {
                                double nCount = 0;
                                double nneCount = 0;
                                double neCount = 0;
                                double eneCount = 0;
                                double eCount = 0;
                                double eseCount = 0;
                                double seCount = 0;
                                double sseCount = 0;
                                double sCount = 0;
                                double sswCount = 0;
                                double swCount = 0;
                                double wswCount = 0;
                                double wCount = 0;
                                double wnwCount = 0;
                                double nwCount = 0;
                                double nnwCount = 0;
                                for (Tuple tuple : wdData) {
                                    if(tuple.get(0)!=null && tuple.get(1)!=null){
                                        if (windRoseRange.getScaleMin() == null) {
                                            if ((double) tuple.get(0) < windRoseRange.getScaleMax()) {
                                                if ((double) tuple.get(1) >= 0 && (double) tuple.get(1) <= 22.5) {
                                                    nCount = nCount + (double) tuple.get(0);
                                                } else if ((double) tuple.get(1) >= 22.6 && (double) tuple.get(1) <= 45) {
                                                    nneCount = nneCount + (double) tuple.get(0);
                                                } else if ((double) tuple.get(1) >= 45.1 && (double) tuple.get(1) <= 67.5) {
                                                    neCount = neCount + (double) tuple.get(0);
                                                } else if ((double) tuple.get(1) >= 67.6 && (double) tuple.get(1) <= 90) {
                                                    eneCount = eneCount + (double) tuple.get(0);
                                                } else if ((double) tuple.get(1) >= 90.1 && (double) tuple.get(1) <= 112.5) {
                                                    eCount = eCount + (double) tuple.get(0);
                                                } else if ((double) tuple.get(1) >= 112.6 && (double) tuple.get(1) <= 135) {
                                                    eseCount = eseCount + (double) tuple.get(0);
                                                } else if ((double) tuple.get(1) >= 135.1 && (double) tuple.get(1) <= 157.5) {
                                                    seCount = seCount + (double) tuple.get(0);
                                                } else if ((double) tuple.get(1) >= 157.6 && (double) tuple.get(1) <= 180) {
                                                    sseCount = sseCount + (double) tuple.get(0);
                                                } else if ((double) tuple.get(1) >= 180.1 && (double) tuple.get(1) <= 202.5) {
                                                    sCount = sCount + (double) tuple.get(0);
                                                } else if ((double) tuple.get(1) >= 202.6 && (double) tuple.get(1) <= 225) {
                                                    sswCount = sswCount + (double) tuple.get(0);
                                                } else if ((double) tuple.get(1) >= 225.1 && (double) tuple.get(1) <= 247.5) {
                                                    swCount = swCount + (double) tuple.get(0);
                                                } else if ((double) tuple.get(1) >= 247.6 && (double) tuple.get(1) <= 270) {
                                                    wswCount = wswCount + (double) tuple.get(0);
                                                } else if ((double) tuple.get(1) >= 270.1 && (double) tuple.get(1) <= 292.5) {
                                                    wCount = wCount + (double) tuple.get(0);
                                                } else if ((double) tuple.get(1) >= 292.6 && (double) tuple.get(1) <= 315) {
                                                    wnwCount = wnwCount + (double) tuple.get(0);
                                                } else if ((double) tuple.get(1) >= 315.1 && (double) tuple.get(1) <= 337.5) {
                                                    nwCount = nwCount + (double) tuple.get(0);
                                                } else if ((double) tuple.get(1) >= 337.6 && (double) tuple.get(1) <= 360) {
                                                    nnwCount = nnwCount + (double) tuple.get(0);
                                                }
                                            }
                                        } else if (windRoseRange.getScaleMax() == null) {
                                            if ((double) tuple.get(0) > windRoseRange.getScaleMin()) {
                                                if ((double) tuple.get(1) >= 0 && (double) tuple.get(1) <= 22.5) {
                                                    nCount = nCount + (double) tuple.get(0);
                                                } else if ((double) tuple.get(1) >= 22.6 && (double) tuple.get(1) <= 45) {
                                                    nneCount = nneCount + (double) tuple.get(0);
                                                } else if ((double) tuple.get(1) >= 45.1 && (double) tuple.get(1) <= 67.5) {
                                                    neCount = neCount + (double) tuple.get(0);
                                                } else if ((double) tuple.get(1) >= 67.6 && (double) tuple.get(1) <= 90) {
                                                    eneCount = eneCount + (double) tuple.get(0);
                                                } else if ((double) tuple.get(1) >= 90.1 && (double) tuple.get(1) <= 112.5) {
                                                    eCount = eCount + (double) tuple.get(0);
                                                } else if ((double) tuple.get(1) >= 112.6 && (double) tuple.get(1) <= 135) {
                                                    eseCount = eseCount + (double) tuple.get(0);
                                                } else if ((double) tuple.get(1) >= 135.1 && (double) tuple.get(1) <= 157.5) {
                                                    seCount = seCount + (double) tuple.get(0);
                                                } else if ((double) tuple.get(1) >= 157.6 && (double) tuple.get(1) <= 180) {
                                                    sseCount = sseCount + (double) tuple.get(0);
                                                } else if ((double) tuple.get(1) >= 180.1 && (double) tuple.get(1) <= 202.5) {
                                                    sCount = sCount + (double) tuple.get(0);
                                                } else if ((double) tuple.get(1) >= 202.6 && (double) tuple.get(1) <= 225) {
                                                    sswCount = sswCount + (double) tuple.get(0);
                                                } else if ((double) tuple.get(1) >= 225.1 && (double) tuple.get(1) <= 247.5) {
                                                    swCount = swCount + (double) tuple.get(0);
                                                } else if ((double) tuple.get(1) >= 247.6 && (double) tuple.get(1) <= 270) {
                                                    wswCount = wswCount + (double) tuple.get(0);
                                                } else if ((double) tuple.get(1) >= 270.1 && (double) tuple.get(1) <= 292.5) {
                                                    wCount = wCount + (double) tuple.get(0);
                                                } else if ((double) tuple.get(1) >= 292.6 && (double) tuple.get(1) <= 315) {
                                                    wnwCount = wnwCount + (double) tuple.get(0);
                                                } else if ((double) tuple.get(1) >= 315.1 && (double) tuple.get(1) <= 337.5) {
                                                    nwCount = nwCount + (double) tuple.get(0);
                                                } else if ((double) tuple.get(1) >= 337.6 && (double) tuple.get(1) <= 360) {
                                                    nnwCount = nnwCount + (double) tuple.get(0);
                                                }
                                            }
                                        } else {
                                            if ((double) tuple.get(0) > windRoseRange.getScaleMin() && (double) tuple.get(0) < windRoseRange.getScaleMax()) {
                                                if ((double) tuple.get(1) >= 0 && (double) tuple.get(1) <= 22.5) {
                                                    nCount = nCount + (double) tuple.get(0);
                                                } else if ((double) tuple.get(1) >= 22.6 && (double) tuple.get(1) <= 45) {
                                                    nneCount = nneCount + (double) tuple.get(0);
                                                } else if ((double) tuple.get(1) >= 45.1 && (double) tuple.get(1) <= 67.5) {
                                                    neCount = neCount + (double) tuple.get(0);
                                                } else if ((double) tuple.get(1) >= 67.6 && (double) tuple.get(1) <= 90) {
                                                    eneCount = eneCount + (double) tuple.get(0);
                                                } else if ((double) tuple.get(1) >= 90.1 && (double) tuple.get(1) <= 112.5) {
                                                    eCount = eCount + (double) tuple.get(0);
                                                } else if ((double) tuple.get(1) >= 112.6 && (double) tuple.get(1) <= 135) {
                                                    eseCount = eseCount + (double) tuple.get(0);
                                                } else if ((double) tuple.get(1) >= 135.1 && (double) tuple.get(1) <= 157.5) {
                                                    seCount = seCount + (double) tuple.get(0);
                                                } else if ((double) tuple.get(1) >= 157.6 && (double) tuple.get(1) <= 180) {
                                                    sseCount = sseCount + (double) tuple.get(0);
                                                } else if ((double) tuple.get(1) >= 180.1 && (double) tuple.get(1) <= 202.5) {
                                                    sCount = sCount + (double) tuple.get(0);
                                                } else if ((double) tuple.get(1) >= 202.6 && (double) tuple.get(1) <= 225) {
                                                    sswCount = sswCount + (double) tuple.get(0);
                                                } else if ((double) tuple.get(1) >= 225.1 && (double) tuple.get(1) <= 247.5) {
                                                    swCount = swCount + (double) tuple.get(0);
                                                } else if ((double) tuple.get(1) >= 247.6 && (double) tuple.get(1) <= 270) {
                                                    wswCount = wswCount + (double) tuple.get(0);
                                                } else if ((double) tuple.get(1) >= 270.1 && (double) tuple.get(1) <= 292.5) {
                                                    wCount = wCount + (double) tuple.get(0);
                                                } else if ((double) tuple.get(1) >= 292.6 && (double) tuple.get(1) <= 315) {
                                                    wnwCount = wnwCount + (double) tuple.get(0);
                                                } else if ((double) tuple.get(1) >= 315.1 && (double) tuple.get(1) <= 337.5) {
                                                    nwCount = nwCount + (double) tuple.get(0);
                                                } else if ((double) tuple.get(1) >= 337.6 && (double) tuple.get(1) <= 360) {
                                                    nnwCount = nnwCount + (double) tuple.get(0);
                                                }
                                            }
                                        }
                                    }
                                }
                                double total = nCount + nneCount + neCount + eneCount + eCount + eseCount + seCount + sseCount + sCount
                                        + sswCount + swCount + wswCount + wCount + wnwCount + nwCount + nnwCount;
                                nTotal = nTotal + nCount;
                                nneTotal = nneTotal + nneCount;
                                neTotal = neTotal + neCount;
                                eneTotal = eneTotal + eneCount;
                                eTotal = eTotal + eCount;
                                eseTotal = eseTotal + eseCount;
                                seTotal = seTotal + seCount;
                                sseTotal = sseTotal + sseCount;
                                sTotal = sTotal + sCount;
                                sswTotal = sswTotal + sswCount;
                                swTotal = swTotal + swCount;
                                wswTotal = wswTotal + wswCount;
                                wTotal = wTotal + wCount;
                                wnwTotal = wnwTotal + wnwCount;
                                nwTotal = nwTotal + nwCount;
                                nnwTotal = nnwTotal + nnwCount;
                                StringBuilder range = new StringBuilder(windRoseRange.getScaleMin() + " - " + windRoseRange.getScaleMax() + " " + pmS.getUnitSymbol());
                                if (windRoseRange.getScaleMin() == null || windRoseRange.getScaleMin() <= 0.00) {
                                    range.setLength(0);
                                    range.append(" < ").append(windRoseRange.getScaleMax()).append(" ").append(pmS.getUnitSymbol());
                                } else if (windRoseRange.getScaleMax() == null) {
                                    range.setLength(0);
                                    range.append(" > ").append(windRoseRange.getScaleMin()).append(" ").append(pmS.getUnitSymbol());
                                }
                                JSONArray jsonString = new JSONArray();
//                                jsonString.put(new JSONObject().put("category", "range").put("value", range));
                                jsonString.put(new JSONObject().put("category", "N").put("value", (grandTotal > 0) ? getFormattedValue((nCount / grandTotal) * 100) : nCount));
                                jsonString.put(new JSONObject().put("category", "NNE").put("value", (grandTotal > 0) ? getFormattedValue((nneCount / grandTotal) * 100) : nneCount));
                                jsonString.put(new JSONObject().put("category", "NE").put("value", (grandTotal > 0) ? getFormattedValue((neCount / grandTotal) * 100) : neCount));
                                jsonString.put(new JSONObject().put("category", "ENE").put("value", (grandTotal > 0) ? getFormattedValue((eneCount / grandTotal) * 100) : eneCount));
                                jsonString.put(new JSONObject().put("category", "E").put("value", (grandTotal > 0) ? getFormattedValue((eCount / grandTotal) * 100) : eCount));
                                jsonString.put(new JSONObject().put("category", "ESE").put("value", (grandTotal > 0) ? getFormattedValue((eseCount / grandTotal) * 100) : eseCount));
                                jsonString.put(new JSONObject().put("category", "SE").put("value", (grandTotal > 0) ? getFormattedValue((seCount / grandTotal) * 100) : seCount));
                                jsonString.put(new JSONObject().put("category", "SSE").put("value", (grandTotal > 0) ? getFormattedValue((sseCount / grandTotal) * 100) : sseCount));
                                jsonString.put(new JSONObject().put("category", "S").put("value", (grandTotal > 0) ? getFormattedValue((sCount / grandTotal) * 100) : sCount));
                                jsonString.put(new JSONObject().put("category", "SSW").put("value", (grandTotal > 0) ? getFormattedValue((sswCount / grandTotal) * 100) : sswCount));
                                jsonString.put(new JSONObject().put("category", "SW").put("value", (grandTotal > 0) ? getFormattedValue((swCount / grandTotal) * 100) : swCount));
                                jsonString.put(new JSONObject().put("category", "WSW").put("value", (grandTotal > 0) ? getFormattedValue((wswCount / grandTotal) * 100) : wswCount));
                                jsonString.put(new JSONObject().put("category", "W").put("value", (grandTotal > 0) ? getFormattedValue((wCount / grandTotal) * 100) : wCount));
                                jsonString.put(new JSONObject().put("category", "WNW").put("value", (grandTotal > 0) ? getFormattedValue((wnwCount / grandTotal) * 100) : wnwCount));
                                jsonString.put(new JSONObject().put("category", "NW").put("value", (grandTotal > 0) ? getFormattedValue((nwCount / grandTotal) * 100) : nwCount));
                                jsonString.put(new JSONObject().put("category", "NNW").put("value", (grandTotal > 0) ? getFormattedValue((nnwCount / grandTotal) * 100) : nnwCount));
//                                jsonString.put(new JSONObject().put("category", "gTotal").put("value", total));

                                /*jsonString.put("N", (grandTotal > 0) ? getFormattedValue((nCount / grandTotal) * 100) : nCount);
                                jsonString.put("NNE", (grandTotal > 0) ? getFormattedValue((nneCount / grandTotal) * 100) : nneCount);
                                jsonString.put("NE", (grandTotal > 0) ? getFormattedValue((neCount / grandTotal) * 100) : neCount);
                                jsonString.put("ENE", (grandTotal > 0) ? getFormattedValue((eneCount / grandTotal) * 100) : eneCount);
                                jsonString.put("E", (grandTotal > 0) ? getFormattedValue((eCount / grandTotal) * 100) : eCount);
                                jsonString.put("ESE", (grandTotal > 0) ? getFormattedValue((eseCount / grandTotal) * 100) : eseCount);
                                jsonString.put("SE", (grandTotal > 0) ? getFormattedValue((seCount / grandTotal) * 100) : seCount);
                                jsonString.put("SSE", (grandTotal > 0) ? getFormattedValue((sseCount / grandTotal) * 100) : sseCount);
                                jsonString.put("S", (grandTotal > 0) ? getFormattedValue((sCount / grandTotal) * 100) : sCount);
                                jsonString.put("SSW", (grandTotal > 0) ? getFormattedValue((sswCount / grandTotal) * 100) : sswCount);
                                jsonString.put("SW", (grandTotal > 0) ? getFormattedValue((swCount / grandTotal) * 100) : swCount);
                                jsonString.put("WSW", (grandTotal > 0) ? getFormattedValue((wswCount / grandTotal) * 100) : wswCount);
                                jsonString.put("W", (grandTotal > 0) ? getFormattedValue((wCount / grandTotal) * 100) : wCount);
                                jsonString.put("WNW", (grandTotal > 0) ? getFormattedValue((wnwCount / grandTotal) * 100) : wnwCount);
                                jsonString.put("NW", (grandTotal > 0) ? getFormattedValue((nwCount / grandTotal) * 100) : nwCount);
                                jsonString.put("NNW", (grandTotal > 0) ? getFormattedValue((nnwCount / grandTotal) * 100) : nnwCount);
                                jsonString.put("gTotal", total);*/
                                array.put(i, jsonString);
                                i++;
                                jsonString = null;
                            }
                        }
                    }

                    totals.put(new JSONObject().put("category", "N").put("value", nTotal));
                    totals.put(new JSONObject().put("category", "NNE").put("value", nneTotal));
                    totals.put(new JSONObject().put("category", "NE").put("value", neTotal));
                    totals.put(new JSONObject().put("category", "ENE").put("value", eneTotal));
                    totals.put(new JSONObject().put("category", "E").put("value", eTotal));
                    totals.put(new JSONObject().put("category", "ESE").put("value", eseTotal));
                    totals.put(new JSONObject().put("category", "SE").put("value", seTotal));
                    totals.put(new JSONObject().put("category", "SSE").put("value", sseTotal));
                    totals.put(new JSONObject().put("category", "S").put("value", sTotal));
                    totals.put(new JSONObject().put("category", "SSW").put("value", sswTotal));
                    totals.put(new JSONObject().put("category", "SW").put("value", swTotal));
                    totals.put(new JSONObject().put("category", "WSW").put("value", wswTotal));
                    totals.put(new JSONObject().put("category", "W").put("value", wTotal));
                    totals.put(new JSONObject().put("category", "WNW").put("value", wnwTotal));
                    totals.put(new JSONObject().put("category", "NW").put("value", nwTotal));
                    totals.put(new JSONObject().put("category", "NNW").put("value", nnwTotal));
                    obj.put("data", array);
                    obj.put("total", totals);
                    array = null;
                    wdData = null;
                    totals = null;
                    pmS = null;
                    pmD = null;
                    return obj;
                } else {
                    obj.put("error","Parameter or Station is incorrect");
                    return obj;
                }
            } else {
                obj.put("error","Statistics can be generated for maximum 6 months");
                return obj;
            }
        } catch (Exception e) {
            log.error("Error in loadWindRose method : "+e.getMessage());
        }
        return obj;
    }
    public double applyOperation(String beforeOp, String operation, BigDecimal afterOp){
        double data =Double.parseDouble(beforeOp);
        if (operation.equals("*")) {
            data *= afterOp.doubleValue();
        } else if (operation.equals("+")) {
            data += afterOp.doubleValue();
        } else if (operation.equals("-")) {
            data -= afterOp.doubleValue();
        } else if (operation.equals("/")) {
            data /= afterOp.doubleValue();
        }
        return data;
    }
    private double getFormattedValue(double v) {
        DecimalFormat df = new DecimalFormat("#.##");
        return Double.parseDouble(df.format(v));
    }
}
