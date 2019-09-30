package com.tianyi.datacenter.access.controller;

import com.alibaba.fastjson.JSONObject;
import com.tianyi.datacenter.access.service.DataAccessService;
import com.tianyi.datacenter.storage.vo.ResponseVo;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 数据访问
 *
 * @author wenxinyan
 * @version 0.1
 */
@RestController
@RequestMapping("data")
@Api("数据访问接口")
public class DataAccessController {

    Logger logger = LoggerFactory.getLogger(DataAccessController.class);
    @Autowired
    private DataAccessService dataAccessService;

    @RequestMapping(value="hello", method = RequestMethod.GET)
    public String hello(@RequestParam String name) {
        String abc = name;
        return "调用数据中心hello成功:"+name;
    }

    /**
     * 新增数据接口
     *
     * @author wenxinyan
     */
    @ApiOperation(value="单实体对象添加")
    @RequestMapping(value="add", method = RequestMethod.POST)
    public ResponseVo add(@RequestBody JSONObject jsonObject) {
        ResponseVo responseVo = null;
        try {
            responseVo = dataAccessService.addData(jsonObject);
        } catch (Exception e) {
            //失败返回错误的信息
            responseVo = ResponseVo.fail(e.getMessage());
            //打印日志
            logger.error(e.toString());
        }
        return responseVo;
    }

    /**
     * 删除数据接口
     *
     * @author wenxinyan
     */
    @ApiOperation(value="单实体对象删除")
    @RequestMapping(value="delete", method = RequestMethod.POST)
    public ResponseVo delete(@RequestBody JSONObject jsonObject) {
        ResponseVo responseVo = null;
        try {
            responseVo = dataAccessService.deleteData(jsonObject);
        } catch (Exception e) {
            responseVo = ResponseVo.fail(e.getMessage());
            logger.error(e.toString());
        }
        return responseVo;
    }

    /**
     * 修改数据接口
     *
     * @author wenxinyan
     */
    @ApiOperation(value="单实体对象更新")
    @RequestMapping(value="update", method = RequestMethod.POST)
    public ResponseVo update(@RequestBody JSONObject jsonObject) {
        ResponseVo responseVo = null;
        try {
            responseVo = dataAccessService.updateData(jsonObject);
        } catch (Exception e) {
            responseVo = ResponseVo.fail(e.getMessage());
            logger.error(e.toString());
        }
        return responseVo;
    }

    /**
     * 查询数据接口
     *
     * @author wenxinyan
     */
    @ApiOperation(value="数据查询", notes="根据jsonObject对象查询数据")
    @RequestMapping(value="retrieve", method = RequestMethod.POST)
    public ResponseVo retrieve(@RequestBody JSONObject jsonObject) {
        ResponseVo responseVo = null;
        try {
            responseVo = dataAccessService.queryData(jsonObject);
        } catch (Exception e) {
            responseVo = ResponseVo.fail(e.getMessage());
            logger.error(e.toString());
        }
        return responseVo;
    }

    /**
     * 查询关联字典数据接口
     * @reteurn key是字典对象id，value是字典对象数据list的结果集
     * @author tianxujin
     */
    @ApiOperation(value="数据关联字典查询", notes="根据jsonObject对象查询数据")
    @RequestMapping(value="retrievedic", method = RequestMethod.POST)
    public ResponseVo retrieveDic(@RequestBody JSONObject jsonObject) {
        ResponseVo responseVo = null;
        try {
            responseVo = dataAccessService.queryDataDic(jsonObject);
        } catch (Exception e) {
            responseVo = ResponseVo.fail(e.getMessage());
            logger.error(e.toString());
        }
        return responseVo;
    }

    /**
     * 数据查询返回树结构
     * @reteurn 数据list的结果集
     * @author tianxujin
     */
    @ApiOperation(value="数据查询返回树结构", notes="根据jsonObject对象查询数据")
    @RequestMapping(value="retrievetree", method = RequestMethod.POST)
    public ResponseVo retrieveTree(@RequestBody JSONObject jsonObject) {
        ResponseVo responseVo = null;
        try {
            responseVo = dataAccessService.queryDataTree(jsonObject);
        } catch (Exception e) {
            responseVo = ResponseVo.fail(e.getMessage());
            logger.error(e.toString());
        }
        return responseVo;
    }

    /**
     * 查询数据接口
     *
     * @author tianxujin
     */
    @ApiOperation(value="获取数据编码", notes="根据jsonObject对象查询数据")
    @RequestMapping(value="retrieveid", method = RequestMethod.POST)
    public ResponseVo retrieveId(@RequestBody JSONObject jsonObject) {
        ResponseVo responseVo = null;
        try {
            responseVo = dataAccessService.retrieveMaxId(jsonObject);
        } catch (Exception e) {
            responseVo = ResponseVo.fail(e.getMessage());
            logger.error(e.toString());
        }
        return responseVo;
    }

    /**
     * 查询数据A是否被引用并返回引用A的对象数据
     * @reteurn dataObjectId是A对象objectId，dataId是A对象数据ID
     * @author tianxujin
     */
    @ApiOperation(value="被引用校验查询", notes="根据jsonObject对象查询数据")
    @RequestMapping(value="checkreference", method = RequestMethod.POST)
    public ResponseVo checkReference(@RequestBody JSONObject jsonObject) {
        ResponseVo responseVo = null;
        try {
            responseVo = dataAccessService.checkReference(jsonObject);
        } catch (Exception e) {
            responseVo = ResponseVo.fail(e.getMessage());
            logger.error(e.toString());
        }
        return responseVo;
    }
}
