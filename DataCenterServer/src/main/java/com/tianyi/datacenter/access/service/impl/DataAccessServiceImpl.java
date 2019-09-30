package com.tianyi.datacenter.access.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.tianyi.datacenter.access.service.DataAccessService;
import com.tianyi.datacenter.access.util.DSParamBuilder;
import com.tianyi.datacenter.access.util.DSParamDsBuilder;
import com.tianyi.datacenter.common.exception.DataCenterException;
import com.tianyi.datacenter.resource.entity.DataObject;
import com.tianyi.datacenter.resource.entity.DataObjectAttribute;
import com.tianyi.datacenter.resource.service.DataObjectAttributeService;
import com.tianyi.datacenter.resource.service.DataObjectService;
import com.tianyi.datacenter.resource.util.JsonTreeUtil;
import com.tianyi.datacenter.storage.service.DataStorageDMLService;
import com.tianyi.datacenter.storage.util.DBUtil;
import com.tianyi.datacenter.storage.vo.DataStorageDMLVo;
import com.tianyi.datacenter.storage.vo.PageListVo;
import com.tianyi.datacenter.storage.vo.RequestVo;
import com.tianyi.datacenter.storage.vo.ResponseVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;

/**
 * 数据访问
 *
 * @author wenxinyan
 * @version 0.1
 */
@Service
public class DataAccessServiceImpl implements DataAccessService {

    Logger logger = LoggerFactory.getLogger(DataAccessServiceImpl.class);
    @Autowired
    private DataStorageDMLService dataStorageDMLService;
    @Autowired
    private DataObjectService dataObjectService;
    @Autowired
    private DataObjectAttributeService dataObjectAttributeService;
    @Autowired
    private DBUtil database;
    /**
     * 将请求数据封装成dmlvo
     * @author wenxinyan
     * 2018/11/27 10:07
     * @param  dataObject 数据对象id
     * @param page 当前页码
     * @param pageSize 每页条数
     * @param conditionAttributes 条件属性列表
     * @param updateAttributes 更新、新增属性列表
     * @param jsonObject
     * @return dml请求对象
    */
    @Override
    public RequestVo<DataStorageDMLVo> integrateData(DataObject dataObject, String operaType, int page, int pageSize,
                                                     List<DataObjectAttribute> conditionAttributes,
                                                     List<DataObjectAttribute> updateAttributes, JSONObject jsonObject) {

        DataStorageDMLVo dmlVo = new DataStorageDMLVo();
        //表信息
        LocalDateTime now = LocalDateTime.now();
        if("C".equals(operaType)){// 新增数据
            dataObject.setPltCreator(-1);
            dataObject.setPltCreatetime(now);
            dataObject.setPltLastmodifier(-1);
            dataObject.setPltLastmodifytime(now);
        } else if("U".equals(operaType)) {// 修改数据
            dataObject.setPltLastmodifier(-1);
            dataObject.setPltLastmodifytime(now);
        }
        dmlVo.setDataObject(dataObject);
        //dml操作类型，增删改查
        dmlVo.setDmlType(operaType);
        //更新字段信息
        dmlVo.setAttributes(updateAttributes);
        //条件字段信息
        dmlVo.setCondition(conditionAttributes);
        //遍历组装更新信息
        RequestVo<DataStorageDMLVo> requestVo = new RequestVo<>(dmlVo);

        PageListVo pageListVo = new PageListVo(page, pageSize);
        requestVo.setPageInfo(pageListVo);
        if(jsonObject.get("orderBy") != null && jsonObject.get("orderBy").toString().trim().length()>0){
            requestVo.setOrderBy(jsonObject.get("orderBy").toString());
        }
        return requestVo;
    }

    /**
     * 新增数据
     * @param jsonObject
     * @return
     * @throws Exception
     */
    @Override
    @Transactional
    public ResponseVo addData(JSONObject jsonObject) throws Exception{
        //首先判断是否缺少参数
        if(jsonObject.get("dataObjectId") == null || jsonObject.get("data") == null){
            return ResponseVo.fail("缺少传参");
        }
        ResponseVo responseVo = ResponseVo.fail("新增数据失败!");
        int dataObjectId = (int) jsonObject.get("dataObjectId");
        Map<String, Object> data = (Map<String, Object>) jsonObject.get("data");
        //获取新增字段属性信息
        List<DataObjectAttribute> insertAttributes = getAllAttributes(dataObjectId, data);
        //组装dml请求vo
        RequestVo requestVo = this.integrateData(dataObjectService.getById(dataObjectId), "C", 0, 0,
                null, insertAttributes, jsonObject);
        responseVo = dataStorageDMLService.doServer(requestVo);
        return responseVo;
    }

