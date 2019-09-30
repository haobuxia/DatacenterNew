package com.tianyi.datacenter.inspect.controller;

import com.tianyi.datacenter.common.util.HttpClientUtil;
import com.tianyi.datacenter.common.vo.ResponseVo;
import com.tianyi.datacenter.config.TianYiConfig;
import com.tianyi.datacenter.inspect.service.CheckOrderItemService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author liulele ${Date}
 *
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
    private TianYiConfig tianYiConfig;

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

    @ApiOperation("资料记录")
    @RequestMapping(value = "details", method = RequestMethod.POST)
    public String details(@RequestBody Map<String, String> param) {
        //{"success":true,"code":"200","message":null,"data":{"videoList":[],"audioList":[],"imageList":[]}}
        Map<String, String> header = new HashMap<>();
        String http = HttpClientUtil.getHttpPostForm(tianYiConfig.getTianYiIntesrvUrl() + "/helmetmedia/list", param, header);
        return http;
    }


}
