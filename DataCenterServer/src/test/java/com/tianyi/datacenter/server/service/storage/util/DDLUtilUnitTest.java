package com.tianyi.datacenter.server.service.storage.util;

import com.tianyi.datacenter.common.exception.DataCenterException;
import com.tianyi.datacenter.resource.entity.DataObject;
import com.tianyi.datacenter.resource.entity.DataObjectAttribute;
import com.tianyi.datacenter.storage.util.DBUtil;
import com.tianyi.datacenter.storage.util.DDLUtil;
import com.tianyi.datacenter.storage.vo.DataStorageDDLVo;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class DDLUtilUnitTest {

    @InjectMocks
    private DDLUtil ddlUtil = new DDLUtil();
    @Mock
    private DBUtil database;

    @Before
    public void setUp() throws SQLException {
        MockitoAnnotations.initMocks(this);
    }
    @Test
    public void createDDL() {
        DataStorageDDLVo ddlVo = new DataStorageDDLVo();
        //create table
        ddlVo.setDdlType("C");
        //表名，test
        DataObject object = new DataObject();
        object.setDefined("Test");
        object.setDescription("comment for table test");
        ddlVo.setDataObject(object);

        //字段
        List<DataObjectAttribute> attributes = new ArrayList<>();
        DataObjectAttribute attribute = new DataObjectAttribute();
        //自增
        attribute.setIsIncrement("true");
        attribute.setColumnName("field1");
        attribute.setDescription("comment for field1");
        attribute.setJdbcType("int");
        attribute.setLength(10);
        attributes.add(attribute);
        //主键
        attribute = new DataObjectAttribute();
        attribute.setColumnName("field2");
        attribute.setDescription("comment for field2");
        attribute.setJdbcType("int");
        attribute.setLength(20);
        attribute.setIsKey("true");
        attributes.add(attribute);

        //可以为空
        attribute = new DataObjectAttribute();
        attribute.setColumnName("field2");
        attribute.setDescription("comment for field2");
        attribute.setJdbcType("varchar");
        attribute.setLength(20);
        attribute.setIsNull("true");
        attributes.add(attribute);

        ddlVo.setAttributes(attributes);


        String sql = "";
        try {
            sql = ddlUtil.generateDDL(ddlVo);
        } catch (DataCenterException e) {
            e.printStackTrace();
        }

        assertEquals("CREATE TABLE Test( field1 int(10) NOT NULL AUTO_INCREMENT COMMENT 'comment for field1' , field2" +
                " int(20) NOT NULL PRIMARY KEY COMMENT 'comment for field2' , field2 varchar(20) NULL COMMENT " +
                "'comment for field2' ) default character set = 'utf8' COMMENT 'comment for table test'", sql);
    }

    @Test
    public void dropDDL() {
        DataStorageDDLVo ddlVo = new DataStorageDDLVo();
        //create table
        ddlVo.setDdlType("D");
        //表名，test
        DataObject object = new DataObject();
        object.setDefined("Test");
        ddlVo.setDataObject(object);

        String sql = "";
        try {
            sql = ddlUtil.generateDDL(ddlVo);
        } catch (DataCenterException e) {
            e.printStackTrace();
        }
        assertEquals("DROP TABLE Test", sql);
    }

    @Test
    public void alterDDL(){
        DataStorageDDLVo ddlVo = new DataStorageDDLVo();
        //create table
        ddlVo.setDdlType("U");
        //表名，test
        DataObject object = new DataObject();
        object.setDefined("Test");
        ddlVo.setDataObject(object);

        /*
        测试没有主键的情况
         */
        //增加字段
        List<DataObjectAttribute> addAttributes = new ArrayList<>();
        DataObjectAttribute attribute = new DataObjectAttribute();
        attribute.setColumnName("field1");
        attribute.setJdbcType("int");
        attribute.setLength(20);
        attribute.setDescription("add column field1");
        addAttributes.add(attribute);

        ddlVo.setAddColumns(addAttributes);
        //修改字段
        List<DataObjectAttribute> alterAttributes = new ArrayList<>();
        attribute = new DataObjectAttribute();
        attribute.setColumnName("field1");
        attribute.setJdbcType("int");
        attribute.setLength(20);
        attribute.setDescription("alter column field1");
        alterAttributes.add(attribute);
        ddlVo.setAlterColumns(alterAttributes);

        String sql = "";
        try {
            sql = ddlUtil.generateDDL(ddlVo);
        } catch (DataCenterException e) {
            e.printStackTrace();
        }

        assertEquals("ALTER TABLE Test ADD COLUMN field1 int(20) NOT NULL COMMENT 'add column field1' , CHANGE COLUMN" +
                        " field1 field1 int(20) NOT NULL COMMENT 'alter column field1' ", sql);

        /*
        测试有主键的情况
         */
        ddlVo.setPkInfo(new HashMap<>());
        try {
            sql = ddlUtil.generateDDL(ddlVo);
        } catch (DataCenterException e) {
            e.printStackTrace();
        }
        assertEquals("ALTER TABLE Test DROP PRIMARY KEY, ADD COLUMN field1 int(20) NOT NULL COMMENT 'add column " +
                "field1' , CHANGE COLUMN field1 field1 int(20) NOT NULL COMMENT 'alter column field1' ", sql);


    }
}