    @Override
    @Transactional
    public ResponseVo deleteData(JSONObject jsonObject) throws Exception {
        if(jsonObject.get("dataObjectId") == null || jsonObject.get("condition") == null){
            return ResponseVo.fail("缺少传参");
        }
        int dataObjectId = (int) jsonObject.get("dataObjectId");
        List<Map<String, Object>> condition = (List<Map<String, Object>>) jsonObject.get("condition");
        //获取条件字段属性信息
        List<DataObjectAttribute> conditionAttributes = getAllAttributes(dataObjectId, condition);
        //组装dml请求vo
        RequestVo requestVo = this.integrateData(dataObjectService.getById(dataObjectId), "D", 0, 0,
                conditionAttributes, null, jsonObject);
        ResponseVo responseVo = ResponseVo.fail("删除数据失败！");
        responseVo = dataStorageDMLService.doServer(requestVo);
        return responseVo;
    }

    @Override
    @Transactional
    public ResponseVo updateData(JSONObject jsonObject) throws Exception {
        if(jsonObject.get("dataObjectId") == null || jsonObject.get("data") == null || jsonObject.get("condition") == null){
            return ResponseVo.fail("缺少传参");
        }
        int dataObjectId = (int) jsonObject.get("dataObjectId");
        Map<String, Object> data = (Map<String, Object>) jsonObject.get("data");
        List<Map<String, Object>> condition = (List<Map<String, Object>>) jsonObject.get("condition");
        //获取更新字段属性信息
        List<DataObjectAttribute> updateAttributes = getAllAttributes(dataObjectId, data);
        //获取条件字段属性信息
        List<DataObjectAttribute> conditionAttributes = getAllAttributes(dataObjectId, condition);
        //组装dml请求vo
        RequestVo requestVo = this.integrateData(dataObjectService.getById(dataObjectId), "U", 0, 0,
                conditionAttributes, updateAttributes, jsonObject);

        ResponseVo responseVo = ResponseVo.fail("修改数据失败！");
        responseVo = dataStorageDMLService.doServer(requestVo);
        return responseVo;
    }

    @Override
    public ResponseVo queryData(JSONObject jsonObject) throws Exception {
        if(jsonObject.get("dataObjectId") == null || jsonObject.get("pageInfo") == null){
            return ResponseVo.fail("缺少传参");
        }
        int dataObjectId = (int) jsonObject.get("dataObjectId");
        if(dataObjectId == 0){
            Map<String, Object> map = new HashMap<>();
            map.put("checkname", jsonObject.get("name"));
            List<DataObject> dataObjects = dataObjectService.listNoPage(map);
            dataObjectId = dataObjects.get(0).getId();
        }
        DataObject dataObject = dataObjectService.getById(dataObjectId);
        if(dataObject == null) {
            return ResponseVo.fail("查询的对象不存在,ID="+dataObjectId);
        }
        ResponseVo responseVo = ResponseVo.fail("数据不存在");
        if("对象".equals(dataObject.getType())) {
            responseVo = queryObjectData(jsonObject, dataObjectId);
        } else if("数据集".equals(dataObject.getType())) {
            responseVo = queryListData(jsonObject, dataObjectId);
        }
        return responseVo;
    }

    /**
     * condition代表的查询参数map，key是sql定义的参数，value是参数值
     * @param jsonObject
     * @param dataObjectId
     * @return
     * @throws DataCenterException
     */
    private ResponseVo queryListData(JSONObject jsonObject, int dataObjectId) throws DataCenterException {
        Map<String, Object> condition = (Map<String, Object>) jsonObject.get("condition");
        Map pageInfoTmp = (Map) jsonObject.get("pageInfo");
        PageListVo pageInfo = new PageListVo((int)pageInfoTmp.get("page"),(int)pageInfoTmp.get("pageSize"));
        //获取条件字段属性信息
        List<DataObjectAttribute> conditionAttributes = null;
        if(condition!=null){
            conditionAttributes = getListAttributes(dataObjectId, condition);
        }
        List<Map<String, Object>> departRangeList = (List<Map<String, Object>>)jsonObject.get("departRange");
        // 过滤数据范围
        if(departRangeList != null && departRangeList.size() > 0) {
            setDepartRangeAttribute(conditionAttributes, dataObjectId, departRangeList);
        }
        //组装dml请求vo
        RequestVo requestVo = this.integrateData(dataObjectService.getById(dataObjectId), "R", pageInfo.getPage(), pageInfo.getPageSize(),
                conditionAttributes, null, jsonObject);
        ResponseVo responseVo = ResponseVo.fail("查询数据失败！");
        responseVo = dataStorageDMLService.doServer(requestVo);
        return responseVo;

    }

