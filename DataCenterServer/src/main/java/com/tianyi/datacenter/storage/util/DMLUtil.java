package com.tianyi.datacenter.storage.util;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelectItem;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.util.JdbcConstants;
import com.tianyi.datacenter.common.exception.DataCenterException;
import com.tianyi.datacenter.common.util.FreeMarkerRoot;
import com.tianyi.datacenter.common.util.FreeMarkerUtil;
import com.tianyi.datacenter.resource.entity.DataObject;
import com.tianyi.datacenter.resource.entity.DataObjectAttribute;
import com.tianyi.datacenter.storage.vo.DataStorageDMLVo;
import com.tianyi.datacenter.storage.vo.PageListVo;
import freemarker.template.TemplateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.tianyi.datacenter.common.exception.DataCenterException.DC_DO_8901;
import static com.tianyi.datacenter.common.exception.DataCenterException.DC_DO_8901_MSG;

/**
 * 预处理DML语句的工具类
 * 2018/11/19 15:25
 *
 * @author zhouwei
 * @version 0.1
 **/
@Component
public class DMLUtil {

    private Logger logger = LoggerFactory.getLogger(DMLUtil.class);
    private final static String dbType = JdbcConstants.MYSQL; // 可以是ORACLE、POSTGRESQL、SQLSERVER、ODPS等
    /**
     * 生成update语句
     *
     * @param dataObject 表信息
     * @param updateInfo 字段信息
     * @param condition 条件字段
     * @return 插入语句
     * @author xiayuan
     * 2018/11/24 17:39
     */
    private static String generateUpdateSql(DataObject dataObject, List<DataObjectAttribute> updateInfo,
                                            List<DataObjectAttribute> condition) throws IOException, TemplateException {
        //组装ftl的root节点
        FreeMarkerRoot ftlRoot = FreeMarkerRoot.build()
        //字段值
        .put("updateInfo", updateInfo)
        //数据库表
        .put("dataObject", dataObject)
        .put("condition", condition);
        String dmlSql = FreeMarkerUtil.process(ftlRoot, "Update");
        return dmlSql;
    }

