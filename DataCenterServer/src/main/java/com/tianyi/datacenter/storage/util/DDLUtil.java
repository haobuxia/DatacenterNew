package com.tianyi.datacenter.storage.util;

import com.tianyi.datacenter.common.exception.DataCenterException;
import com.tianyi.datacenter.common.util.FreeMarkerRoot;
import com.tianyi.datacenter.common.util.FreeMarkerUtil;
import com.tianyi.datacenter.resource.entity.DataObject;
import com.tianyi.datacenter.resource.entity.DataObjectAttribute;
import com.tianyi.datacenter.storage.vo.DataStorageDDLVo;
import freemarker.template.TemplateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static com.tianyi.datacenter.common.exception.DataCenterException.DC_DO_8901;
import static com.tianyi.datacenter.common.exception.DataCenterException.DC_DO_8901_MSG;

@Component
public class DDLUtil {

    private Logger logger = LoggerFactory.getLogger(DBUtil.class);

    /**
     * 根据DDL对象生成DDL语句
     *
     * @param ddlVo ddl信息
     * @return 查询sql语句
     * @author zhouwei
     * 2018/11/19 15:26
     */
    public String generateDDL(DataStorageDDLVo ddlVo) throws DataCenterException {
        String ddlSql;

        switch (ddlVo.getDdlType()) {
            case "C":
                //创建表操作
                String ftlName = "Create";
                try {
                    ddlSql = generateCreateTableSql(ddlVo.getDataObject(), ddlVo.getAttributes(), ftlName);
                } catch (IOException e) {
                    logger.error("生成create table语句异常",e);
                    throw new DataCenterException("生成Create Table语句失败");
                } catch (TemplateException e) {
                    logger.error("生成create table语句异常",e);
                    throw new DataCenterException("生成Create Table语句失败");
                }
                break;
            case "R":
                // 重命名表操作
                ftlName = "Rename";
                try {
                    ddlSql = generateRenameTableSql(ddlVo.getDataObject(), ftlName);
                } catch (IOException e) {
                    logger.error("生成Rename table语句异常",e);
                    throw new DataCenterException("生成Rename Table语句失败");
                } catch (TemplateException e) {
                    logger.error("生成Rename table语句异常",e);
                    throw new DataCenterException("生成Rename Table语句失败");
                }
                break;
            case "U":
                ftlName = "Alter";
                try {
                    ddlSql = generateAlterTableSql(ddlVo.getDataObject(), ddlVo.getAddColumns(), ddlVo.getAlterColumns(),
                            ddlVo.getDropColumns(), ftlName, ddlVo.getPkInfo());
                } catch (TemplateException e) {
                    logger.error("生成alter table语句异常",e);
                    throw new DataCenterException("生成Alter Table语句失败");
                } catch (IOException e){
                    logger.error("生成alter table语句异常",e);
                    throw new DataCenterException("生成Alter Table语句失败");
                }
                break;
            case "D":
                ftlName = "Drop";
                try {
                    ddlSql = generateDropTableSql(ddlVo.getDataObject(), ftlName);
                } catch (IOException e) {
                    logger.error("生成drop table语句异常",e);
                    throw new DataCenterException("生成drop table语句失败");
                } catch (TemplateException e) {
                    logger.error("生成drop table语句异常",e);
                    throw new DataCenterException("生成drop table语句失败");
                }
                break;
            default:
                throw new DataCenterException(DC_DO_8901, DC_DO_8901_MSG);
        }
        //删除回车换行符
        ddlSql = ddlSql.replaceAll("[\r\n]", "");
        while(ddlSql.contains("  ")){
            ddlSql = ddlSql.replaceAll("\\s{2,}?", " ");
        }
        logger.debug("生成DDL语句:" + ddlSql);
        return ddlSql;
    }

    private String generateRenameTableSql(DataObject dataObject, String ftlName) throws IOException, TemplateException {
        FreeMarkerRoot ftlRoot = FreeMarkerRoot.build()
                .put("dataObject", dataObject);
        return FreeMarkerUtil.process(ftlRoot, ftlName);
    }

    /**
     * 生成dorp表的sql语句
     * @author zhouwei
     * 2018/12/5 11:46
     * @param dataObject 表信息
     * @param ftlName 模板文件名称
     * @return drop语句
    */
    private String generateDropTableSql(DataObject dataObject, String ftlName) throws IOException, TemplateException {
        FreeMarkerRoot ftlRoot = FreeMarkerRoot.build().put("dataObject", dataObject);
        return FreeMarkerUtil.process(ftlRoot, ftlName);
    }

    /**
     * 生成修改表的ddl语句
     * @author zhouwei
     * 2018/11/28 19:10
     * @param dataObject 表信息
     * @param addColumns 修改字段信息
     * @param alterColumns 修改字段信息
     * @param dropColumns 删除字段信息
     * @param ftlName 模板名称
     * @param pkInfo 主键信息
     * @return ddl修改表信息sql
    */
    private String generateAlterTableSql(DataObject dataObject, List<DataObjectAttribute> addColumns,
                                         List<DataObjectAttribute> alterColumns,
                                         List<DataObjectAttribute> dropColumns, String ftlName,
                                         Map<String, Object> pkInfo) throws IOException, TemplateException {

        FreeMarkerRoot ftlRoot = FreeMarkerRoot.build()
        .put("addColumns", addColumns)
        .put("alterColumns", alterColumns)
        .put("dropColumns", dropColumns)
        .put("dataObject", dataObject)
        .put("pkInfo", pkInfo);

        return FreeMarkerUtil.process(ftlRoot, ftlName);
    }

    /**
     * 生成创建表的ddl语句
     * 不支持联合主键，联合索引，触发器
     *
     * @param dataObject 数据对象
     * @param attributes 数据对象属性列表
     * @param ftlName 模板名称
     * @return String 查询sql
     * @author zhouwei
     * 2018/11/19 16:39
     */
    private String generateCreateTableSql(DataObject dataObject,
                                                 List<DataObjectAttribute> attributes, String ftlName) throws IOException, TemplateException {
        FreeMarkerRoot ftlRoot = FreeMarkerRoot.build()
        .put("columns", attributes)
        .put("dataObject", dataObject);

        return FreeMarkerUtil.process(ftlRoot, ftlName);
    }
}