    private void setDepartRangeAttribute(List<DataObjectAttribute> conditionAttributes, int dataObjectId, List<Map<String, Object>> departRangeList) {
        //获取所有字段属性
        Map tmpInfo = new HashMap();
        tmpInfo.put("resId", dataObjectId);
        List<DataObjectAttribute> allAttributes = dataObjectAttributeService.listNoPage(tmpInfo);
        for (DataObjectAttribute tmpAttribute : allAttributes) {
            if (tmpAttribute.getDescription().indexOf("#{departRange}") != -1 && departRangeList != null && departRangeList.size() > 0) {
                String departRangeSql = "select distinct did from k_dept where 1=1 ";
                for (Map<String, Object> departRange : departRangeList) {
                    departRangeSql = departRangeSql + " and fullPath like '" + departRange.get("fullPath").toString() + "%'";
                }
                tmpAttribute.setDescription(tmpAttribute.getDescription().replaceAll("#\\{\\s*departRange\\s*\\}", departRangeSql));
                if(conditionAttributes==null) {
                    conditionAttributes = new ArrayList<DataObjectAttribute>();
                }
                conditionAttributes.add(tmpAttribute);
            }
        }
    }

    private ResponseVo queryObjectData(JSONObject jsonObject, int dataObjectId) throws DataCenterException {
        List<Map<String, Object>> condition = (List<Map<String, Object>>) jsonObject.get("condition");
        Map pageInfoTmp = (Map) jsonObject.get("pageInfo");
        PageListVo pageInfo = new PageListVo((int)pageInfoTmp.get("page"),(int)pageInfoTmp.get("pageSize"));
        //获取条件字段属性信息
        List<DataObjectAttribute> conditionAttributes = null;
        if(condition!=null){
            conditionAttributes = getAllAttributes(dataObjectId, condition);
        }
        //获取结果集字段、字典关联信息
        List<DataObjectAttribute> selectAttributes = getSelectAttributes(dataObjectId);
        //组装dml请求vo
        RequestVo requestVo = this.integrateData(dataObjectService.getById(dataObjectId), "R", pageInfo.getPage(), pageInfo.getPageSize(),
                conditionAttributes, selectAttributes, jsonObject);

        ResponseVo responseVo = ResponseVo.fail("查询数据失败！");
        responseVo = dataStorageDMLService.doServer(requestVo);
        return responseVo;
    }

    @Override
    public ResponseVo queryDataDic(JSONObject jsonObject) throws Exception {
        if(jsonObject.get("dataObjectId") == null || jsonObject.get("pageInfo") == null){
            return ResponseVo.fail("缺少传参");
        }
        int dataObjectId = (int) jsonObject.get("dataObjectId");
        if(dataObjectId == 0){
            Map<String, Object> map = new HashMap<>();
            map.put("checkname", jsonObject.get("name"));
            List<DataObject> dataObjects = dataObjectService.listNoPage(map);
            dataObjectId = dataObjects.get(0).getId();
        }
        List<Map<String, Object>> condition = (List<Map<String, Object>>) jsonObject.get("condition");
        Map pageInfoTmp = (Map) jsonObject.get("pageInfo");
        PageListVo pageInfo = new PageListVo((int)pageInfoTmp.get("page"),(int)pageInfoTmp.get("pageSize"));
        //获取条件字段属性信息
        List<DataObjectAttribute> conditionAttributes = null;
        if(condition!=null){
            conditionAttributes = getAllAttributes(dataObjectId, condition);
        }
        //获取关联字典列表信息
        Map<String, Object> map = new HashMap<>();
        map.put("resId", dataObjectId);
        List<Integer> attrDicIds = listAttributeDicIds(map);
        ResponseVo responseVo = ResponseVo.fail("查询数据失败！");
        Map<Integer, Object> result = new HashMap<>();
        for(int i=0;i<attrDicIds.size();i++) {
            //组装dml请求vo
            RequestVo requestVo = this.integrateData(dataObjectService.getById(attrDicIds.get(i)), "R", pageInfo.getPage(), pageInfo.getPageSize(),
                    conditionAttributes, null, jsonObject);
            ResponseVo responseVoTmp = dataStorageDMLService.doServer(requestVo);
            result.put(attrDicIds.get(i),responseVoTmp.getData().get("rtnData"));
        }
        responseVo = ResponseVo.success(result);
        return responseVo;
    }