    /**
     * 根据DML对象生成DML语句
     *
     * @param dmlVo    dml信息
     * @param pageInfo 分页信息
     * @param orderBy 排序信息
     * @return 查询sql语句
     * @author zhouwei
     * 2018/11/19 15:26
     */
    public String generateDML(DataStorageDMLVo dmlVo, PageListVo pageInfo, String orderBy) throws DataCenterException {
        String dmlSql;

        switch (dmlVo.getDmlType()) {
            case "R":
                if ("数据集".equals(dmlVo.getDataObject().getType())) {
                    //数据集接口直接使用数据集定义的sql
                    dmlSql = generateListRetrieveSql(dmlVo.getCondition(), dmlVo.getDataObject(), pageInfo);
                    if (StringUtils.isEmpty(dmlSql)) {
                        throw new DataCenterException("获取数据集sql失败");
                    }
                    break;
                }
                //查询操作
                try {
                    dmlSql = generateRetrieveSql(dmlVo.getCondition(), dmlVo.getDataObject(), dmlVo.getAttributes(), pageInfo, orderBy);
                } catch (IOException e) {
                    logger.error("生成select语句异常",e);
                    throw new DataCenterException("生成Select语句失败");

                } catch (TemplateException e) {
                    logger.error("生成select语句异常",e);
                    throw new DataCenterException("生成Select语句失败");

                }
                break;
            case "C":
                //新增操作
                try {
                    dmlSql = generateInsertSql(dmlVo.getDataObject(), dmlVo.getAttributes());
                } catch (IOException e) {
                    logger.error("生成insert语句异常",e);
                    throw new DataCenterException("生成insert语句失败");

                } catch (TemplateException e) {
                    logger.error("生成insert语句异常",e);
                    throw new DataCenterException("生成insert语句失败");

                }
                break;
            case "U":
                try {
                    dmlSql = generateUpdateSql(dmlVo.getDataObject(), dmlVo.getAttributes(), dmlVo.getCondition());
                } catch (IOException e) {
                    logger.error("生成update语句异常",e);
                    throw new DataCenterException("生成update语句失败");

                } catch (TemplateException e) {
                    logger.error("生成update语句异常",e);
                    throw new DataCenterException("生成update语句失败");

                }
                break;
            case "D":
                try {
                    dmlSql = generateDeleteSql(dmlVo.getDataObject(), dmlVo.getCondition());
                } catch (IOException e) {
                    logger.error("生成delete语句异常",e);
                    throw new DataCenterException("生成delete语句失败");

                } catch (TemplateException e) {
                    logger.error("生成delete语句异常",e);
                    throw new DataCenterException("生成delete语句失败");

                }
                break;
            default:
                throw new DataCenterException(DC_DO_8901, DC_DO_8901_MSG);
        }
        //替换模板文件中的换行
        dmlSql = dmlSql.replaceAll("[\r\n]", "");
        while(dmlSql.contains("  ")){
            dmlSql = dmlSql.replaceAll("\\s{2,}?", " ");
        }
        logger.debug("生成的dml语句：" + dmlSql);
        return dmlSql;
    }
    /**
     * 生成数据集查询sql语句
     *
     * @param condition 查询条件
     * @param dataObject 数据对象
     * @param pageInfo   分页信息
     * @return String 查询sql
     * @author tianxujin
     * 20190409
     */
    private String generateListRetrieveSql(List<DataObjectAttribute> condition, DataObject dataObject, PageListVo pageInfo) {
        String dmlSql = dataObject.getDefined();
        for(DataObjectAttribute dataObjectAttribute:condition){
            dmlSql += dataObjectAttribute.getDescription();
        }
        if(pageInfo!=null && pageInfo.getPage() != 0 && pageInfo.getPageSize() != 0){
            //组装分页信息
            Map<String, Object> pageInfoTmp = null;
            if(pageInfo!=null){
                pageInfoTmp = PageListVo.createParamMap(pageInfo.getPage(), pageInfo.getPageSize());
            }
            dmlSql += " LIMIT "+pageInfoTmp.get("start")+","+pageInfoTmp.get("length");
        }
        return dmlSql;
    }

    /**
     * 生成delete语句
     * @author zhouwei
     * 2018/11/28 09:48
     * @param dataObject 表信息
     * @param condition 删除条件
     * @return 删除sql语句
    */
    private String generateDeleteSql(DataObject dataObject, List<DataObjectAttribute> condition) throws IOException, TemplateException {
        String dmlSql;
        //组装ftl的root节点
        FreeMarkerRoot ftlRoot = FreeMarkerRoot.build()
        //字段值
        .put("condition", condition)
        //数据库表
        .put("dataObject", dataObject);
        dmlSql = FreeMarkerUtil.process(ftlRoot, "Delete");
        return dmlSql;
    }

    /**
     * 生成insert语句
     *
     * @param dataObject 表信息
     * @param condition 要插入的字段信息，复用条件字段
     * @return 插入语句
     * @author zhouwei
     * 2018/11/22 17:39
     */
    private String generateInsertSql(DataObject dataObject, List<DataObjectAttribute> condition) throws IOException, TemplateException {
        String dmlSql;
        //组装ftl的root节点
        FreeMarkerRoot ftlRoot = FreeMarkerRoot.build()
        //字段值
        .put("insertInfo", condition)
        //数据库表
        .put("dataObject", dataObject);
        dmlSql = FreeMarkerUtil.process(ftlRoot, "Insert");
        return dmlSql;
    }

