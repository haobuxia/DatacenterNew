package com.tianyi.datacenter.server.service.storage;

import com.tianyi.datacenter.common.exception.DataCenterException;
import com.tianyi.datacenter.resource.entity.DataObject;
import com.tianyi.datacenter.resource.entity.DataObjectAttribute;
import com.tianyi.datacenter.storage.service.DataStorageDDLService;
import com.tianyi.datacenter.storage.vo.DataStorageDDLVo;
import com.tianyi.datacenter.storage.vo.RequestVo;
import com.tianyi.datacenter.storage.vo.ResponseVo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;

@SpringBootTest
@RunWith(SpringRunner.class)
public class DataStorageDDLServiceManualTest {

    @Autowired
    private DataStorageDDLService dataStorageDDLService;

    @Test
    public void doServer() throws DataCenterException {
        DataStorageDDLVo ddlVo = new DataStorageDDLVo();
        //alter operation
        ddlVo.setDdlType("U");
        //table name info
        DataObject object = new DataObject();
        object.setDefined("testtable");
        ddlVo.setDataObject(object);
        //add column info
        List<DataObjectAttribute> attributes = new ArrayList<>();
        DataObjectAttribute attribute = new DataObjectAttribute();
        attribute.setColumnName("field8");
        attribute.setDescription("comment for field1");
        attribute.setJdbcType("int");
        attribute.setLength(10);
        attributes.add(attribute);
        //alter column info
        attribute = new DataObjectAttribute();
        attribute.setColumnName("field7");
        attribute.setDescription("comment for field1");
        attribute.setJdbcType("int");
        attribute.setLength(20);
        attributes.add(attribute);

        ddlVo.setAttributes(attributes);
        //build request
        RequestVo<DataStorageDDLVo> requestVo = new RequestVo<>(ddlVo);
        //call ddl alter table add column service
        ResponseVo responseVo = dataStorageDDLService.doServer(requestVo);
        assertTrue(responseVo.isSuccess());
    }
}