    @Override
    public ResponseVo queryDataTree(JSONObject jsonObject) throws Exception {
        if(jsonObject.get("dataObjectId") == null || jsonObject.get("pageInfo") == null || jsonObject.get("idKey") == null || jsonObject.get("pidKey") == null){
            return ResponseVo.fail("缺少传参");
        }
        ResponseVo responseVo = this.queryData(jsonObject);
        if(responseVo.isSuccess()) {
            List<Map<String, Object>> list = (List<Map<String, Object>>) responseVo.getData().get("rtnData");
            String root = JsonTreeUtil.findRootId(list, jsonObject.getString("idKey"), jsonObject.getString("pidKey"));
            List<Map<String, Object>> result = JsonTreeUtil.buildByRecursive(list, root,jsonObject.getString("idKey"),jsonObject.getString("pidKey"));
            Map<String, List<Map<String, Object>>> map = new HashMap<>();
            map.put("rtnData", result);
            return ResponseVo.success(map);
        }
        return responseVo;
    }

    @Override
    public ResponseVo retrieveMaxId(JSONObject jsonObject) throws Exception {
        if(jsonObject.get("dataObjectId") == null){
            return ResponseVo.fail("缺少传参");
        }
        String code = "";
        String sql = "select max(currentNo) as maxCurrentNo from data_center_coderule where resId="+jsonObject.getInteger("dataObjectId");
        List<Map<String, Object>> list = database.executeQuery(sql);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd") ; //使用了默认的格式创建了一个日期格式化对象。
        Date date = new Date();
        String time = dateFormat.format(date);
        String orderNo = "00001";
        if(list.size()>0 && list.get(0).get("maxCurrentNo") != null) {
            int maxCurrentNo = (int)list.get(0).get("maxCurrentNo");
            String updateSql = "update data_center_coderule set currentNo="+(maxCurrentNo+1) + " where resId="+jsonObject.getInteger("dataObjectId");
            database.executeDML("U", updateSql);
            orderNo = String.format("%0"+5+"d", ++maxCurrentNo);
            code = time + orderNo;
        } else {
            String insertSql = "insert into data_center_coderule(resid,currentNo) values("+jsonObject.getInteger("dataObjectId")+",1)";
            database.executeDML("C", insertSql);
            code = time + orderNo;
        }
        Map<String, String> map = new HashMap<>();
        map.put("rtnData", code);
        return ResponseVo.success(map);
    }

    @Override
    public ResponseVo checkReference(JSONObject jsonObject) throws Exception {
        if(jsonObject.get("dataObjectId") == null || jsonObject.get("dataId") == null){
            return ResponseVo.fail("缺少传参");
        }
        //1、查询引用dataObjectId的所有对象
        DSParamDsBuilder dsParamDsBuilder = new DSParamDsBuilder(51);
        dsParamDsBuilder.buildCondition("dicRes", jsonObject.get("dataObjectId"));
        ResponseVo responseVo = this.queryData(dsParamDsBuilder.build());
        Map<String, Object> map = new HashMap<>();
        map.put("isRefer", "0"); // 0没被引用1被引用
        map.put("referObject", "");
        map.put("referData", "");
        if(responseVo.isSuccess()) {
            List<Map<String, Object>> list = (List<Map<String, Object>>) responseVo.getData().get("rtnData");
            if (list == null || list.size() == 0) {// 没有对象引用
                return ResponseVo.success(map);
            } else {
                //2、遍历引用对象查询是否引用了dataId
                for(Map<String, Object> referData : list) {
                    DSParamBuilder referParamBuilder = new DSParamBuilder(Integer.parseInt(referData.get("id").toString()));
                    referParamBuilder.buildCondition(referData.get("cname").toString(),"equals",jsonObject.get("dataId"));
                    ResponseVo referVo = this.queryData(referParamBuilder.build());
                    if(referVo.isSuccess()) {
                        List<Map<String, Object>> referList = (List<Map<String, Object>>) referVo.getData().get("rtnData");
                        if(referList!=null && referList.size()>0) {
                            //3、引用了就返回引用数据对象的ID,NAME以及数据
                            map.put("isRefer", "1"); // 0没被引用1被引用
                            map.put("referObject", referData);
                            map.put("referData", referList);
                            break;
                        }
                    } else {
                        return referVo;
                    }
                }
                return ResponseVo.success(map);
            }
        } else {
            return responseVo;
        }
    }

