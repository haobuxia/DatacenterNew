package com.tianyi.datacenter.inspect.controller;

import com.tianyi.datacenter.common.framework.controller.SuperController;
import com.tianyi.datacenter.common.util.upRate;
import com.tianyi.datacenter.common.vo.ResponseVo;
import com.tianyi.datacenter.inspect.service.AnalysisService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by tianxujin on 2019/5/13 10:56
 */
@Api("统计分析接口")
@RestController
@RequestMapping("inspect/analysis")
public class AnalysisController extends SuperController {
    @Autowired
    private AnalysisService analysisService;
/*
    @ApiOperation(value = "检查人员工作效率图")
    @RequestMapping(value = "queryByDay", method = RequestMethod.POST)
//    @Transactional(propagation = Propagation.REQUIRED,readOnly = false)
    public ResponseVo getEfficiency(

            @ApiParam(value = "\"dimension\":\"按日\",\n" +
                    "\"startTime\":\"2019-06-01 11:11:12\",\n" +
                    "\"endTime\":\"2019-06-01 11:20:12\",\n" +
                    "\"typeName\": \"挖掘机\",  \n" +
                    "\"modelName\": \"PC200-8\",  \n" +
                    "\"station\": \"台上检查\",  \n" +
                    "\"checktypeName\": \"油水检查\",\n" +
                    "\"checker\" :\"检查人\"", required = true) @RequestBody Map<String, String> param

    ) {
        if (param != null && param.get("dimension") != null) {
            Object dimension = param.get("dimension");
            List<Map<String, Object>> checkMin = new ArrayList<>();
            if (dimension.equals("按日")) {
                checkMin = analysisService.queryByDay(param);
            }

            Map<String, List<Map<String, Object>>> map = new HashMap<>();
            map.put("rtnData", checkMin);
            return ResponseVo.success(map);
        }
        return ResponseVo.fail("查询条件为空，请重新输入");
    }*/

    @ApiOperation(value = "检查人员工作效率图(默认按日)")
    @RequestMapping(value = "queryByDay", method = RequestMethod.POST)
//    @Transactional(propagation = Propagation.REQUIRED,readOnly = false)
    public ResponseVo getinit(

            @ApiParam(value = "\"dimension\":\"按日\",\n" +
                    "\"startTime\":\"2019-06-01 11:11:12\",\n" +
                    "\"endTime\":\"2019-06-01 11:20:12\",\n" +
                    "\"typeName\": \"挖掘机\",  \n" +
                    "\"modelName\": \"PC200-8\",  \n" +
                    "\"station\": \"台上检查\",  \n" +
                    "\"checktypeName\": \"油水检查\",\n" +
                    "\"checker\" :\"检查人\"", required = true) @RequestBody Map<String, String> param) {
        if (param != null && param.get("dimension") != null) {
            Object dimension = param.get("dimension");
            List<Map<String, Object>> checkMin = new ArrayList<>();
            if (dimension.equals("按日")) {
                checkMin = analysisService.queryByDayinit(param);

            }
            //判断前一天是不是为空  为空list的第一个为0
            upRate upRate = new upRate();
            List list = upRate.upRate(checkMin);

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
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
            String today = (String) checkMin.get(0).get("today");
            if (today.equalsIgnoreCase(oneDayBeforeGet)) {
                logger.info("前一天存在检查项的平均时长");
                for (int i = 0; i < checkMin.size(); i++) {
                    if (i <= checkMin.size() - 2) {
                        checkMin.get(i + 1).put("update", list.get(i));
                    }
                }
                checkMin.remove(0);
            } else {
                logger.info("前一天不存在检查项的平均时长");
                //查询中没有前一天  list的第一个设置为0
                List listNoBefore = upRate.upRateNoBefore(checkMin);
                for (int i = 0; i < checkMin.size(); i++) {
                    if (i <= checkMin.size() - 2) {
                        checkMin.get(i + 1).put("update", listNoBefore.get(i));
                    }
                }
                checkMin.remove(0);
            }

            Map<String, List<Map<String, Object>>> map = new HashMap<>();
            map.put("rtnData", checkMin);
            return ResponseVo.success(map);
        }
        return ResponseVo.fail("查询条件为空，请重新输入");
    }

