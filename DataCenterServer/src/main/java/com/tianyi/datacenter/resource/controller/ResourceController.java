package com.tianyi.datacenter.resource.controller;

import com.tianyi.datacenter.common.exception.DataCenterException;
import com.tianyi.datacenter.access.controller.DataAccessController;
import com.tianyi.datacenter.resource.service.DataObjectService;
import com.tianyi.datacenter.resource.service.ResourceService;
import com.tianyi.datacenter.storage.vo.PageListVo;
import com.tianyi.datacenter.storage.vo.RequestVo;
import com.tianyi.datacenter.storage.vo.ResponseVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 资源目录
 *
 * @author xiayuan
 * 2018/11/19 16:39
 */
@RestController
@RequestMapping("resource")
public class ResourceController {

    private Logger logger = LoggerFactory.getLogger(DataAccessController.class);
    @Autowired
    private ResourceService resourceService;
    @Autowired
    private DataObjectService dataObjectService;

    /**
     * @param dataObjectId 对象id
     * @param keyword      查询关键字
     * @param pageListVo   当前页数
     * @return ResponseVo json
     * @author xiayuan
     * 2018/11/19 16:39
     */

    @RequestMapping(value = "get",method = RequestMethod.GET)
    public ResponseVo getResources(@RequestParam(required = false) Integer dataObjectId,@RequestParam(required = false) String type,@RequestParam(required = false) String isDic,@RequestParam(required = false) String keyword,@RequestParam(required = false) PageListVo pageListVo) {
        RequestVo<Map> requestVo = resourceService.integrateData(dataObjectId, type,isDic, keyword, pageListVo);
        ResponseVo responseVo = ResponseVo.fail("数据查询失败");
        try {
            responseVo = dataObjectService.list(requestVo);
        } catch (DataCenterException e) {
            logger.error("", e.toString());
        }
        return responseVo;
    }
}