    private List<Integer> listAttributeDicIds(Map<String, Object> param) {
        List<DataObjectAttribute> allAttributes = dataObjectAttributeService.listNoPage(param);
        List<Integer> resultAttribute = new ArrayList<>();
        if(allAttributes.size()>0){
            Map<String, Object> attrParam = new HashMap<>();
            for (DataObjectAttribute tmpAttribute : allAttributes) {
                if(tmpAttribute.getDicRes() != -1) { //引用字典的对象处理
                    resultAttribute.add(tmpAttribute.getDicRes());
                }
            }
        }
        return resultAttribute;
    }
    private List<DataObjectAttribute> getSelectAttributes(int dataObjectId) {
        //获取所有字段属性
        Map tmpInfo = new HashMap();
        tmpInfo.put("resId", dataObjectId);
        List<DataObjectAttribute> allAttributes = dataObjectAttributeService.listNoPage(tmpInfo);

        for (DataObjectAttribute tmpAttribute : allAttributes) {
            if(tmpAttribute.getDicRes() != -1){ //引用字典的对象处理
                DataObject dicObject = dataObjectService.getById(tmpAttribute.getDicRes());
                DataObjectAttribute dicKey = dataObjectAttributeService.getById(tmpAttribute.getDicKey());
                DataObjectAttribute dicValue = dataObjectAttributeService.getById(tmpAttribute.getDicValue());

                tmpAttribute.setDicResColumnName(dicObject.getDefined());
                tmpAttribute.setDicKeyColumnName(dicKey.getColumnName());
                tmpAttribute.setDicValueColumnName(dicValue.getColumnName());
                tmpAttribute.setDicResName(dicObject.getName());
                tmpAttribute.setDicKeyName(dicKey.getName());
                tmpAttribute.setDicValueName(dicValue.getName());
            }
        }
        return allAttributes;
    }

    /**
     * 获取数据对象属性交集
     * @author zhouwei
     * 2018/11/27 09:39
     * @param dataObjectId 数据对象ID
     * @param columnInfo 请求的字段信息
     * @return 数据对象所有属性与传入属性的交集
     */
    private List<DataObjectAttribute> getAllAttributes(int dataObjectId, List<Map<String, Object>> columnInfo) {

        //获取所有字段属性
        Map tmpInfo = new HashMap();
        tmpInfo.put("resId", dataObjectId);
        List<DataObjectAttribute> allAttributes = dataObjectAttributeService.listNoPage(tmpInfo);

        //初始化送往模板的字段属性
        List<DataObjectAttribute> conditionInfo = new ArrayList<>();
        //与传入的字段做合并
        for (Map<String, Object> attr : columnInfo) {
            for (DataObjectAttribute tmpAttribute : allAttributes) {
                if (tmpAttribute.getName().equals(attr.get("key").toString())) {
                    //匹配到相同的属性
                    //设置条件
                    tmpAttribute.setOper((String) attr.get("condition"));
                    //设置值
                    if("integer".equals(tmpAttribute.getJdbcType())){
                        tmpAttribute.setValue((int) attr.get("value") + "");
                    } else if("double".equals(tmpAttribute.getJdbcType())){
                        tmpAttribute.setValue(Double.valueOf(attr.get("value")  + "").toString());
                    } else if("varchar".equals(tmpAttribute.getJdbcType())){
                        tmpAttribute.setValue((String) attr.get("value"));
                    } else if("datetime".equals(tmpAttribute.getJdbcType())){
                        tmpAttribute.setValue((String) attr.get("value"));
                    }
                    conditionInfo.add(tmpAttribute);
                }
            }
        }
        return conditionInfo;
    }

