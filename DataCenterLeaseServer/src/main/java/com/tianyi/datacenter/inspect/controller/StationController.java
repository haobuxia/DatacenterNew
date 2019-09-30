package com.tianyi.datacenter.inspect.controller;

import com.github.liaochong.myexcel.core.DefaultExcelBuilder;
import com.github.liaochong.myexcel.utils.AttachmentExportUtil;
import com.tianyi.datacenter.common.framework.controller.SuperController;
import com.tianyi.datacenter.common.vo.RequestVo2;
import com.tianyi.datacenter.common.vo.ResponseVo;
import com.tianyi.datacenter.feign.service.DataCenterFeignService;
import com.tianyi.datacenter.inspect.dao.StationCheckDao;
import com.tianyi.datacenter.inspect.entity.StationCheck;
import com.tianyi.datacenter.inspect.service.StationService;
import io.swagger.annotations.Api;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import javax.websocket.server.PathParam;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by tianxujin on 2019/5/13 10:56
 */
@Api("工具检查项配置接口")
@RestController
@RequestMapping(value = "inspect/station")
public class StationController extends SuperController {
    @Autowired
    private StationService stationService;
    @Autowired
    private DataCenterFeignService dataCenterFeignService;
    @Autowired
    private StationCheckDao stationCheckDao;

    @RequestMapping("search")
    public ResponseVo search(@RequestBody RequestVo2 requestVo) {

        if (requestVo.getCondition() == null) {
            return ResponseVo.fail("请输入查询条件");
        }
        return stationService.selectByPage(requestVo);

    }

    @RequestMapping("save")

    public ResponseVo hello(@RequestBody Map<String, Object> param) {
        if (param.get("cid") == null) {
            return ResponseVo.success();
        }

        ResponseVo responseVo = stationService.saveOfBest(param);

        return responseVo;
    }

    @RequestMapping(value = "export", method = RequestMethod.GET)
    public void export(HttpServletResponse response, @PathParam("stationName") String stationName,
                       @PathParam("modelName") String modelName,
                       @PathParam("typeName") String typeName
    ) {
        Map<String, Object> conditionMap = new HashMap<>();
        if (stationName != null && stationName != "") {
            conditionMap.put("stationName", stationName);
        }
        if (modelName != null && modelName != "") {
            conditionMap.put("modelName", modelName);
        }
        if (typeName != null && typeName != "") {
            conditionMap.put("typeName", typeName);
        }
        List<Map<String, Object>> mapList = stationCheckDao.searchByPage(conditionMap);

        List<StationCheck> dataList = new ArrayList<>(1000);

        for (Map<String, Object> stringObjectMap : mapList) {
            StationCheck stationCheck = new StationCheck();
            stationCheck.setModelName((String) stringObjectMap.get("modelName"));
            stationCheck.setTypeName((String) stringObjectMap.get("typeName"));
            stationCheck.setStationName((String) stringObjectMap.get("stationName"));
            stationCheck.setChecktypeNum((String) stringObjectMap.get("checktypeNum"));
            stationCheck.setChecktypeName((String) stringObjectMap.get("checktypeName"));
            stationCheck.setCheckOrder((Double) stringObjectMap.get("checkOrder"));

            dataList.add(stationCheck);
        }
        Workbook workbook = DefaultExcelBuilder.of(StationCheck.class).build(dataList);
        AttachmentExportUtil.export(workbook, "工位检查项配置表", response);
    }


}
