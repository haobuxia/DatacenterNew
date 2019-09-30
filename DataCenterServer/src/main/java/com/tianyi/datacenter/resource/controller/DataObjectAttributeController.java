package com.tianyi.datacenter.resource.controller;

import com.alibaba.fastjson.JSONObject;
import com.tianyi.datacenter.common.exception.DataCenterException;
import com.tianyi.datacenter.resource.entity.DataObject;
import com.tianyi.datacenter.resource.entity.DataObjectAttribute;
import com.tianyi.datacenter.resource.service.DataObjectAttributeService;
import com.tianyi.datacenter.resource.service.DataObjectService;
import com.tianyi.datacenter.storage.entity.DdlTypeEnum;
import com.tianyi.datacenter.storage.factory.DataStorageDDLFactory;
import com.tianyi.datacenter.storage.service.DataStorageDDLService;
import com.tianyi.datacenter.storage.vo.PageListVo;
import com.tianyi.datacenter.storage.vo.RequestVo;
import com.tianyi.datacenter.storage.vo.ResponseVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据对象属性接口
 *
 * @author wenxinyan
 * @version 0.1
 */
@RestController
@RequestMapping("data")
@Api("实体属性元数据接口")
public class DataObjectAttributeController {

    Logger logger = LoggerFactory.getLogger(DataObjectAttributeController.class);
    @Autowired
    private DataObjectAttributeService dataObjectAttributeService;
    @Autowired
    private DataObjectService dataObjectService;
    @Autowired
    private DataStorageDDLFactory dataStorageDDLFactory;

    private final String DRIVER = "com.mysql.jdbc.Driver";

    /**
     * 新增数据对象属性接口(单个)
     *
     * @author tianxujin
     */
    @RequestMapping("/object/attributes")
    @Transactional
    public ResponseVo add(@RequestBody JSONObject jsonObject){
        if(jsonObject.get("list") == null || jsonObject.get("objectId") == null){
            return ResponseVo.fail("缺少传参");
        }
        List<Map<String, Object>> requestList = (List<Map<String, Object>>) jsonObject.get("list");
        int resId = (int) jsonObject.get("objectId");
        DataObject dataObject = dataObjectService.getById(resId);
        if(dataObject==null) {
            return ResponseVo.fail("数据对象不存在"+resId);
        }
        List<DataObjectAttribute> attributeList = new ArrayList<>();
        for(Map<String, Object> map : requestList){
            DataObjectAttribute attribute = new DataObjectAttribute();
            if(StringUtils.isEmpty(map.get("id")) || "数据集".equals(dataObject.getType())){
                attribute = this.setAttribute(resId, map.get("columnName")==null?"":map.get("columnName").toString(),
                        map.get("jdbcType")==null?"":map.get("jdbcType").toString(),
                        map.get("length")==null?0:Integer.parseInt(map.get("length").toString()),
                        map.get("name")==null?"":map.get("name").toString(),
                        map.get("description")==null?"":map.get("description").toString(),
                        map.get("type")==null?"":map.get("type").toString(),
                        Integer.parseInt(map.get("dicRes")==null?"-1":map.get("dicRes").toString()),
                        map.get("rule")==null?"":map.get("rule").toString(),
                        map.get("isKey")==null?"":map.get("isKey").toString(),
                        map.get("isNull")==null?"":map.get("isNull").toString(),
                        map.get("isIncrement")==null?"":map.get("isIncrement").toString(),
                        map.get("indexType")==null?"":map.get("indexType").toString(),
                        Integer.parseInt(map.get("dicKey")==null?"-1":map.get("dicKey").toString()),
                        Integer.parseInt(map.get("dicValue")==null?"-1":map.get("dicValue").toString()));
                attributeList.add(attribute);
            }
        }

        Map<String, Object> map = new HashMap<>();
        map.put("resId", resId);
        if("对象".equals(dataObject.getType())){
            String ddlType = "C";
            List<DataObjectAttribute> attributes = dataObjectAttributeService.listNoPage(map);
            if(attributes != null && attributes.size() > 0){
                List<String> columnList = this.getColumnList(attributes);
                List<String> nameList = this.getNameList(attributes);
                for(DataObjectAttribute doa : attributeList){
                    if(columnList.contains(doa.getColumnName())){
                        return ResponseVo.fail("columnName重复");
                    }
                    if(nameList.contains(doa.getName())){
                        return ResponseVo.fail("name重复");
                    }
                }
            }
            try {
                DataStorageDDLService dataStorageDDLService = dataStorageDDLFactory.getDDLService(DdlTypeEnum.column);
                ResponseVo  responseVo = dataStorageDDLService.doServer(dataStorageDDLService.getRequestVo(ddlType, dataObject, attributeList));
                logger.debug("调用DDL是否成功:" + responseVo.isSuccess());
            } catch (DataCenterException e) {
                logger.error(e.toString());
            }
        } else if("数据集".equals(dataObject.getType())){// 先删除再新增
            dataObjectAttributeService.delete(map);
        }
        List<Integer> ids = new ArrayList<>();
        for(DataObjectAttribute doa : attributeList) {
            dataObjectAttributeService.insert(doa);
            ids.add(doa.getId());
        }
        Map<String, Object> result = new HashMap<>();
        result.put("id", ids);
        return ResponseVo.success(result);
    }

