package com.tianyi.datacenter.resource.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.tianyi.datacenter.common.util.TianYuanIntesrvApiHelper;
import com.tianyi.datacenter.resource.dao.DataObjectAttributeDao;
import com.tianyi.datacenter.resource.entity.DataObject;
import com.tianyi.datacenter.resource.entity.DataObjectAttribute;
import com.tianyi.datacenter.resource.service.DataObjectAttributeService;
import com.tianyi.datacenter.resource.service.DataObjectService;
import com.tianyi.datacenter.storage.vo.PageListVo;
import com.tianyi.datacenter.storage.vo.RequestVo;
import com.tianyi.datacenter.storage.vo.ResponseVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.util.*;

/**
 * 数据对象属性服务实现
 *
 * @author wenxinyan
 * @version 0.1
 */
@Service
public class DataObjectAttributeServiceImpl implements DataObjectAttributeService {

    @Autowired
    private DataObjectService dataObjectService;
    @Autowired
    private DataObjectAttributeDao dataObjectAttributeDao;
    @Autowired
    private TianYuanIntesrvApiHelper tianYuanIntesrvApiHelper;
    @Override
    public int insert(DataObjectAttribute dataObjectAttribute){
        return dataObjectAttributeDao.insert(dataObjectAttribute);
    }

    @Override
    public int delete(Map<String, Object> param){
        return dataObjectAttributeDao.delete(param);
    }


    public int update(DataObjectAttribute dataObjectAttribute){
        return dataObjectAttributeDao.update(dataObjectAttribute);
    }

    @Override
    @Transactional(readOnly = true,propagation = Propagation.NOT_SUPPORTED)
    public ResponseVo list(RequestVo<Map> requestVo){
        PageListVo pageInfo = requestVo.getPageInfo();
        Map<String, Object> param = PageListVo.createParamMap(pageInfo.getPage(), pageInfo.getPageSize());
        param.putAll(requestVo.getRequest());

        List<DataObjectAttribute> attributeList = dataObjectAttributeDao.listBy(param);
        int count = dataObjectAttributeDao.countBy(param);

        pageInfo.setTotal(count);

        Map<String, Object> result = new HashMap<>();
        result.put("pageInfo", pageInfo);
        result.put("list", attributeList);

        ResponseVo responseVo = ResponseVo.success(result);

        return responseVo;
    }

    @Override
    @Transactional(readOnly = true,propagation = Propagation.NOT_SUPPORTED)
    public List<DataObjectAttribute> listNoPage(Map<String, Object> param){
        return dataObjectAttributeDao.listByNoPage(param);
    }

    @Override
    @Transactional(readOnly = true,propagation = Propagation.NOT_SUPPORTED)
    public List<DataObjectAttribute> listAttributesIncludeDic(Map<String, Object> param) {
        List<DataObjectAttribute> allAttributes = this.listNoPage(param);
        List<DataObjectAttribute> resultAttribute = new ArrayList<>();
        if(allAttributes.size()>0){
            int count = allAttributes.get(allAttributes.size()-1).getId()+1;
            for (DataObjectAttribute tmpAttribute : allAttributes) {
                resultAttribute.add(tmpAttribute);
                if(tmpAttribute.getDicRes() != -1) { //引用字典的对象处理
//                    DataObject dicObject = dataObjectService.getById(tmpAttribute.getDicRes());
//                    DataObjectAttribute dicValue = this.getById(tmpAttribute.getDicValue());
//                    DataObjectAttribute dicKey = this.getById(tmpAttribute.getDicKey());
//                    dicValue.setId(count++);
//                    dicValue.setColumnName(dicObject.getDefined()+dicValue.getColumnName());
//                    dicValue.setName(dicObject.getName()+dicValue.getName());
//                    dicValue.setDescription(dicObject.getDescription()+dicValue.getDescription());
//                    resultAttribute.add(dicValue);
//
//                    tmpAttribute.setDicKeyName(dicKey.getName());
//                    tmpAttribute.setDicValueName(dicValue.getName());

                    DataObject dicObject = dataObjectService.getById(tmpAttribute.getDicRes());
                    DataObjectAttribute dicKey = this.getById(tmpAttribute.getDicKey());
                    DataObjectAttribute dicValue = this.getById(tmpAttribute.getDicValue());

                    tmpAttribute.setDicResColumnName(dicObject.getDefined());
                    tmpAttribute.setDicKeyColumnName(dicKey.getColumnName());
                    tmpAttribute.setDicValueColumnName(dicValue.getColumnName());
                    tmpAttribute.setDicResName(dicObject.getName());
                    tmpAttribute.setDicKeyName(dicKey.getName());
                    tmpAttribute.setDicValueName(dicValue.getName());

                }
            }
        }
        return resultAttribute;
    }

