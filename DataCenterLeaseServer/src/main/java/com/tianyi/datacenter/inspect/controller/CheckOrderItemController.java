package com.tianyi.datacenter.inspect.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import com.tianyi.datacenter.common.util.HttpClientUtil;
import com.tianyi.datacenter.common.util.TimeUtil;
import com.tianyi.datacenter.common.vo.ResponseVo;
import com.tianyi.datacenter.config.TianYiConfig;
import com.tianyi.datacenter.feign.common.util.DSParamBuilder;
import com.tianyi.datacenter.feign.common.util.DSParamDsBuilder;
import com.tianyi.datacenter.feign.service.DataCenterFeignService;
import com.tianyi.datacenter.inspect.service.CheckOrderItemService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import javafx.scene.input.InputMethodTextRun;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.xml.crypto.Data;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.IntStream;

/**
 * @author liulele ${Date}
 * @version 0.1
 **/
@Api("检查单详情查询")
@RestController
@RequestMapping("inspect/checkorderitem")
public class CheckOrderItemController {
    @Autowired
    private CheckOrderItemService checkOrderItemService;
    @Autowired
    private TianYiConfig tianYiIntesrvImgocrmodelVideoUrl;
    @Autowired
    private DataCenterFeignService dataCenterFeignService;
    @Autowired
    private TianYiConfig tianYiConfig;
    @Autowired
    RestTemplate template;


    @ApiOperation("检查单详情查询")
    @RequestMapping("search")
    public ResponseVo search(@RequestBody Map<String, Object> param) {

        if (param == null || param.size() == 0) {
            return ResponseVo.fail("缺少传参");
        }
        return checkOrderItemService.search(param);

    }

    @ApiOperation("图像识别结果查询")
    @RequestMapping("imgresult")
    public ResponseVo imgresult(@RequestBody Map<String, Object> param) {
        return checkOrderItemService.imgresult(param);

    }

    @ApiOperation("视频打标资源查询")
    @RequestMapping("markresult")
    public ResponseVo markresult(@RequestBody Map<String, String> param) {
        return checkOrderItemService.markresult(param);

    }

    /**
     * 结果查询
     *
     * @param param
     * @return
     */
    @RequestMapping(value = "resultSearch", method = RequestMethod.POST)
    public ResponseVo resultSearch(@RequestBody Map<String, Object> param) {

        if (param == null || param.size() == 0) {
            return ResponseVo.fail("缺少传参");
        }
        return checkOrderItemService.resultSearch(param);

    }

    @RequestMapping(value = "searchResult", method = RequestMethod.POST)
    public ResponseVo searchResult(@RequestBody Map<String, Object> param) {
        if (param == null || param.size() == 0) {
            return ResponseVo.fail("缺少传参");
        }
        return checkOrderItemService.searchResult(param);

    }


    @RequestMapping(value = "save", method = RequestMethod.POST)
    public ResponseVo save(@RequestBody List<Map<String, Object>> param) {

        if (param == null || param.size() == 0) {
            return ResponseVo.fail("缺少传参");
        }
        return checkOrderItemService.save(param);

    }

    @ApiOperation("根据视频ID查询语音弹幕")
    @RequestMapping(value = "getBarrage", method = RequestMethod.POST)
    public ResponseVo getBarrage(@RequestBody Map<String, String> param) {

        if (param == null || param.size() == 0) {
            return ResponseVo.fail("缺少传参");
        }
        return checkOrderItemService.getBarrage(param);

    }

    @ApiOperation("根据视频ID查询语音识别备注")
    @RequestMapping(value = "getVoiceRemark", method = RequestMethod.POST)
    public ResponseVo getVoiceRemark(@RequestBody Map<String, String> param) {

        if (param == null || param.size() == 0) {
            return ResponseVo.fail("缺少传参");
        }
        return checkOrderItemService.getVoiceRemark(param);

    }

