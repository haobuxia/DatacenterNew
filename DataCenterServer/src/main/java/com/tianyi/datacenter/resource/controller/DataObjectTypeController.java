package com.tianyi.datacenter.resource.controller;

import com.alibaba.fastjson.JSONObject;
import com.tianyi.datacenter.resource.entity.DataObjectType;
import com.tianyi.datacenter.resource.service.DataObjectService;
import com.tianyi.datacenter.resource.service.DataObjectTypeService;
import com.tianyi.datacenter.resource.util.TreeUtil;
import com.tianyi.datacenter.storage.vo.ResponseVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 数据对象分类定义接口
 *
 * @author tianxujin
 * @version 0.1
 */
@RestController
@RequestMapping("datatype")
public class DataObjectTypeController {

    Logger logger = LoggerFactory.getLogger(DataObjectTypeController.class);

    @Autowired
    private DataObjectTypeService dataObjectTypeService;
    @Autowired
    private DataObjectService dataObjectService;
    /**
     * 查询数据对象接口
     *
     * @author tianxujin
     */
    @RequestMapping("list")
    public ResponseVo list(@RequestBody JSONObject jsonParam){
        if(jsonParam.get("pid") == null){
            return ResponseVo.fail("缺少传参");
        }
        Map<String, Object> map = new HashMap<>();
        ResponseVo responseVo = ResponseVo.fail("查询对象失败！");
        Map<String, Object> result = new HashMap<>();
        //构建左边的级别目录，就是构建child里面的东西
        result.put("list", TreeUtil.buildByRecursive(dataObjectTypeService.listNoPage(map), jsonParam.getInteger("pid")));
        responseVo = ResponseVo.success(result);
        return responseVo;
    }

    /**
     * 新增数据对象接口
     *
     * @author tianxujin
     */
    @RequestMapping("add")
    public ResponseVo add(@RequestBody DataObjectType dataObjectType){
        dataObjectTypeService.insert(dataObjectType);
        Map<String, Object> result = new HashMap<>();
        result.put("id", dataObjectType.getId());
        return ResponseVo.success(result);
    }

    /**
     * 删除数据对象接口
     *
     * @author tianxujin
     */
    @RequestMapping("delete")
    public ResponseVo delete(@RequestBody JSONObject jsonParam){
        if(jsonParam.get("id") == null){
            return ResponseVo.fail("缺少参数");
        }
        int id = (int) jsonParam.get("id");
        Map<String, Object> check = new HashMap<>();
        check.put("typeId", id);
        if(dataObjectService.listNoPage(check).size() > 0){
            return ResponseVo.fail("分类下存在实体，不能删除");
        }
        dataObjectTypeService.delete(id);
        return ResponseVo.success();
    }

    /**
     * 修改数据对象接口
     *
     * @author tianxujin
     */
    @RequestMapping("update")
    public ResponseVo update(@RequestBody DataObjectType dataObject){

        dataObjectTypeService.update(dataObject);

        return ResponseVo.success();
    }

}
