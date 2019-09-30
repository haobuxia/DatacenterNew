package com.tianyi.datacenter.storage.service.impl;

import com.tianyi.datacenter.common.exception.DataCenterException;
import com.tianyi.datacenter.resource.entity.DataObject;
import com.tianyi.datacenter.resource.entity.DataObjectAttribute;
import com.tianyi.datacenter.resource.service.DataObjectAttributeService;
import com.tianyi.datacenter.storage.service.DataStorageDDLService;
import com.tianyi.datacenter.storage.util.DBUtil;
import com.tianyi.datacenter.storage.util.DDLUtil;
import com.tianyi.datacenter.storage.vo.DataStorageDDLVo;
import com.tianyi.datacenter.storage.vo.RequestVo;
import com.tianyi.datacenter.storage.vo.ResponseVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 执行DDL语句服务实现
 *
 * @author zhouwei
 * 2018/11/22 08:30
 * @version 0.1
 **/
@Service("columnDDLService")
public class DataStorageColumnDDLServiceImpl implements DataStorageDDLService {

    @Autowired
    private DBUtil database;
    private DDLUtil ddlUtil = new DDLUtil();
    @Autowired
    private DataObjectAttributeService dataObjectAttributeService;

    private Logger logger = LoggerFactory.getLogger(DataStorageColumnDDLServiceImpl.class);

    @Override
    public RequestVo<DataStorageDDLVo> getRequestVo(String ddlType, DataObject dataObject, List<DataObjectAttribute> attributeList){
        DataStorageDDLVo dataStorageDDLVo = new DataStorageDDLVo();
        dataStorageDDLVo.setDdlType(ddlType);
        dataStorageDDLVo.setDataObject(dataObject);
        dataStorageDDLVo.setAttributes(attributeList);

        RequestVo<DataStorageDDLVo> requestVo = new RequestVo<>(dataStorageDDLVo);

        return requestVo;
    }

    @Override
    public ResponseVo doServer(RequestVo<DataStorageDDLVo> requestVo) {

        //校验服务接口
        DataStorageDDLVo ddlVo = requestVo.getRequest();
        ResponseVo validResult = validParam(ddlVo);
        if(!validResult.isSuccess()){
            //参数校验失败
            logger.error(validResult.getMessage());
            return validResult;
        }
        String ddlType = ddlVo.getDdlType();
        if ("C".equals(ddlType)){// 新增字段
            try {
                handlerAddColumns(ddlVo);
                ddlVo.setDdlType("U");//新增字段生成sql方法和修改字段一样
                //获取主键信息
//                ddlVo.setPkInfo(database.getPrimaryKey(ddlVo.getDataObject().getDefined()));
            } catch (SQLException e) {
                logger.error("获取表schema信息失败",e);
                return ResponseVo.fail("生成Alter语句异常");
            } catch (DataCenterException e) {
                logger.error("处理schema信息失败:",e);
                return ResponseVo.fail("生成Alter语句异常");
            }
        } else if ("D".equals(ddlType)){// 删除字段
            try {
                handlerDelColumns(ddlVo);
                ddlVo.setDdlType("U");//删除字段生成sql方法和修改字段一样
            } catch (SQLException e) {
                logger.error("获取表schema信息失败",e);
                return ResponseVo.fail("生成Alter语句异常");
            } catch (DataCenterException e) {
                logger.error("处理schema信息失败:",e);
                return ResponseVo.fail("生成Alter语句异常");
            }
        } else if ("U".equals(ddlType)){
            try {
                //如果是alter操作，需要处理新增字段，修改字段，删除字段的信息
                handlerAlterColumns(ddlVo);
                //获取主键信息
//                ddlVo.setPkInfo(database.getPrimaryKey(ddlVo.getDataObject().getDefined()));
            } catch (SQLException e) {
                logger.error("获取表schema信息失败",e);
                return ResponseVo.fail("生成Alter语句异常");
            } catch (DataCenterException e) {
                logger.error("处理schema信息失败:",e);
                return ResponseVo.fail("生成Alter语句异常");
            }
        }
        //创建ddl语句
        String ddlSql;
        try {
            ddlSql = ddlUtil.generateDDL(ddlVo);
        } catch (DataCenterException e) {
            logger.error("生成alter语句失败:\n"+e.getStackTrace());
            return ResponseVo.fail(e.getMessage());
        }

        //执行ddl语句
        try {
            database.executeDDL(ddlType, ddlSql);
        } catch (Exception e) {
            logger.error("执行alter语句失败:\n"+e.getStackTrace());
            return ResponseVo.fail("执行alter语句异常:"+e.getCause());
        }
        return ResponseVo.success();
    }