    @ApiOperation("根据视频ID更新语音识别备注")
    @RequestMapping(value = "saveVoiceRemark", method = RequestMethod.POST)
    public ResponseVo updateVoiceRemark(@RequestBody Map<String, Object> param) {

        if (param == null || param.size() == 0) {
            return ResponseVo.fail("缺少传参");
        }
        return checkOrderItemService.updateVoiceRemark(param);

    }

    @ApiOperation("资料记录")
    @RequestMapping(value = "details", method = RequestMethod.POST)
    public ResponseVo details(@RequestBody Map<String, String> param) {
        //{"success":true,"code":"200","message":null,"data":{"videoList":[],"audioList":[],"imageList":[]}}
        Map<String, String> header = new HashMap<>();
        String http = HttpClientUtil.getHttpPostForm(tianYiConfig.getTianYiIntesrvUrl() + "/helmetmedia/list", param, header);
        JSONObject parse = JSONObject.parseObject(http);
        Map<String, Object> data = (Map<String, Object>) parse.get("data");//视频 照片 录音信息
        Map<String, Object> result = getresultByOrderId(param.get("orderNo"));//工单id 资料详情检查项信息
        String timeBetweenTwo = TimeUtil.getTimeBetweenTwo((String) result.get("checkStart"), (String) result.get("checkEnd"));
        result.put("checkTime", timeBetweenTwo);
        result.remove("checkEnd");
        result.putAll(data);
        return ResponseVo.success(result);
    }

    @ApiOperation("统计工况数据")
    @RequestMapping(value = "getAnalysisData", method = RequestMethod.POST)
    public ResponseVo getAnalysisData(@RequestBody Map<String, String> param) {
        ResponseVo responseVo = checkOrderItemService.getAnalysisData(param);
        return responseVo;
    }

    private Map<String, Object> getresultByOrderId(String orderId) {
        Map<String, Object> resultFinalls = new HashMap<>();//保存语音识别ID
        //1.查询 order
        DSParamBuilder dsParamBuilderOrder = new DSParamBuilder(71);
        dsParamBuilderOrder.buildCondition("oid", "equals", orderId);
        com.tianyi.datacenter.feign.common.vo.ResponseVo retrieveOrder = dataCenterFeignService.retrieve
                (dsParamBuilderOrder.build());
        if (retrieveOrder.isSuccess() && retrieveOrder.getMessage() == null) {
            DSParamDsBuilder dsParamBuilder = new DSParamDsBuilder(100);
            dsParamBuilder.buildCondition("oid", orderId);
            com.tianyi.datacenter.feign.common.vo.ResponseVo retrieveCheckOrder = dataCenterFeignService.retrieve
                    (dsParamBuilder.build());
            if (retrieveCheckOrder.isSuccess() && retrieveCheckOrder.getMessage() == null) {
                List<Map<String, Object>> rtnDataresult = (List<Map<String, Object>>) retrieveCheckOrder.getData().get("rtnData");
                //去除检查项类别名称，检查项,判断标准，检查结果
                Map<String, Object> stringObjectMap = rtnDataresult.get(0);
                stringObjectMap.remove("checktypeName");
                stringObjectMap.remove("checkitemName");
                stringObjectMap.remove("result");
                stringObjectMap.remove("standard");
                return stringObjectMap;
            }
        } else {
            //2.查询checkorder
            DSParamBuilder dsParamBuilderCheckOrder = new DSParamBuilder(16);
            dsParamBuilderCheckOrder.buildCondition("cid", "equals", orderId);
            com.tianyi.datacenter.feign.common.vo.ResponseVo retrieveCheckOrder = dataCenterFeignService.retrieve
                    (dsParamBuilderCheckOrder.build());
            if (retrieveCheckOrder.isSuccess() && retrieveCheckOrder.getMessage() == null) {
                DSParamDsBuilder dsParamBuilderCheckOrderresult = new DSParamDsBuilder(100);
                dsParamBuilderCheckOrderresult.buildCondition("coid", orderId);
                com.tianyi.datacenter.feign.common.vo.ResponseVo retrieveCheckOrderItem = dataCenterFeignService.retrieve
                        (dsParamBuilderCheckOrderresult.build());
                if (retrieveCheckOrderItem.isSuccess() && retrieveCheckOrderItem.getMessage() == null) {
                    List<Map<String, Object>> rtnDataresult = (List<Map<String, Object>>) retrieveCheckOrderItem
                            .getData()
                            .get("rtnData");
                    //去除检查项类别名称，检查项,判断标准，检查结果
                    Map<String, Object> stringObjectMap = rtnDataresult.get(0);
                    stringObjectMap.remove("checktypeName");
                    stringObjectMap.remove("checkitemName");
                    stringObjectMap.remove("result");
                    stringObjectMap.remove("standard");
                    return stringObjectMap;
                }
            } else {
                //3.查询checkorderitem
                DSParamBuilder dsParamBuilderCheckOrderItem = new DSParamBuilder(17);
                dsParamBuilderCheckOrderItem.buildCondition("cid", "equals", orderId);
                com.tianyi.datacenter.feign.common.vo.ResponseVo retrieveCheckOrderItem = dataCenterFeignService.retrieve
                        (dsParamBuilderCheckOrderItem.build());
                if (retrieveCheckOrderItem.isSuccess() && retrieveCheckOrderItem.getMessage() == null) {
                    DSParamDsBuilder dsParamBuilderCheckOrderItemResult = new DSParamDsBuilder(100);
                    dsParamBuilderCheckOrderItemResult.buildCondition("coiid", orderId);
                    com.tianyi.datacenter.feign.common.vo.ResponseVo retrieveCheckOrderItemResult = dataCenterFeignService.retrieve
                            (dsParamBuilderCheckOrderItemResult.build());
                    if (retrieveCheckOrderItemResult.isSuccess() && retrieveCheckOrderItemResult.getMessage() == null) {
                        List<Map<String, Object>> rtnDataresult = (List<Map<String, Object>>) retrieveCheckOrderItemResult
                                .getData()
                                .get("rtnData");
                        return rtnDataresult.get(0);
                    }
                }
            }
        }
        return resultFinalls;
    }

