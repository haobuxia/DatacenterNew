package com.tianyi.datacenter.inspect.controller;

import com.tianyi.datacenter.common.util.HttpClientUtil;
import com.tianyi.datacenter.common.vo.ResponseVo;
import com.tianyi.datacenter.inspect.service.CheckOrderItemService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

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

    //检查单详情查询
    @RequestMapping("search")
    public ResponseVo search(@RequestBody Map<String, Object> param) {

        if (param == null || param.size() == 0) {
            return ResponseVo.fail("缺少传参");
        }
        return checkOrderItemService.search(param);

    }

    /**
     * 结果查询
     *
     * @param param
     * @return
     */
    //{"condition":{},"dataObjectId":31,"pageInfo":{"page":1,"count":0,"pageSize":100,"total":10}}
    @RequestMapping(value = "resultSearch", method = RequestMethod.POST)
    public ResponseVo resultSearch(@RequestBody Map<String, Object> param) {

        if (param == null || param.size() == 0) {
            return ResponseVo.fail("缺少传参");
        }
        return checkOrderItemService.resultSearch(param);

    }

    @RequestMapping(value = "save", method = RequestMethod.POST)
    public ResponseVo save(@RequestBody List<Map<String, Object>> param) {

        if (param == null || param.size() == 0) {
            return ResponseVo.fail("缺少传参");
        }
        return checkOrderItemService.save(param);

    }

    /**
     * 资料记录
     *
     * @param param
     * @return
     */

    @RequestMapping(value = "details", method = RequestMethod.POST)
    public String details(@RequestBody Map<String, String> param) {

        //{"success":true,"code":"200","message":null,"data":{"videoList":[],"audioList":[],"imageList":[]}}
        Map<String, String> header = new HashMap<>();
        String http = HttpClientUtil.getHttpPostForm("http", 13332, "smart.tygps.com", "helmetmedia/list", param, header);
        return http;
    }

}