    private void handlerAddColumns(DataStorageDDLVo ddlVo) throws SQLException, DataCenterException {
        List<DataObjectAttribute> attributes = ddlVo.getAttributes();
        List<DataObjectAttribute> addColumns = new ArrayList<>();
        for (DataObjectAttribute attribute : attributes) {
            addColumns.add(attribute);
        }
        ddlVo.setAddColumns(addColumns);
    }

    private void handlerDelColumns(DataStorageDDLVo ddlVo) throws SQLException, DataCenterException {
        List<DataObjectAttribute> attributes = ddlVo.getAttributes();
        List<DataObjectAttribute> delColumns = new ArrayList<>();
        for (DataObjectAttribute attribute : attributes) {
            delColumns.add(attribute);
        }
        ddlVo.setDropColumns(delColumns);
    }

    /**
     * 服务参数校验方法
     * @author zhouwei
     * 2018/12/3 14:14
     * @param ddlVo ddl服务入参
     * @return ResponseVo.success为true表示校验成功
    */
    private ResponseVo validParam(DataStorageDDLVo ddlVo) {
        if(StringUtils.isEmpty(ddlVo.getDdlType())){
            return ResponseVo.fail("参数校验失败:操作类型不可为空");
        }
        if (ddlVo.getDataObject() == null || ddlVo.getAttributes() == null) {
            return ResponseVo.fail("参数校验失败:数据对象或属性不可为空");
        }
        if (StringUtils.isEmpty(ddlVo.getDataObject().getDefined())) {
            return ResponseVo.fail("参数校验失败:表名不可为空");
        }
        return ResponseVo.success();
    }
    /**
     * 组装alter表需要的属性信息
     * @author zhouwei
     * 2018/11/29 10:30
     * @param ddlVo 修改请求接口数据
     * @exception SQLException 获取字段信息异常
     */
    private void handlerAlterColumns(DataStorageDDLVo ddlVo) throws SQLException, DataCenterException {
        List<DataObjectAttribute> attributes = ddlVo.getAttributes();
        List<DataObjectAttribute> alterColumns = new ArrayList<>();
       /*
       以请求参数中的字段为基准，比较schema的字段信息，比较后删除schema信息，剩余的是需要删除的字段
       如果没有，说明需要新增
       如果有，需要判断是否需要修改
        */
        for (DataObjectAttribute attribute : attributes) {
            DataObjectAttribute oldDataObjectAttribute = dataObjectAttributeService.getById(attribute.getId());
            if(needAlter(attribute, oldDataObjectAttribute)) {
                alterColumns.add(attribute);
            }
        }
        ddlVo.setAlterColumns(alterColumns);
    }