    @Override
    @Transactional(readOnly = true,propagation = Propagation.NOT_SUPPORTED)
    public DataObjectAttribute getById(int id){
        return dataObjectAttributeDao.getById(id);
    }

    @Override
    public List<DataObjectAttribute> listDwf(String attributeDwfUrl) {
        JSONObject jo = tianYuanIntesrvApiHelper.getResult(attributeDwfUrl);
        List<DataObjectAttribute> list = new ArrayList<DataObjectAttribute>();
        JSONArray jsonArray = jo.getJSONArray("data");
        for (Iterator iterator = jsonArray.iterator(); iterator.hasNext();){
            JSONObject jsonObject = (JSONObject) iterator.next();
            DataObjectAttribute dataObject = new DataObjectAttribute();
            dataObject.setColumnName(jsonObject.getString("attributeName"));
            dataObject.setName(jsonObject.getString("displayName"));
            list.add(dataObject);
        }
        return list;
    }

    @Override
    public List<DataObjectAttribute> listDb(String driver, String url, String user, String password, String tableName) {
        try {
            //获取数据库连接
            Connection connection = getConnection(driver, url, user, password);
            DatabaseMetaData databaseMetaData = connection.getMetaData();
            //如果是获取所有字段信息生成代码，执行
            ResultSet resultSet = databaseMetaData.getColumns(null, "%", tableName, "%");
            List<DataObjectAttribute> columnClassList = getColumns(resultSet);
            connection.close();
            return columnClassList;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    private Connection getConnection(String driver, String url, String user, String password) throws Exception {
        Class.forName(driver);
        return DriverManager.getConnection(url, user, password);
    }


    private List<DataObjectAttribute> getColumns(ResultSet resultSet) throws Exception {
        List<DataObjectAttribute> columnClassList = new ArrayList<>();
        DataObjectAttribute columnClass = null;
        while (resultSet.next()) {
            //id字段略过
            if (resultSet.getString("COLUMN_NAME").equals("id")) continue;
            columnClass = new DataObjectAttribute();
            //获取字段名称
//            columnClass.setColumnName(resultSet.getString("COLUMN_NAME"));
            //获取字段类型
            columnClass.setJdbcType(resultSet.getString("TYPE_NAME"));
            //转换字段名称，如 sys_name 变成 SysName
            columnClass.setColumnName(replaceUnderLineAndUpperCase(resultSet.getString("COLUMN_NAME")));
            //字段在数据库的注释
            columnClass.setName(resultSet.getString("REMARKS"));
            columnClass.setType("");
            columnClassList.add(columnClass);
        }
        return columnClassList;
    }

    /**
     * 将表名和字段名替换为正常的驼峰命名
     * @param str
     * @return
     */
    public String replaceUnderLineAndUpperCase(String str) {
        StringBuffer sb = new StringBuffer();
        sb.append(str);
        int count = sb.indexOf("_");
        while (count != 0) {
            int num = sb.indexOf("_", count);
            count = num + 1;
            if (num != -1) {
                char ss = sb.charAt(count);
                char ia = (char) (ss - 32);
                sb.replace(count, count + 1, ia + "");
            }
        }
        String result = sb.toString().replaceAll("_", "");
        return StringUtils.capitalize(result);
    }
}
