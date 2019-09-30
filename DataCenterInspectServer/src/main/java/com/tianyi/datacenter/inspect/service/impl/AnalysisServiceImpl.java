package com.tianyi.datacenter.inspect.service.impl;

import cn.hutool.core.util.NumberUtil;
import com.tianyi.datacenter.common.util.TimeUtil;
import com.tianyi.datacenter.common.vo.ResponseVo;
import com.tianyi.datacenter.feign.common.util.DSParamBuilder;
import com.tianyi.datacenter.feign.common.util.DSParamDsBuilder;
import com.tianyi.datacenter.feign.service.DataCenterFeignService;
import com.tianyi.datacenter.inspect.dao.AnalysisDao;
import com.tianyi.datacenter.inspect.service.AnalysisService;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author liulele ${Date}
 * @version 0.1
 **/
@Service
@Slf4j
public class AnalysisServiceImpl implements AnalysisService {
    private static final String format = "yyyy-MM-dd";
    private static final String formatMonth = "yyyy-MM ";
    private static final String formatYear = "yyyy";
    @Autowired
    private AnalysisDao analysisDao;
    @Autowired
    private DataCenterFeignService dataCenterFeignService;

    /**
     * @param param
     * @return
     */


    @Override
    public List<Map<String, Object>> queryByDayinit(Map<String, String> param) {
        //当前日期
        Date dateTime = new DateTime().toDate();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        String today = simpleDateFormat.format(dateTime);
        try {
            Date parse = simpleDateFormat.parse(today);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        log.info("当前时间" + today);
        //默认下获取当前月前一天
        if (param.get("startTime") == null || param.get("startTime") == "" || param.get("startTime") == null || param.get("startTime") == "") {
            Date parse = null;
            try {
                parse = simpleDateFormat.parse(today);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(parse);
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.add(Calendar.DATE, -1);
            String firstDay = simpleDateFormat.format(calendar.getTime());
            log.info("当前日期的前一天零点" + firstDay);
            param.put("startTime", firstDay);
            param.put("endTime", today);
        } else {
            //获取输入日期的前一天
            String startTime = param.get("startTime");
            Date oneDayBefore = null;
            try {
                oneDayBefore = simpleDateFormat.parse(startTime);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(oneDayBefore);
            calendar.add(Calendar.DATE, -1);
            String oneDayBeforeGet = simpleDateFormat.format(calendar.getTime());
            param.put("startTime", oneDayBeforeGet);
        }

        List<Map<String, Object>> maps = analysisDao.queryByDay(param);

        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        if (maps == null || maps.size() == 0) {
            return list;
        }
        Map<String, Object> returnMap = new HashMap<>();
        //数据库中的第一条数据的天数
        String eachDay = (String) maps.get(0).get("everyday");
        long count = 0L;
        double minute = 0.00;
        double minuteSum = 0.00;
        long times = 0L;
        for (int i = 0; i < maps.size(); i++) {
            //数据库中的每一天
            String everyday = (String) maps.get(i).get("everyday");
            if (eachDay.equals(everyday)) {
                String checkStart = (String) maps.get(i).get("checkStart");
                String checkEnd = (String) maps.get(i).get("checkEnd");
                TimeUtil timeUtil = new TimeUtil();
                //获取这一天的检查时间
                minute = timeUtil.getMinute(checkStart, checkEnd);
                count++;
                minuteSum += minute;
                times++;
                if (times == maps.size()) {
                    double avg = minuteSum / count;
                    returnMap.put("today", Integer.parseInt(everyday));
                    returnMap.put("avg", avg);
                    list.add(returnMap);
                    return list;
                }
            } else {
                double avg = minuteSum / count;
                returnMap.put("today", (String) maps.get(i - 1).get("everyday"));
                returnMap.put("avg", avg);
                list.add(returnMap);
                //next  如果相同那么就统计
                long countNext = 0L;
                double minuteNext = 0.00;
                double minuteSumNext = 0.00;
                for (int y = 0; y < maps.size(); y++) {
                    if (everyday.equalsIgnoreCase((String) maps.get(y).get("everyday"))) {
                        String checkStart = (String) maps.get(y).get("checkStart");
                        String checkEnd = (String) maps.get(y).get("checkEnd");
                        TimeUtil timeUtil = new TimeUtil();
                        //获取这一天的检查时间
                        minuteNext = timeUtil.getMinute(checkStart, checkEnd);
                        countNext++;
                        minuteSumNext += minuteNext;
                    } else {
                        continue;
                    }
                }
                double avgNext = minuteSumNext / countNext;
                Map<String, Object> resultMapNext = new HashMap<>();
                resultMapNext.put("today", everyday);
                resultMapNext.put("avg", avgNext);
                list.add(resultMapNext);
                return list;
            }
        }
        return list;
    }


    @Override
    //按照月份查询
    public List<Map<String, Object>> getinitByMonth(Map<String, String> param) {

        //当前日期
        Date dateTime = new DateTime().toDate();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(formatMonth);
        String today = simpleDateFormat.format(dateTime);
        log.info("当前时间" + today);
        //获取当前月第一天
        if (param.get("startTime") == null || param.get("startTime") == "" || param.get("startTime") == null || param.get("startTime") == "") {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            calendar.set(Calendar.MONTH, 0);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            String firshMonth = simpleDateFormat.format(calendar.getTime());
            log.info("当前日期的第一月零点" + firshMonth);
            param.put("startTime", firshMonth);
            param.put("endTime", today);
        } else {
            String startTime = param.get("startTime");
            Date sixMonthBefore = null;
            try {
                sixMonthBefore = simpleDateFormat.parse(startTime);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(sixMonthBefore);
            calendar.add(Calendar.MONTH, -6);

            String sixMonthBeforeGet = simpleDateFormat.format(calendar.getTime());
            param.put("startTime", sixMonthBeforeGet);
        }


        List<Map<String, Object>> maps = analysisDao.queryByMouth(param);


        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        if (maps == null || maps.size() == 0) {
            return list;
        }
        Map<String, Object> returnMap = new HashMap<>();
        //数据库中的第一条数据的天数
        String eachMonth = (String) maps.get(0).get("everyMonth");
        long count = 0L;
        double minute = 0.00;
        double minuteSum = 0.00;
        long times = 0L;
        for (int i = 0; i < maps.size(); i++) {
            //数据库中的每一天
            String everyMonth = (String) maps.get(i).get("everyMonth");

            if (eachMonth.equals(everyMonth)) {
                String checkStart = (String) maps.get(i).get("checkStart");
                String checkEnd = (String) maps.get(i).get("checkEnd");
                TimeUtil timeUtil = new TimeUtil();
                //获取这一天的检查时间
                minute = timeUtil.getMinute(checkStart, checkEnd);
                count++;
                minuteSum += minute;
                times++;
                if (times == maps.size()) {
                    double avg = minuteSum / count;
                    returnMap.put("month", everyMonth);
                    returnMap.put("avg", avg);
                    list.add(returnMap);
                    return list;
                }
            } else {
                double avg = minuteSum / count;
                returnMap.put("month", (String) maps.get(i - 1).get("everyMonth"));
                returnMap.put("avg", avg);
                list.add(returnMap);
                //next  如果相同那么就统计
                long countNext = 0L;
                double minuteNext = 0.00;
                double minuteSumNext = 0.00;
                for (int y = 0; y < maps.size(); y++) {
                    if (everyMonth.equalsIgnoreCase((String) maps.get(y).get("everyMonth"))) {
                        String checkStart = (String) maps.get(y).get("checkStart");
                        String checkEnd = (String) maps.get(y).get("checkEnd");
                        TimeUtil timeUtil = new TimeUtil();
                        //获取这一天的检查时间
                        minuteNext = timeUtil.getMinute(checkStart, checkEnd);
                        countNext++;
                        minuteSumNext += minuteNext;
                    } else {
                        continue;
                    }
                }
                double avgNext = minuteSumNext / countNext;
                Map<String, Object> resultMapNext = new HashMap<>();
                resultMapNext.put("month", everyMonth);
                resultMapNext.put("avg", avgNext);
                list.add(resultMapNext);
                return list;
            }
        }
        return list;
    }

    @Override
    public List<Map<String, Object>> getinitByYear(Map<String, String> param) {
        //当前日期
        Date dateTime = new DateTime().toDate();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(formatYear);
        String today = simpleDateFormat.format(dateTime);
        log.info("当前时间" + today);
        //获取今年往前推6年
        if (param.get("startTime") == null || param.get("startTime") == "" || param.get("startTime") == null || param.get("startTime") == "") {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());

            calendar.add(Calendar.YEAR, -6);
            String beforeSix = simpleDateFormat.format(calendar.getTime());
            log.info("获取当前时间的前六年" + beforeSix);
            param.put("startTime", beforeSix);
            param.put("endTime", today);
        } else {
            //获取当前时间前6年
            String startYear = param.get("startTime");
            Date sixYearBefore = null;
            try {
                sixYearBefore = simpleDateFormat.parse(startYear);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(sixYearBefore);

            calendar.add(Calendar.YEAR, -6);
            String sixYearBeforeGet = simpleDateFormat.format(calendar.getTime());
            log.info("获取查询时间的前六年" + sixYearBeforeGet);
            param.put("startTime", sixYearBeforeGet);
        }

        List<Map<String, Object>> maps = analysisDao.queryByYear(param);

        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        if (maps == null || maps.size() == 0) {
            return list;
        }
        Map<String, Object> returnMap = new HashMap<>();
        //数据库中的第一条数据的天数
        String eachYear = (String) maps.get(0).get("everyYear");
        long count = 0L;
        double minute = 0.0;
        double minuteSum = 0.0;
        long times = 0L;
        for (int i = 0; i < maps.size(); i++) {
            //数据库中的每一天
            String everyYear = (String) maps.get(i).get("everyYear");
            if (eachYear.equals(everyYear)) {
                String checkStart = (String) maps.get(i).get("checkStart");
                String checkEnd = (String) maps.get(i).get("checkEnd");
                TimeUtil timeUtil = new TimeUtil();
                //获取这一天的检查时间
                minute = timeUtil.getMinute(checkStart, checkEnd);
                count++;
                minuteSum += minute;
                times++;
                if (times == maps.size()) {
                    double avg = minuteSum / count;
                    returnMap.put("year", everyYear);
                    returnMap.put("avg", avg);
                    list.add(returnMap);
                    return list;
                }
            } else {
                double avg = minuteSum / count;
                returnMap.put("year", Integer.parseInt((String) maps.get(i - 1).get("everyYear")));
                returnMap.put("avg", avg);
                list.add(returnMap);
                //next  如果相同那么就统计
                long countNext = 0L;
                double minuteNext = 0.00;
                double minuteSumNext = 0.00;
                for (int y = 0; y < maps.size(); y++) {
                    if (everyYear.equalsIgnoreCase((String) maps.get(y).get("everyYear"))) {
                        String checkStart = (String) maps.get(y).get("checkStart");
                        String checkEnd = (String) maps.get(y).get("checkEnd");
                        TimeUtil timeUtil = new TimeUtil();
                        //获取这一天的检查时间
                        minuteNext = timeUtil.getMinute(checkStart, checkEnd);
                        countNext++;
                        minuteSumNext += minuteNext;
                    } else {
                        continue;
                    }
                }
                double avgNext = minuteSumNext / countNext;
                Map<String, Object> resultMapNext = new HashMap<>();
                resultMapNext.put("year", everyYear);
                resultMapNext.put("avg", avgNext);
                list.add(resultMapNext);
                return list;
            }
        }
        return list;
    }

    @Override
    public List<Map<String, Object>> selectByDate(Map<String, String> param) {
        Map<String, Object> resultMap = new HashMap();
        List<Map<String, Object>> list = new ArrayList<>();
        resultMap.put("typeName", param.get("typeName"));
//        resultMap.put("modelName", param.get("modelName"));
        // resultMap.put("stationName", param.get("stationName"));

        //根据机型获取车辆ID
        //{"condition":{"typeName":"挖掘机"},"dataObjectId":46,"pageInfo":{"page":1,"count":0,"pageSize":100,"total":2}}
        DSParamDsBuilder dsParamDsBuilder = new DSParamDsBuilder(46);
        dsParamDsBuilder.buildCondition("modelName", (String) param.get("modelName"));
        dsParamDsBuilder.buildCondition("typeName", (String) param.get("typeName"));
        dsParamDsBuilder.buildCondition("stationName", (String) param.get("stationName"));
        dsParamDsBuilder.buildCondition("checktypeName", (String) param.get("checktypeName"));
        com.tianyi.datacenter.feign.common.vo.ResponseVo responseVo = dataCenterFeignService.retrieve(dsParamDsBuilder.build());
        if (responseVo.isSuccess()) {
            List<Map<String, Object>> rtnData = (List<Map<String, Object>>) responseVo.getData().get("rtnData");
            if (rtnData != null && rtnData.size() != 0) {
                //"rtnData":[{"cid":"DBBJT8","modelName":"PC200-8","typeName":"挖掘机","checktypeName":"油水检查","checktypeNum":"ST3600-010"},
                for (Map<String, Object> rtnDatum : rtnData) {
                    String cid = (String) rtnDatum.get("cid");
                    String modelName = (String) rtnDatum.get("modelName");
                    String checktypeNum = (String) rtnDatum.get("checktypeNum");
                    String checktypeName = (String) rtnDatum.get("checktypeName");
                    String stationName = (String) rtnDatum.get("stationName");
                    resultMap.put("jihao", cid);
                    resultMap.put("checktypeNum", checktypeNum);
                    resultMap.put("modelName", modelName);
                    resultMap.put("checktypeName", checktypeName);
                    resultMap.put("stationName", stationName);
                    String startTime = (String) param.get("startTime");
                    String endTime = (String) param.get("endTime");
                    //通过车辆cid查询检查工单id
                    //{"condition":[{"key":"carId","condition":"equals","value":"DBBJT8"}],
                    // "dataObjectId":16,"pageInfo":{"page":1,"count":0,"pageSize":100,"total":2}}
                    DSParamBuilder dsParamBuilderCheckOrder = new DSParamBuilder(16);
                    dsParamBuilderCheckOrder.buildCondition("carId", "equals", cid);
                    com.tianyi.datacenter.feign.common.vo.ResponseVo responseVoCheckOrder = dataCenterFeignService.retrieve(dsParamBuilderCheckOrder.build());
                    if (responseVoCheckOrder.isSuccess()) {
                        List<Map<String, Object>> rtnDataCheckType = (List<Map<String, Object>>) responseVoCheckOrder.getData().get("rtnData");
                        if (rtnDataCheckType != null && rtnDataCheckType.size() != 0) {
                            for (Map<String, Object> map : rtnDataCheckType) {
                                String checkOrderCid = (String) map.get("cid");
                                //根据工单ID获取检查工单项目
                                //{"condition":[{"key":"itemId","condition":"equals","value":"1"}],"dataObjectId":17,
                                // "pageInfo":{"page":1,"count":0,"pageSize":100,"total":5}}
                                //{"condition":[{"key":"checkStart","condition":"greate than equals","value":"dd"}],
                                // "dataObjectId":17,"pageInfo":{"page":1,"count":
                                DSParamBuilder dsParamBuildercheckOrderItem = new DSParamBuilder(17);
                                dsParamBuildercheckOrderItem.buildCondition("checkorderId", "equals", checkOrderCid);
                                dsParamBuildercheckOrderItem.buildCondition("checkEnd", "greate than equals", startTime);
                                dsParamBuildercheckOrderItem.buildCondition("checkEnd", "less than equals", endTime);
                                dsParamBuildercheckOrderItem.buildCondition("result", "equals", "通过");
                                com.tianyi.datacenter.feign.common.vo.ResponseVo responseVocheckOrderItem = dataCenterFeignService.retrieve(dsParamBuildercheckOrderItem.build());
                                if (responseVocheckOrderItem.isSuccess()) {
                                    List<Map<String, Object>> rtnDataCheckOrder = (List<Map<String, Object>>) responseVo.getData().get("rtnData");
                                    if (rtnDataCheckOrder != null && rtnDataCheckOrder.size() != 0) {
                                        List<Map<String, Object>> rtnDatacheckOrderItem = (List<Map<String, Object>>) responseVocheckOrderItem.getData().get("rtnData");
                                        //获取检查人
                                        StringBuffer checker = new StringBuffer();
                                        checker.append("");
                                        TimeUtil timeUtil = new TimeUtil();
                                        String minStartTime = "1111-11-11 00:00:00";
                                        String maxStartTime = "1111-11-11 00:00:00";
                                        double avgTime = 0.0;
                                        long count = 0;
                                        for (int i = 0; i < rtnDatacheckOrderItem.size(); i++) {
                                            String checkers = (String) rtnDatacheckOrderItem.get(i).get("checker");
                                            if (i == rtnDatacheckOrderItem.size() - 1) {
                                                checker.append(checkers);
                                            } else {
                                                checker.append(checkers + ",");
                                            }
                                            String checkStart = (String) rtnDatacheckOrderItem.get(i).get("checkStart");
                                            if (minStartTime.compareTo(checkStart) < 0) {
                                                minStartTime = checkStart;
                                            }
                                            String checkEnd = (String) rtnDatacheckOrderItem.get(i).get("checkEnd");
                                            if (checkEnd.compareTo(maxStartTime) > 0) {
                                                maxStartTime = checkEnd;
                                            }
                                            double minute = timeUtil.getMinute(checkStart, checkEnd);
                                            avgTime += minute;
                                            count++;
                                        }
                                        resultMap.put("checker", checker.toString());
                                        resultMap.put("startTime", minStartTime);
                                        resultMap.put("endTime", maxStartTime);
                                        if (count != 0) {
                                            double finalavgTime = avgTime / count;
                                            //获取检查平均时间
                                            resultMap.put("avgTime", finalavgTime + "");
                                        }
                                    }
                                }
                            }
                            list.add(resultMap);
                        }
                    }
                }
                return list;
            }
            return new ArrayList<>();
        }
        return new ArrayList<>();
    }


    @Override
    @Transactional
    public List<Map<String, Object>> getProblemByDay(Map<String, String> param) {
        List<Map<String, Object>> problemByDay = new ArrayList<>();
        if (param.get("dimension") != null && param.get("dimension").equalsIgnoreCase("按日")) {


            problemByDay = analysisDao.getProblemByDay(param);
        }

        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        List<Map<String, Object>> listHaveProblem = new ArrayList<Map<String, Object>>();
        if (problemByDay == null || problemByDay.size() == 0) {
            return list;
        }
        Map<String, Object> returnMap = new LinkedHashMap<>();
        //数据库中依据天来统计这一天总共有多少个检查项
        String eachDay = (String) problemByDay.get(0).get("everyday");
        long count = 0L;
        double minute = 0.00;
        double minuteSum = 0.00;
        long times = 0L;
        for (int i = 0; i < problemByDay.size(); i++) {
            //数据库中的每一天
            String everyday = (String) problemByDay.get(i).get("everyday");
            if (eachDay.equals(everyday)) {
                String checkStart = (String) problemByDay.get(i).get("checkStart");
                String checkEnd = (String) problemByDay.get(i).get("checkEnd");
                count++;
                times++;
                if (times == problemByDay.size()) {
                    double avg = minuteSum / count;
                    returnMap.put("total", count);
                    returnMap.put("today", everyday);
                    list.add(returnMap);
                }
            } else {
                returnMap.put("total", count);
                returnMap.put("today", (String) problemByDay.get(i - 1).get("everyday"));
                list.add(returnMap);
                //next  如果相同那么就统计
                long countNext = 0L;
                for (int y = 0; y < problemByDay.size(); y++) {
                    if (everyday.equalsIgnoreCase((String) problemByDay.get(y).get("everyday"))) {
                        countNext++;
                    } else {
                        continue;
                    }
                }
                Map<String, Object> resultMapNext = new HashMap<>();
                resultMapNext.put("total", countNext);
                resultMapNext.put("today", everyday);
                list.add(resultMapNext);
            }
        }


        List<Map<String, Object>> problemByDayHaveProblem = analysisDao.getProblemByDayHaveProblem(param);
        //统计这一天有多少个异常项
        for (int i = 0; i < problemByDayHaveProblem.size(); i++) {
            //数据库中的每一天
            String everyday = (String) problemByDayHaveProblem.get(i).get("everyday");
            if (eachDay.equals(everyday)) {
                String checkStart = (String) problemByDayHaveProblem.get(i).get("checkStart");
                String checkEnd = (String) problemByDayHaveProblem.get(i).get("checkEnd");
                count++;
                times++;
                if (times == problemByDayHaveProblem.size()) {
                    double avg = minuteSum / count;
                    returnMap.put("total", count);
                    returnMap.put("today", everyday);
                    listHaveProblem.add(returnMap);
                }
            } else {
                returnMap.put("total", count);
                returnMap.put("today", (String) problemByDayHaveProblem.get(i - 1).get("everyday"));
                listHaveProblem.add(returnMap);
                //next  如果相同那么就统计
                long countNext = 0L;
                for (int y = 0; y < problemByDayHaveProblem.size(); y++) {
                    if (everyday.equalsIgnoreCase((String) problemByDayHaveProblem.get(y).get("everyday"))) {
                        countNext++;
                    } else {
                        continue;
                    }
                }
                Map<String, Object> resultMapNext = new HashMap<>();
                resultMapNext.put("total", countNext);
                resultMapNext.put("today", everyday);
                listHaveProblem.add(resultMapNext);
            }
        }

        List<Map<String, Object>> resultList = new ArrayList<>();
        Map<String, Object> resultMap = new LinkedHashMap<>();
        //合格率  todo
        if (problemByDayHaveProblem.size() == 0) {
            for (Map<String, Object> mapNoPronlem : problemByDay) {
                resultMap.put("today", mapNoPronlem.get("everyday"));
                resultMap.put("rate", 100);
                resultList.add(resultMap);
            }
            return resultList;
        }
        for (Map<String, Object> map : problemByDay) {
            for (Map<String, Object> mapHiveProblem : problemByDayHaveProblem) {
                if (map.get("everyday").equals(mapHiveProblem.get("everyday"))) {
                    Long total = (Long) map.get("everyday");
                    Long totalHiveProblem = (Long) mapHiveProblem.get("everyday");
                    Long noProblem = total - totalHiveProblem;
                    // 创建一个数值格式化对象
                    NumberFormat numberFormat = NumberFormat.getInstance();
                    // 设置精确到小数点后2位
                    numberFormat.setMaximumFractionDigits(2);
                    String result = numberFormat.format((float) noProblem / (float) total * 100);
                    int finalResult = Integer.parseInt(result);
                    resultMap.put("today", map.get("everyday"));
                    resultMap.put("rate", Integer.parseInt(result));
                } else {
                    resultMap.put("today", map.get("everyday"));
                    resultMap.put("rate", 100);
                }
            }
            resultList.add(resultMap);
        }
        return resultList;
    }

    @Override
    public List<Map<String, Object>> getProblemByMonth(Map<String, String> param) {
        return null;
    }

    @Override
    public List<Map<String, Object>> getProblemByYear(Map<String, String> param) {
        return null;
    }

    /**
     * 车辆一次交检报告查询   新原型
     *
     * @param param
     * @return
     */
    @Override
    public ResponseVo onetime(Map<String, String> param) {
        List<Map> numList = new ArrayList<>();
        List<Date> date = new ArrayList<>();

        List<Map<String, Object>> CheckOrderItemListTotal = new ArrayList<>();
        DSParamDsBuilder dsParamDsBuilder = new DSParamDsBuilder(76);
        try {
            dsParamDsBuilder.buildCondition("endTime", TimeUtil.parseTime(param.get("endTime")));

            dsParamDsBuilder.buildCondition("startTime", TimeUtil.parseTime(param.get("startTime")));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (param.get("endTime") == null || param.get("endTime") == "" || param.get("startTime") == null || param.get("startTime") == "") {
            return ResponseVo.fail("日期为必填项");
        }

        List<Map<String, Object>> resultList = new ArrayList<>();

        try {
            //多获取后一个月
            date = TimeUtil.getMouthGroup(param.get("startTime"), param.get("endTime"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        dsParamDsBuilder.buildCondition("stationName", param.get("stationName"));
        dsParamDsBuilder.buildCondition("modelName", param.get("modelName"));
        dsParamDsBuilder.buildCondition("typeName", param.get("typeName"));

        com.tianyi.datacenter.feign.common.vo.ResponseVo retrieve = dataCenterFeignService.retrieve(dsParamDsBuilder.build());
        List<Map<String, Object>> rtnData = (List<Map<String, Object>>) retrieve.getData().get("rtnData");
        float passNoCar = 0L;
        float problemNoCar = 0L;
        if (retrieve.isSuccess() && retrieve.getMessage() == null) {
            for (int i = 0; i < date.size(); i++) {
                Map<String, Object> numMap = new HashMap<>();
                float pass = 0L;
                float problem = 0L;
                for (Map<String, Object> stringStringMap : rtnData) {
                    String checkEnd = (String) stringStringMap.get("checkEnd");
                    String checkStart = (String) stringStringMap.get("checkStart");
                    Date dateEnd = null;
                    Date dateStart = null;
                    try {
                        if (checkEnd != null && checkEnd != "" && checkStart != null && checkStart != "") {
                            dateEnd = TimeUtil.parseLong(checkEnd);
                            dateStart = TimeUtil.parseLong(checkStart);
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    if (i <= date.size() - 2) {
                        //判断工单是不是在 所选日期的各个月内
                        if ((date.get(i).before(dateStart) || date.get(i).equals(dateStart)) && (date.get(i + 1).after(dateEnd) || date.get(i).equals(dateEnd))) {
                            if (((Integer) stringStringMap.get("result")) == 0) {
                                //合格车的数量
                                pass++;
                            } else {
                                //不合格车的数量
                                problem++;
                            }
                            numMap.put("pass", pass);
                            numMap.put("problem", problem);
                            numMap.put("date", TimeUtil.parse(date.get(i)));
                        } else {
                            continue;
                        }
                    }
                }
                if (numMap.size() == 0) {
                    numMap.put("pass", passNoCar);
                    numMap.put("problem", problemNoCar);
                    numMap.put("date", TimeUtil.parse(date.get(i)));
                    numList.add(numMap);
                } else {
                    numList.add(numMap);
                }
            }
        } else {
            return ResponseVo.success("", "没有查询到数据!");
        }
        numList.remove(numList.size()-1);
        //添加合格率
        for (Map map : numList) {
            float pass = (float) map.get("pass");
            float problem = (float) map.get("problem");
            if ((pass + problem) == 0) {
                map.put("hegelv", "0.00%");
            } else {
                float baifenbi = (float) pass / (float) (pass + problem);
                String hegelv = NumberUtil.decimalFormat("#.00%", baifenbi);
                String substring = hegelv.substring(0, hegelv.length() - 1);
                double v = Double.parseDouble(substring);
                map.put("hegelv", v);
            }
        }
        return ResponseVo.success(numList);
    }

    @Override
    public ResponseVo onetimeByDay(Map<String, String> param) {
        List<Map> numList = new ArrayList<>();

        List<Map<String, Object>> CheckOrderItemListTotal = new ArrayList<>();


        DSParamDsBuilder dsParamDsBuilder = new DSParamDsBuilder(77);
        try {
            dsParamDsBuilder.buildCondition("endTime", TimeUtil.parseTime(param.get("endTime")));

            dsParamDsBuilder.buildCondition("startTime", TimeUtil.parseTime(param.get("startTime")));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (param.get("endTime") == null || param.get("endTime") == "" || param.get("startTime") == null || param.get("startTime") == "") {
            return ResponseVo.fail("日期为必填项");
        }
        List<Date> date = new ArrayList<>();
        String month = param.get("month");

        List<Map<String, Object>> resultList = new ArrayList<>();
        try {
            //多获取后一天
            date = TimeUtil.getDayGroup(month);
        } catch (Exception e) {
            e.printStackTrace();
        }


        dsParamDsBuilder.buildCondition("stationName", param.get("stationName"));
        dsParamDsBuilder.buildCondition("modelName", param.get("modelName"));
        dsParamDsBuilder.buildCondition("typeName", param.get("typeName"));

        com.tianyi.datacenter.feign.common.vo.ResponseVo retrieve = dataCenterFeignService.retrieve(dsParamDsBuilder.build());
        if (retrieve.isSuccess() && retrieve.getMessage() == null) {
            List<Map<String, Object>> rtnData = (List<Map<String, Object>>) retrieve.getData().get("rtnData");
            float passNoCar = 0L;
            float problemNoCar = 0L;
            for (int i = 0; i < date.size(); i++) {
                Map<String, Object> numMap = new HashMap<>();
                float pass = 0L;
                float problem = 0L;
                for (Map<String, Object> stringStringMap : rtnData) {
                    String checkEnd = (String) stringStringMap.get("checkEnd");
                    String checkStart = (String) stringStringMap.get("checkStart");
                    Date dateEnd = null;
                    Date dateStart = null;
                    try {
                        if (checkEnd != null && checkEnd != "" && checkStart != null && checkStart != "") {
                            dateEnd = TimeUtil.parseLong(checkEnd);
                            dateStart = TimeUtil.parseLong(checkStart);
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    if (i <= date.size() - 2) {
                        //判断工单是不是在 所选日期的各个月内
                        if ((date.get(i).before(dateStart) || date.get(i).equals(dateStart)) && (date.get(i + 1).after(dateEnd) || date.get(i).equals(dateEnd))) {
                            if (((Integer) stringStringMap.get("result")) == 0) {
                                //合格车的数量
                                pass++;
                            } else {
                                //不合格车的数量
                                problem++;
                            }
                            numMap.put("pass", pass);
                            numMap.put("problem", problem);
                            numMap.put("date", TimeUtil.parseDay(date.get(i)));
                        } else {
                            continue;
                        }
                    }
                }
                if (numMap.size() == 0) {
                    numMap.put("pass", passNoCar);
                    numMap.put("problem", problemNoCar);
                    numMap.put("date", TimeUtil.parseDay(date.get(i)));
                    numList.add(numMap);
                } else {
                    numList.add(numMap);
                }
            }
        } else {
            return ResponseVo.success("", "没有查询到数据!");
        }
        numList.remove(numList.size()-1);

        //添加合格率
        for (Map map : numList) {
            float pass = (float) map.get("pass");
            float problem = (float) map.get("problem");
            if ((pass + problem) == 0) {
                map.put("hegelv", "0.00%");
            } else {
                float baifenbi = (float) pass / (float) (pass + problem);
                String hegelv = NumberUtil.decimalFormat("#.00%", baifenbi);
                String substring = hegelv.substring(0, hegelv.length() - 1);
                double v = Double.parseDouble(substring);
                map.put("hegelv", v);
            }
        }
        return ResponseVo.success(numList);
    }
}