    private boolean needAlter(DataObjectAttribute attribute, DataObjectAttribute oldAttribute) {
        try {
            List<Map<String ,Object>> list = compareTwoClass(attribute,oldAttribute);
            if(list.size()>0){
                attribute.setOldColumnName(attribute.getColumnName());
                for(Map<String ,Object> map : list) {
                    if("columnName".equals(map.get("name").toString())) {
                        attribute.setOldColumnName(map.get("old").toString());
                    }
                }
                return true;
            } else {
                return false;
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 获取两个对象同名属性内容不相同的列表
     * @param class1 新对象1
     * @param class2 旧对象2
     * @return
     * @throws ClassNotFoundException
     * @throws IllegalAccessException
     */
    public static List<Map<String ,Object>> compareTwoClass(Object class1,Object class2) throws ClassNotFoundException, IllegalAccessException {
        List<Map<String,Object>> list=new ArrayList<Map<String, Object>>();
//获取对象的class
        Class<?> clazz1 = class1.getClass();
        Class<?> clazz2 = class2.getClass();
//获取对象的属性列表
        Field[] field1 = clazz1.getDeclaredFields();
        Field[] field2 = clazz2.getDeclaredFields();
//遍历属性列表field1
        for(int i=0;i<field1.length;i++){
//遍历属性列表field2
            for(int j=0;j<field2.length;j++){
//如果field1[i]属性名与field2[j]属性名内容相同
                if(field1[i].getName().equals(field2[j].getName())){
                    if(field1[i].getName().equals(field2[j].getName())){
                        field1[i].setAccessible(true);
                        field2[j].setAccessible(true);
//如果field1[i]属性值与field2[j]属性值内容不相同
                        if (!compareTwo(field1[i].get(class1), field2[j].get(class2))){
                            Map<String,Object> map2=new HashMap<String, Object>();
                            map2.put("name",field1[i].getName());
                            map2.put("new",field1[i].get(class1));
                            map2.put("old",field2[j].get(class2));
                            list.add(map2);
                        }
                        break;
                    }
                }}
        }
        return list;
    }
    /**
     * 对比两个数据是否内容相同
     *
     * @param  object1,object2
     * @return boolean类型
     */
    public static boolean compareTwo(Object object1,Object object2){

        if(object1==null&&object2==null){
            return true;
        }
        if(object1==null&&object2!=null){
            return false;
        }
        if(object1.equals(object2)){
            return true;
        }
        return false;
    }

    /**
     * 组装alter表需要的属性信息
     * @author zhouwei
     * 2018/11/29 10:30
     * @param ddlVo 修改请求接口数据
     * @exception SQLException 获取字段信息异常
    */
    private void handlerBatchColumns(DataStorageDDLVo ddlVo) throws SQLException, DataCenterException {
        List<DataObjectAttribute> attributes = ddlVo.getAttributes();
        Map<String, Map<String, Object>> schemaColumnInfo = database.getColumnsInfo(ddlVo.getDataObject().getDefined());
        //数据库有多少个字段
        int dbTableHaveColumns = schemaColumnInfo.size();

        List<DataObjectAttribute> addColumns = new ArrayList<>();
        List<DataObjectAttribute> alterColumns = new ArrayList<>();
        List<DataObjectAttribute> dropColumns = new ArrayList<>();
       /*
       以请求参数中的字段为基准，比较schema的字段信息，比较后删除schema信息，剩余的是需要删除的字段
       如果没有，说明需要新增
       如果有，需要判断是否需要修改
        */

        for (DataObjectAttribute attribute : attributes) {
            Map<String ,Object> schemaColumn = schemaColumnInfo.get(attribute.getColumnName().toUpperCase()) ;
            if (schemaColumn == null) {
                //没有这个字段，需要新增
                addColumns.add(attribute);
            } else {
                //有同名文件，判断是否需要修改
                if(needAlterBatch(attribute, schemaColumn)) {
                    alterColumns.add(attribute);
                }
            }
            //删除处理过的字段，剩余的字段需要删除
            schemaColumnInfo.remove(attribute.getColumnName().toUpperCase());
        }
        //组装需要删除的字段
        for (Object o : schemaColumnInfo.keySet()) {
            DataObjectAttribute tmp = new DataObjectAttribute();
            tmp.setColumnName((String) o);
            dropColumns.add(tmp);
        }
        ddlVo.setAddColumns(addColumns);
        ddlVo.setAlterColumns(alterColumns);
        if(dbTableHaveColumns>0 && dbTableHaveColumns==dropColumns.size()) {
            throw new DataCenterException("应该调用删除table服务");
        }
        ddlVo.setDropColumns(dropColumns);
    }

    /**
     * 比较字段信息是否一致（字段名一定一致）
     * @author zhouwei
     * 2018/11/29 15:58
     * @param attribute 传入的字段信息
     * @param schemaColumn 数据库字段信息
     * @return 比较结果 true，一致；false，不一致
    */
    private boolean needAlterBatch(DataObjectAttribute attribute, Map<String, Object> schemaColumn) {
        //判断是否为空
       if(("true".equals(attribute.getIsNull()))!=("YES".equals(schemaColumn.get("IS_NULLABLE")))){
           return true;
       }
       //判断类型是否一致
        if(!attribute.getJdbcType().equalsIgnoreCase(((String) schemaColumn.get("TYPE_NAME")).split(" ")[0])){
            return  true;
        }
        //判断字段说明是否一致
        String desc = attribute.getDescription()==null?"":attribute.getDescription();
        if(!desc.equalsIgnoreCase((String) schemaColumn.get("REMARKS"))){
            return true;
        }
        //判断长度是否一致，字符和日期判断长度，数字是精度，不做校验
        if ("text".equals(attribute.getType()) || "date".equals(attribute.getJdbcType())) {
            if(attribute.getLength()!=(int)schemaColumn.get("COLUMN_SIZE")){
                return true;
            }
        }
        return false;
    }
}
