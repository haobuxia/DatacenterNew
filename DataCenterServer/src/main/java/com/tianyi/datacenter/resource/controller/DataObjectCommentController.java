package com.tianyi.datacenter.resource.controller;

import com.alibaba.fastjson.JSONObject;
import com.tianyi.datacenter.resource.entity.DataObjectComment;
import com.tianyi.datacenter.resource.service.DataObjectCommentService;
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
 * 数据集结果集注解定义接口
 *
 * @author tianxujin
 * @version 0.1
 */
@RestController
@RequestMapping("datacomment")
public class DataObjectCommentController {

    Logger logger = LoggerFactory.getLogger(DataObjectCommentController.class);

    @Autowired
    private DataObjectCommentService dataObjectCommentService;
    /**
     * 查询数据集注解列表（已sql定义为基准，匹配注解维护结果）
     *
     * @author tianxujin
     */
    @RequestMapping("listcolumns")
    public ResponseVo listColumns(@RequestBody JSONObject jsonParam){
        if(jsonParam.get("resId") == null){
            return ResponseVo.fail("缺少传参");
        }
        ResponseVo responseVo = dataObjectCommentService.listDataSetColumns(jsonParam);
        return responseVo;
    }

    /**
     * 查询数据集注解列表
     * @param jsonParam
     * @return
     */
    @RequestMapping("list")
    public ResponseVo list(@RequestBody JSONObject jsonParam){
        if(jsonParam.get("resId") == null){
            return ResponseVo.fail("缺少传参");
        }
        Map<String, Object> map = new HashMap<>();
        map.put("resId", jsonParam.get("resId"));
        ResponseVo responseVo = ResponseVo.fail("查询对象失败！");
        Map<String, Object> result = new HashMap<>();
        result.put("list", dataObjectCommentService.listNoPage(map));
        responseVo = ResponseVo.success(result);
        return responseVo;
    }

    /**
     * 新增数据集注解接口
     *
     * @author tianxujin
     */
    @RequestMapping("add")
    public ResponseVo add(@RequestBody DataObjectComment dataObjectComment){
        dataObjectCommentService.insert(dataObjectComment);
        Map<String, Object> result = new HashMap<>();
        result.put("id", dataObjectComment.getId());
        return ResponseVo.success(result);
    }

    /**
     * 删除数据集注解接口
     *
     * @author tianxujin
     */
    @RequestMapping("delete")
    public ResponseVo delete(@RequestBody JSONObject jsonParam){
        if(jsonParam.get("id") == null){
            return ResponseVo.fail("缺少参数");
        }
        int id = (int) jsonParam.get("id");
        dataObjectCommentService.delete(id);
        return ResponseVo.success();
    }

    /**
     * 修改数据集注解接口
     *
     * @author tianxujin
     */
    @RequestMapping("update")
    public ResponseVo update(@RequestBody DataObjectComment dataObject){

        dataObjectCommentService.update(dataObject);

        return ResponseVo.success();
    }

    /**
     * 批量修改数据集注解接口
     *
     * @author tianxujin
     */
    @RequestMapping("updatebatch")
    public ResponseVo updateBatch(@RequestBody JSONObject jsonObject){
        if(jsonObject.get("list") == null || jsonObject.get("resId") == null){
            return ResponseVo.fail("缺少传参");
        }
        dataObjectCommentService.updates(jsonObject);
        return ResponseVo.success();
    }

}