    /**
     * 新增数据对象属性接口
     *
     * @author wenxinyan
     */
    @RequestMapping("/object/attributes/addBatch")
    @Transactional
    public ResponseVo addBatch(@RequestBody JSONObject jsonObject){
        if(jsonObject.get("list") == null || jsonObject.get("objectId") == null){
            return ResponseVo.fail("缺少传参");
        }
        List<Map<String, String>> requestList = (List<Map<String, String>>) jsonObject.get("list");
        int resId = (int) jsonObject.get("objectId");
        List<DataObjectAttribute> attributeList = new ArrayList<>();
        for(Map<String, String> map : requestList){
            DataObjectAttribute attribute = new DataObjectAttribute();
            if(StringUtils.isEmpty(map.get("id"))){
                attribute = this.setAttribute(resId, map.get("columnName"), map.get("jdbcType"),
                        Integer.parseInt(map.get("length")), map.get("name"), map.get("description"), map.get("type"),
                        Integer.parseInt(map.get("dicRes")==null?"-1":map.get("dicRes").toString()), map.get("rule"), map.get("isKey"), map.get("isNull"), map.get("isIncrement"), map.get("indexType"),
                        Integer.parseInt(map.get("dicKey")==null?"-1":map.get("dicKey").toString()),
                        Integer.parseInt(map.get("dicValue")==null?"-1":map.get("dicValue").toString()));
            } else {
                attribute = dataObjectAttributeService.getById(Integer.parseInt(map.get("id")));
                attribute.setResId(resId);
            }

            attributeList.add(attribute);
        }

        DataObject dataObject = dataObjectService.getById(resId);
        Map<String, Object> map = new HashMap<>();
        map.put("resId", resId);
        String ddlType = "U";
        List<DataObjectAttribute> attributes = dataObjectAttributeService.listNoPage(map);
        if(attributes.size() == 0 || attributes == null){
            ddlType = "C";
        } else {
            List<String> columnList = this.getColumnList(attributes);
            List<String> nameList = this.getNameList(attributes);
            for(DataObjectAttribute doa : attributeList){
                if(columnList.contains(doa.getColumnName())){
                    return ResponseVo.fail("columnName重复");
                }
                if(nameList.contains(doa.getName())){
                    return ResponseVo.fail("name重复");
                }
            }
        }

        List<Integer> ids = new ArrayList<>();
        for(DataObjectAttribute doa : attributeList){
            dataObjectAttributeService.insert(doa);
            ids.add(doa.getId());
        }

        if("对象".equals(dataObject.getType())){
            try {
                DataStorageDDLService dataStorageDDLService = dataStorageDDLFactory.getDDLService(DdlTypeEnum.column);
                ResponseVo  responseVo = dataStorageDDLService.doServer(dataStorageDDLService.getRequestVo(ddlType, dataObject, attributeList));
                logger.debug("调用DDL是否成功:" + responseVo.isSuccess());
            } catch (DataCenterException e) {
                logger.error(e.toString());
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("id", ids);

        return ResponseVo.success(result);
    }

    /**
     * 删除数据对象属性接口
     *
     * @author wenxinyan
     */
    @RequestMapping("/object/attribute/delete")
    public ResponseVo delete(@RequestBody JSONObject jsonParam){
        if(jsonParam.get("id") == null){
            return ResponseVo.fail("缺少传参");
        }
        Map<String, Object> map = new HashMap<>();
        int id = (int) jsonParam.get("id");
        map.put("id", id);
        DataObjectAttribute dataObjectAttribute = dataObjectAttributeService.getById(id);
        int resId = dataObjectAttribute.getResId();
        DataObject dataObject = dataObjectService.getById(resId);
        if("对象".equals(dataObject.getType())){
            List<DataObjectAttribute> attributeList = new ArrayList<>();
            attributeList.add(dataObjectAttribute);
            try {
                DataStorageDDLService dataStorageDDLService = dataStorageDDLFactory.getDDLService(DdlTypeEnum.column);
                dataStorageDDLService.doServer(dataStorageDDLService.getRequestVo("D", dataObject, attributeList));
            } catch (DataCenterException e) {
                logger.error(e.toString());
            }
        }
        dataObjectAttributeService.delete(map);
        return ResponseVo.success();
    }

    /**
     * 修改数据对象属性接口
     *
     * @author wenxinyan
     */
    @RequestMapping(value="/object/attribute/update", method = RequestMethod.POST)
    public ResponseVo update(@RequestBody DataObjectAttribute dataObjectAttribute){
        DataObject dataObject = dataObjectService.getById(dataObjectAttribute.getResId());
        if("对象".equals(dataObject.getType())){
            List<DataObjectAttribute> attributeList = new ArrayList<>();
            attributeList.add(dataObjectAttribute);
            try {
                DataStorageDDLService dataStorageDDLService = dataStorageDDLFactory.getDDLService(DdlTypeEnum.column);
                dataStorageDDLService.doServer(dataStorageDDLService.getRequestVo("U", dataObject, attributeList));
            } catch (DataCenterException e) {
                logger.error(e.toString());
            }
        }
        dataObjectAttributeService.update(dataObjectAttribute);
        return ResponseVo.success();
    }

    /**
     * 查询数据对象属性接口
     * includeDic boolean 是否包含字典属性字段
     * pageInfo:{page:0,pageSize:12}
     * resId:对象ID
     * @author wenxinyan
     */
    @ApiOperation(value="获取单实体对象的属性列表")
    @RequestMapping(value="/object/attribute/list", method = RequestMethod.POST)
    public ResponseVo list(@RequestBody JSONObject jsonParam){
        if(jsonParam.get("resId") == null){
            return ResponseVo.fail("缺少传参");
        }
        Map pageInfoTmp = (Map) jsonParam.get("pageInfo");
        PageListVo pageInfo = new PageListVo(0,0);
        if(pageInfoTmp!=null && pageInfoTmp.size()>0){
            pageInfo = new PageListVo((int) pageInfoTmp.get("page"), (int) pageInfoTmp.get("pageSize"));
        }

        Map<String, Object> map = new HashMap<>();
        map.put("resId", jsonParam.get("resId"));

        RequestVo<Map> requestVo = new RequestVo<>(map);

        requestVo.setPageInfo(pageInfo);

        ResponseVo responseVo = ResponseVo.fail("查询对象属性失败！");
        Map<String, Object> result = new HashMap<>();
        Boolean includeDic = jsonParam.getBoolean("includeDic");
        if(includeDic!=null && includeDic.booleanValue()){
            result.put("list", dataObjectAttributeService.listAttributesIncludeDic(map));
            responseVo = ResponseVo.success(result);
        } else if(pageInfo.getPage() == 0 && pageInfo.getPageSize() == 0) {
            result.put("list", dataObjectAttributeService.listNoPage(map));
            responseVo = ResponseVo.success(result);
        } else {
            try {
                responseVo = dataObjectAttributeService.list(requestVo);
            } catch (DataCenterException e) {
                logger.error(e.toString());
            }
        }

        return responseVo;
    }

    /**
     * 查询数据对象属性接口
     *
     * @author wenxinyan
     */
    @RequestMapping("/object/attribute/listDwf")
    public ResponseVo listDwf(@RequestBody JSONObject jsonParam){
        if(jsonParam.get("pageInfo") == null || jsonParam.get("resId") == null){
            return ResponseVo.fail("缺少传参");
        }
        /*Map pageInfoTmp = (Map) jsonParam.get("pageInfo");
        PageListVo pageInfo = new PageListVo(0,0);
        if(pageInfoTmp!=null){
            pageInfo = new PageListVo((int) pageInfoTmp.get("page"), (int) pageInfoTmp.get("pageSize"));
        }
        Map<String, Object> map = new HashMap<>();
        map.put("resId", jsonParam.get("resId"));
        RequestVo<Map> requestVo = new RequestVo<>(map);
        requestVo.setPageInfo(pageInfo);*/
        String attributeDwfUrl="meta/class/"+jsonParam.get("resId")+"/attributes";
        ResponseVo responseVo = ResponseVo.fail("查询对象属性失败！");
        Map<String, Object> result = new HashMap<>();
        result.put("list", dataObjectAttributeService.listDwf(attributeDwfUrl));
        responseVo = ResponseVo.success(result);
        return responseVo;
    }

    /**
     * 查询数据对象属性接口
     *
     * @author wenxinyan
     */
    @RequestMapping("/object/attribute/listDb")
    public ResponseVo listDb(@RequestBody JSONObject jsonObject){
        if(jsonObject.get("url") == null || jsonObject.get("user") == null || jsonObject.get("password") == null || jsonObject.get("tableName") == null){
            return ResponseVo.fail("缺少传参");
        }
        String url = (String)jsonObject.get("url");
        String password = (String)jsonObject.get("password");
        String user = (String)jsonObject.get("user");
        String tableName = (String)jsonObject.get("tableName");

        ResponseVo responseVo = ResponseVo.fail("查询对象属性失败！");
        Map<String, Object> result = new HashMap<>();
        result.put("list", dataObjectAttributeService.listDb(DRIVER,url,user,password,tableName));
        responseVo = ResponseVo.success(result);
        return responseVo;
    }

    private List<String> getColumnList(List<DataObjectAttribute> attributeList){
        List<String> columnList = new ArrayList<>();

        for(DataObjectAttribute doa : attributeList){
            columnList.add(doa.getColumnName());
        }

        return columnList;
    }

    private List<String> getNameList(List<DataObjectAttribute> attributeList){
        List<String> nameList = new ArrayList<>();

        for(DataObjectAttribute doa : attributeList){
            nameList.add(doa.getName());
        }

        return nameList;
    }

    private DataObjectAttribute setAttribute(int resId, String columnName, String jdbcType, int length, String name,
                                             String description, String type, int dicRes, String rule, String isKey,
                                             String isNull, String isIncrement, String indexType, int dicKey, int dicValue){
        DataObjectAttribute dataObjectAttribute = new DataObjectAttribute();
        dataObjectAttribute.setResId(resId);
        dataObjectAttribute.setColumnName(columnName);
        dataObjectAttribute.setJdbcType(jdbcType);
        dataObjectAttribute.setLength(length);
        dataObjectAttribute.setName(name);
        dataObjectAttribute.setDescription(description);
        dataObjectAttribute.setType(type);
        dataObjectAttribute.setDicRes(dicRes);
        dataObjectAttribute.setRule(rule);
        dataObjectAttribute.setIsKey(isKey);
        dataObjectAttribute.setIsNull(isNull);
        dataObjectAttribute.setIsIncrement(isIncrement);
        dataObjectAttribute.setIndexType(indexType);
        dataObjectAttribute.setDicKey(dicKey);
        dataObjectAttribute.setDicValue(dicValue);

        return dataObjectAttribute;
    }
}