    @ApiOperation("资料详情的九宫格工况数据")
    @RequestMapping(value = "/playvideo/chartdata", method = RequestMethod.POST)
    public ResponseVo chartdata(@RequestBody Map<String, String> param) {
        param.put("videoId", "3334");
        ResponseVo responseVo = getMap(param);
        Map<String, Object> timeData = (Map<String, Object>) responseVo.getData();
        String createTimeString = (String) timeData.get("createTimeString");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date createTimeDate = new Date();
        try {
            createTimeDate = simpleDateFormat.parse(createTimeString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long createtime = createTimeDate.getTime();
        String url = tianYiConfig.getTianYiIntesrvUrl() + "/helmetmedia/playvideo/chartdata/" + param.get("videoId");
        HttpHeaders headers = new HttpHeaders();
        // 以表单的方式提交
        //定义请求头
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        //将请求头部和参数合成一个请求
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(params, headers);
        //参数请求地址,请求封装的实体,响应的类型   向头盔发送请求并获取响应信息
        ResponseEntity<ResponseVo> response1 = template.postForEntity(url, requestEntity, ResponseVo.class);
        //获取响应的响应体
        ResponseVo vo = response1.getBody();
        Map<String, Object> data = (Map<String, Object>) vo.getData();
        Map<String, List<Map<String, Object>>> val = (Map<String, List<Map<String, Object>>>) data.get("val");
        if (val.size() >= 9) {
            List<Map<String, Object>> FPCEPC = val.get("F泵PC-EPC电流mA");
            for (Map<String, Object> FpcepcMap : FPCEPC) {
                changeKey(createtime, FpcepcMap);
            }
            List<Map<String, Object>> FKGCM = val.get("F泵泵压Kg/cm2");
            for (Map<String, Object> FkgcmMap : FKGCM) {
                changeKey(createtime, FkgcmMap);
            }
            List<Map<String, Object>> RPCEPC = val.get("R泵PC-EPC电流mA");
            for (Map<String, Object> RpcepcMap : RPCEPC) {
                changeKey(createtime, RpcepcMap);
            }
            List<Map<String, Object>> RKGCM = val.get("R泵泵压Kg/cm2");
            for (Map<String, Object> RkgcmMap : RKGCM) {
                changeKey(createtime, RkgcmMap);
            }
            List<Map<String, Object>> PERSENT = val.get("扭矩百分比%");
            for (Map<String, Object> PersentMap : PERSENT) {
                changeKey(createtime, PersentMap);
            }
            List<Map<String, Object>> JIYOUYALI = val.get("机油压力");
            for (Map<String, Object> JiyouyaliMap : JIYOUYALI) {
                changeKey(createtime, JiyouyaliMap);
            }
            List<Map<String, Object>> WATTER = val.get("水温℃");
            for (Map<String, Object> WatterMap : WATTER) {
                changeKey(createtime, WatterMap);
            }
            List<Map<String, Object>> FUEL = val.get("燃料瞬间消耗L/H");
            for (Map<String, Object> FuelMap : FUEL) {
                changeKey(createtime, FuelMap);
            }
            List<Map<String, Object>> SPEED = val.get("转速rpm");
            for (Map<String, Object> SpeedMap : SPEED) {
                changeKey(createtime, SpeedMap);
            }
            return ResponseVo.success(data);
        } else {
            return ResponseVo.fail("获取kmx数据不正确！");
        }
    }

    private void changeKey(long createtime, Map<String, Object> FkgcmMap) {
        long key = (long) FkgcmMap.get("key");
        double doubleKey = Double.parseDouble(key + "");
        double doubleCreatetime = Double.parseDouble(createtime + "");
        if (doubleKey >= doubleCreatetime && doubleKey != 0 && doubleCreatetime != 0) {
            double minKey = doubleKey / 1000;
            double minCreatetime = doubleCreatetime / 1000;
            double newKey = minKey - minCreatetime;
            BigDecimal bigDecimal = new BigDecimal(newKey);
            newKey = bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();//保留两位小数
            FkgcmMap.put("key", newKey);
        }
    }

    @ApiOperation("资料详情的位置经纬度获取")
    @RequestMapping(value = "getMap", method = RequestMethod.POST)
    public ResponseVo getMap(@RequestBody Map<String, String> param) {
        param.put("videoId", "10540");
        String url = tianYiConfig.getTianYiIntesrvUrl() + "/helmetmedia/getMap/" + param.get("videoId");
        HttpHeaders headers = new HttpHeaders();
        //定义请求体
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        // 以表单的方式提交
        //定义请求头
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        //将请求头部和参数合成一个请求
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(params, headers);
        //参数请求地址,请求封装的实体,响应的类型   向头盔发送请求并获取响应信息
        ResponseEntity<ResponseVo> response1 = template.postForEntity(url, requestEntity, ResponseVo.class);
        //获取响应的响应体
        ResponseVo vo = response1.getBody();
        return ResponseVo.success(vo.getData());
    }

    @ApiOperation("资料详情的语音识别数据")
    @RequestMapping(value = "/getTagresultByVideoId", method = RequestMethod.POST)
    public ResponseVo getItemTagresult(@RequestBody Map<String, String> param) {
        ResponseVo responseVo = checkOrderItemService.getItemTagresult(param.get("videoId"));
        return responseVo;
    }

    @ApiOperation("显示图像识别视频(标注了打标的时间点)")
    @RequestMapping(value = "/getVideoList", method = RequestMethod.POST)
    public ResponseVo getVideoList(@RequestBody Map<String, String> param) {
        param.put("videoId", "10540");
        String videoId = tianYiConfig.getTianYiImgRecognition() + "/video/videoData/getVideoList?videoId=" + (String) param.get("videoId");
        String result = HttpUtil.get(videoId);
        JSONObject jsonObject = JSONObject.parseObject(result);
        return ResponseVo.success(jsonObject);
    }

    @ApiOperation("显示图像识别表单数据")
    @RequestMapping(value = "/getVideoAuditInfo", method = RequestMethod.POST)
    public ResponseVo getVideoAuditInfo(@RequestBody Map<String, String> param) {
        param.put("video", "10540");
        String videoId = tianYiConfig.getTianYiImgRecognition() + "/video/videoData/getVideoAuditInfo?videoId=" + (String) param.get("videoId");
        String result = HttpUtil.get(videoId);
        JSONObject jsonObject = JSONObject.parseObject(result);
        Map<String, Object> mapJson = (Map<String, Object>) jsonObject;
        List<Map<String, Object>> video_list = (List<Map<String, Object>>) mapJson.get("video_list");
        String jobname = (String) video_list.get(0).get("jobname");
        DSParamBuilder dsParamBuilder = new DSParamBuilder(80);//添加打标的场景ID
        dsParamBuilder.buildCondition("name", "equals", jobname);
        com.tianyi.datacenter.feign.common.vo.ResponseVo retrieve = dataCenterFeignService.retrieve(dsParamBuilder.build());
        String mid = null;
        if (retrieve.isSuccess() && retrieve.getMessage() == null) {
            List<Map<String, Object>> rtnDataItem = (List<Map<String, Object>>) retrieve.getData()
                    .get("rtnData");
            mid = (String) rtnDataItem.get(0).get("mid");
        }
        mapJson.put("modelId", mid);
        return ResponseVo.success(jsonObject);
    }

    @ApiOperation("获取图像识别图片中的打标信息")
    @RequestMapping(value = "/queryClassList", method = RequestMethod.POST)
    public ResponseVo queryClassList(@RequestBody Map<String, String> param) {
        param.put("modelId", "50");
        String videoId = tianYiConfig.getTianYiImgRecognition() + "/video/videoData/queryClassList?modelId=" + (String) param.get("modelId");
        String result = HttpUtil.get(videoId);
        JSONObject jsonObject = JSONObject.parseObject(result);
        return ResponseVo.success(jsonObject);
    }

    @ApiOperation("获取图像识别图片")
    @RequestMapping(value = "/getPicinfo", method = RequestMethod.POST)
    public ResponseVo getPicinfo(@RequestBody Map<String, String> param) {
        param.put("modelId", "50");
        param.put("video", "1333");
        param.put("frames", "221");
        String videoId = tianYiConfig.getTianYiImgRecognition() + "/video/videoData/getPicinfo?videoId="
                + (String) param.get("videoId") + "&frames=" + (String) (param.get("frames") + "")
                + "&modelId=" + (String) param.get("modelId");
        String result = HttpUtil.get(videoId);
        JSONObject jsonObject = JSONObject.parseObject(result);
        return ResponseVo.success(jsonObject);
    }


   /* @ApiOperation("test")
    @RequestMapping(value = "/test", method = RequestMethod.POST)
    public ResponseVo test() {
        for (int i = 3494; i < 3820; i++) {
            DSParamBuilder dsParamBuilder = new DSParamBuilder(101);
            com.tianyi.datacenter.feign.common.vo.ResponseVo responseVo = dataCenterFeignService.retrieveId(dsParamBuilder.build());
            Random random = new Random();
            List per = new ArrayList();
            per.add("打黄油");
            per.add("接着打黄油");
            per.add("继续打黄油");
            per.add("查看座椅");
            per.add("无缺失");
            per.add("无损坏");
            per.add("通过");
            per.add("不通过");
            per.add("哈哈哈哈哈啊哈哈");
            if (responseVo.isSuccess() && responseVo.getMessage() == null) {

                Map data = new HashMap();
                data.put("bid", responseVo.getData().get("rtnData"));
                data.put("videoId", i + "");
                data.put("timeseconds", random.nextInt(35));
                Random random1 = new Random();
                System.out.println(random1.nextInt());
                data.put("perspeech", per.get(random1.nextInt(8)));
                dsParamBuilder.buildData(data);
                com.tianyi.datacenter.feign.common.vo.ResponseVo retrieve = dataCenterFeignService.add(dsParamBuilder.build());
                System.out.println(retrieve.getMessage() + "   " + retrieve.isSuccess());
            }

        }
        return null;
    }*/
}