    /**
     * 生成查询sql语句，暂时不支持group by
     *
     * @param conditions 查询条件
     * @param dataObject 数据对象
     * @param attributes 数据对象属性列表
     * @param pageInfo   分页信息
     * @param orderBy
     * @return String 查询sql
     * @author zhouwei
     * 2018/11/19 16:39
     */
    private String generateRetrieveSql(List<DataObjectAttribute> conditions, DataObject dataObject,
                                       List<DataObjectAttribute> attributes, PageListVo pageInfo, String orderBy) throws IOException, TemplateException {
        if (conditions == null || conditions.size() == 0) {
            conditions = null;
        }
        String dmlSql;
        //组装ftl的root节点
        FreeMarkerRoot ftlRoot = FreeMarkerRoot.build()
        //查询条件
        .put("condition", conditions)
        //字段
        .put("columns", attributes)
        //数据库表
        .put("dataObject", dataObject);

        //组装分页信息
        Map<String, Object> pageInfoTmp = null;
        if(pageInfo!=null && pageInfo.getPage() != 0 && pageInfo.getPageSize() != 0){
            pageInfoTmp = PageListVo.createParamMap(pageInfo.getPage(), pageInfo.getPageSize());
        }
        //分页信息
        ftlRoot.put("pageInfo", pageInfoTmp);

        //排序信息
        ftlRoot.put("orderBy", orderBy);

        dmlSql = FreeMarkerUtil.process(ftlRoot, "Retrieve");
        return dmlSql;
    }

    /**
     * 将结果集的key转换成sql中自定义的columnName
     * @param datas 结果集
     * @param sql 原始sql
     */
    public static void handleDataList(List<Map<String, Object>> datas, String sql) {
        List<String> columnList = getColumnListBySqlDruid(sql);
        if(columnList.size()<0){
            return;
        }
        Map<String, String> columnMap = new HashMap<String, String>();
        for (String column : columnList) {
            columnMap.put(column.toUpperCase(), column);
        }
        List<String> keyList = new ArrayList<>();
        for (String key : datas.get(0).keySet()) {
            keyList.add(key);
        }
        for(Map<String, Object> result : datas) {
            for (String key: keyList) {
                if(columnMap.get(key.toUpperCase()) != null)
                    result.put(columnMap.get(key.toUpperCase()), result.remove(key));
            }
        }
    }

    /**
     * 获取sql定义中的结果集字段集
     * @param sql 原始sql
     */
    public static List<String> getColumnListBySql(String sql) {
        List<String> columnList = new ArrayList<>();
        String str = sql;
        if(str==null || str.length()<7){
            return columnList;
        }
        str = str.replaceAll("\\s+", " ").trim()
                .substring(7).replaceAll(" (?i)from ", "@from@")
                .replaceAll("(?i)distinct ", "");
        if(str.indexOf("@from@") == -1){
            return columnList;
        }
        str = str.substring(0, str.indexOf("@from@"))
                .replaceAll(" (?i)as ", " ")
                .replaceAll("(\\. | \\.)", ".")
                .replaceAll("(, | ,)", ",");
        if("*".equals(str)){
            return columnList;
        }
        String[] strs = str.split(",");
        for (String strtmp : strs) {
            String[] strtmps = strtmp.trim().split(" ");
            String column = "";
            if (strtmps.length > 1) {
                column=strtmps[1];
            } else {
                if(strtmps[0].indexOf(".")!=-1){
                    column=strtmps[0].substring(strtmps[0].indexOf(".") + 1);
                } else {
                    column=strtmps[0];
                }
            }
            columnList.add(column);
        }
        return columnList;
    }
    /**
     * 获取sql定义中的结果集字段集
     * @param sql 原始sql
     */
    public static List<String> getColumnListBySqlDruid(String sql) {
        List<String> columnList = new ArrayList<>();
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);
        SQLSelectStatement stmt = (SQLSelectStatement) stmtList.get(0);
        SQLSelectQueryBlock queryBlock = stmt.getSelect().getQueryBlock();
        List<SQLSelectItem> selectItemList = queryBlock.getSelectList();
        for(SQLSelectItem selectItem : selectItemList) {
            String column = selectItem.computeAlias();
            if(column!=null && !column.equals("*")){
                columnList.add(column);
            }
        }
        return columnList;
    }

    public static void main(String[] args) {
        String sql = "SELECT hello,(select typename from data_center_type where data_center_type.id=a.typeId) AS typeName FROM hmserver.data_center_res a";
        List<String> abc = getColumnListBySqlDruid(sql);
        for (String s : abc) {
            System.out.println(s);
        }

    }
}