    @ApiOperation(value = "检查人员工作效率图(默认按月)")
    @RequestMapping(value = "queryByMonth", method = RequestMethod.POST)
//    @Transactional(propagation = Propagation.REQUIRED,readOnly = false)
    public ResponseVo getinitByMonth(
            @RequestBody Map<String, String> param) {
        if (param != null && param.get("dimension") != null) {
            Object dimension = param.get("dimension");
            List<Map<String, Object>> checkMin = new ArrayList<>();
            if (dimension.equals("按月")) {
                checkMin = analysisService.getinitByMonth(param);
            }

            //判断前一月是不是为空  为空list的第一个为0
            upRate upRate = new upRate();


            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM");
            String startTime = param.get("startTime");
            Date oneDayBefore = null;
            try {
                oneDayBefore = simpleDateFormat.parse(startTime);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(oneDayBefore);
            calendar.add(Calendar.MONTH, -1);
            String oneMonthBeforeGet = simpleDateFormat.format(calendar.getTime());
            String today = (String) checkMin.get(0).get("month");
            if (today.equalsIgnoreCase(oneMonthBeforeGet)) {
                List list = upRate.upRate(checkMin);
                logger.info("前一个月存在检查项的平均时长");
                for (int i = 0; i < checkMin.size(); i++) {
                    checkMin.get(i).put("update", list.get(i));
                }
                checkMin.remove(0);
            } else {
                logger.info("前一个月不存在检查项的平均时长");
                //查询中没有前一天  list的第一个设置为0
                List listNoBefore = upRate.upRateNoBefore(checkMin);
                for (int i = 0; i < checkMin.size(); i++) {
                    checkMin.get(i).put("update", listNoBefore.get(i));
                }
            }

            Map<String, List<Map<String, Object>>> map = new HashMap<>();
            map.put("rtnData", checkMin);
            return ResponseVo.success(map);
        }
        return ResponseVo.fail("查询条件为空，请重新输入");
    }

    @ApiOperation(value = "检查人员工作效率图(按年)")
    @RequestMapping(value = "queryByYear", method = RequestMethod.POST)
//    @Transactional(propagation = Propagation.REQUIRED,readOnly = false)
    public ResponseVo getinitByYear(
            @RequestBody Map<String, String> param) {
        if (param != null && param.get("dimension") != null) {
            Object dimension = param.get("dimension");
            List<Map<String, Object>> checkMin = new ArrayList<>();
            if (dimension.equals("按年")) {
                checkMin = analysisService.getinitByYear(param);
            }

            //判断前一年是不是为空  为空list的第一个为0
            upRate upRate = new upRate();
            List list = upRate.upRate(checkMin);

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy");
            String startTime = param.get("startTime");
            Date oneDayBefore = null;
            try {
                oneDayBefore = simpleDateFormat.parse(startTime);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(oneDayBefore);
            calendar.add(Calendar.YEAR, -1);
            String oneYearBeforeGet = simpleDateFormat.format(calendar.getTime());
            String today = (String) checkMin.get(0).get("year");
            if (today.equalsIgnoreCase(oneYearBeforeGet)) {
                logger.info("前一个年存在检查项的平均时长");
                for (int i = 0; i < checkMin.size(); i++) {
                    if (i <= checkMin.size() - 2) {
                        checkMin.get(i + 1).put("update", list.get(i));
                    }
                }
                checkMin.remove(0);
            } else {
                logger.info("前一个年不存在检查项的平均时长");
                //查询中没有前一天  list的第一个设置为0
                List listNoBefore = upRate.upRateNoBefore(checkMin);
                for (int i = 0; i < checkMin.size(); i++) {
                    if (i <= checkMin.size() - 2) {
                        checkMin.get(i + 1).put("update", listNoBefore.get(i));
                    }
                }
                checkMin.remove(0);
            }

            Map<String, List<Map<String, Object>>> map = new HashMap<>();
            map.put("rtnData", checkMin);
            return ResponseVo.success(map);
        }
        return ResponseVo.fail("查询条件为空，请重新输入");
    }


    //工作效率分析图表展示
    @ApiOperation(value = "检查人员工作效率图表查询")
    @RequestMapping(value = "select", method = RequestMethod.POST)
    public ResponseVo select(
            @RequestBody Map<String, String> param) {
        String dimension = (String) param.get("dimension");
        List<Map<String, Object>> checkMin = new ArrayList<>();
        checkMin = analysisService.selectByDate(param);
        Map<String, List<Map<String, Object>>> map = new HashMap<>();
        map.put("rtnData", checkMin);
        return ResponseVo.success(map);
    }

    @ApiOperation(value = "车辆一次交检合格率")
    @RequestMapping(value = "qualify", method = RequestMethod.POST)
    public ResponseVo qualify(
            @RequestBody Map<String, String> param) {
        Object dimension = param.get("dimension");
        List<Map<String, Object>> checkMin = new ArrayList<>();
        checkMin = analysisService.getProblemByDay(param);
        Map<String, List<Map<String, Object>>> map = new HashMap<>();
        map.put("rtnData", checkMin);
        return ResponseVo.success(map);
    }

    @ApiOperation(value = "车辆一次交检合格率")
    @RequestMapping(value = "onetime", method = RequestMethod.POST)
    public ResponseVo onetime(@RequestBody Map<String, String> param) {

        if (param.get("month") == null || param.get("month") == "") {
            return analysisService.onetime(param);
        } else {
            return analysisService.onetimeByDay(param);
        }
    }
}