    /**
     * 获取数据集属性交集
     * @author tianxujin
     * 2019/04/09 09:39
     * @param dataObjectId 数据对象ID
     * @param columnInfo 请求的字段信息
     * @return 数据集所有属性与传入属性的交集
     */
    private List<DataObjectAttribute> getListAttributes(int dataObjectId, Map<String, Object> columnInfo) {

        //获取所有字段属性
        Map tmpInfo = new HashMap();
        tmpInfo.put("resId", dataObjectId);
        List<DataObjectAttribute> allAttributes = dataObjectAttributeService.listNoPage(tmpInfo);

        //初始化送往模板的字段属性
        List<DataObjectAttribute> conditionInfo = new ArrayList<>();
        //与传入的字段做合并
        if(columnInfo!=null && columnInfo.size()>0){
            for (DataObjectAttribute tmpAttribute : allAttributes) {
                boolean exist = replaceParam(columnInfo, tmpAttribute, false);
                if(exist){
                    conditionInfo.add(tmpAttribute);
                }
            }
        }
        return conditionInfo;
    }

    private boolean replaceParam(Map<String, Object> columnInfo, DataObjectAttribute tmpAttribute, boolean required) {
        String tmpCondition = tmpAttribute.getDescription().replaceAll("\\s","");
        if(tmpCondition.indexOf("#{") != -1){
            tmpCondition = tmpCondition.substring(tmpCondition.indexOf("#{")+2,tmpCondition.indexOf("}"));
            if(columnInfo.get(tmpCondition)!=null){
                String description = tmpAttribute.getDescription();
                String conditionValue = String.valueOf(columnInfo.get(tmpCondition));
//                if(tmpAttribute.getType()==null || "文本".equals(tmpAttribute.getType()) || "".equals(tmpAttribute.getType())){
//                    conditionValue = "'"+String.valueOf(columnInfo.get(tmpCondition))+"'";
//                }
                description = description.replaceAll("#\\{\\s*"+tmpCondition+"\\s*\\}", conditionValue);
                tmpAttribute.setDescription(description);
            } else if(required){
                throw new RuntimeException("缺少数据集必要查询参数" + tmpCondition);
            } else {
                return false;
            }
        }
        if(tmpAttribute.getDescription().indexOf("#{") != -1){
            replaceParam(columnInfo, tmpAttribute, true);
        } else if(!(tmpAttribute.getDescription().startsWith(" "))) {
            tmpAttribute.setDescription(" " + tmpAttribute.getDescription());
        }
        return true;
    }

    /**
     * 获取新增字段属性信息
     * @param dataObjectId
     * @param columnInfo
     * @return
     */
    private List<DataObjectAttribute> getAllAttributes(int dataObjectId, Map<String, Object> columnInfo) {

        //获取所有字段属性
        Map tmpInfo = new HashMap();
        tmpInfo.put("resId", dataObjectId);
        //对象的属性
        List<DataObjectAttribute> allAttributes = dataObjectAttributeService.listNoPage(tmpInfo);
        //初始化送往模板的字段属性
        List<DataObjectAttribute> conditionInfo = new ArrayList<>();
        //与传入的字段做合并
        for (Object key : columnInfo.keySet()) {
            for (DataObjectAttribute tmpAttribute : allAttributes) {
                if (tmpAttribute.getColumnName().toUpperCase().equals(key.toString().toUpperCase())) {
                    //匹配到相同的属性
                    //设置值
                    if("integer".equals(tmpAttribute.getJdbcType())){
                        tmpAttribute.setValue((int) columnInfo.get(key) + "");
                    } else if("double".equals(tmpAttribute.getJdbcType())){
                        tmpAttribute.setValue(Double.valueOf(columnInfo.get(key)  + "").toString());
                    } else if("varchar".equals(tmpAttribute.getJdbcType())){
                        tmpAttribute.setValue((String) columnInfo.get(key));
                    } else if("datetime".equals(tmpAttribute.getJdbcType())){
                        tmpAttribute.setValue((String) columnInfo.get(key));
                    }

                    conditionInfo.add(tmpAttribute);
                }
            }
        }
        return conditionInfo;
    }

}
