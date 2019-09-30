package com.tianyi.datacenter.server.service.storage.impl;

import com.tianyi.datacenter.common.exception.DataCenterException;
import com.tianyi.datacenter.resource.entity.DataObject;
import com.tianyi.datacenter.resource.entity.DataObjectAttribute;
import com.tianyi.datacenter.storage.service.DataStorageDMLService;
import com.tianyi.datacenter.storage.service.impl.DataStorageDMLServiceImpl;
import com.tianyi.datacenter.storage.util.DBUtil;
import com.tianyi.datacenter.storage.util.DMLUtil;
import com.tianyi.datacenter.storage.vo.DataStorageDMLVo;
import com.tianyi.datacenter.storage.vo.RequestVo;
import com.tianyi.datacenter.storage.vo.ResponseVo;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class DataStorageDMLServiceUnitTest {
    @InjectMocks
    private DataStorageDMLService dataStorageDMLService = new DataStorageDMLServiceImpl();
    @Mock
    private DBUtil dbUtil;
    @Mock
    private DMLUtil dmlUtil;

    @Before
    public void setUp() throws DataCenterException {
        MockitoAnnotations.initMocks(this);


        when(dmlUtil.generateDML(any(DataStorageDMLVo.class), any(), null)).thenReturn("");
        when(dbUtil.executeDML(anyString(), anyString())).thenReturn(1);
    }
    @Test
    public void selectDMLService() {

        DataStorageDMLVo vo = new DataStorageDMLVo();
        //操作 查询
        vo.setDmlType("R");
        //表信息
        DataObject object = new DataObject();
        object.setDefined("test");
        vo.setDataObject(object);

        RequestVo<DataStorageDMLVo> req = new RequestVo(vo);

        ResponseVo resp = null;
        try {
            resp = dataStorageDMLService.doServer(req);
        } catch (DataCenterException e) {
            e.printStackTrace();
        }

        assertFalse(resp.isSuccess());
        assertEquals("没有查询到数据", resp.getMessage());
    }

    @Test
    public void errDataSetNotRetrieve() throws DataCenterException {
        DataStorageDMLVo dmlVo = new DataStorageDMLVo();
        //操作类型
        dmlVo.setDmlType("C");

        //表信息
        DataObject dataObject = new DataObject();
        dataObject.setName("test1");
        dataObject.setType("数据集");
        dmlVo.setDataObject(dataObject);

        RequestVo<DataStorageDMLVo> requestVo = new RequestVo<>(dmlVo);
        //调用dml服务
        ResponseVo responseVo = dataStorageDMLService.doServer(requestVo);
        assertFalse(responseVo.isSuccess());
        assertEquals("操作失败,数据集只能做查询操作", responseVo.getMessage());
    }

    @Test
    public void insertDMLService() throws DataCenterException {
        DataStorageDMLVo dmlVo = new DataStorageDMLVo();
        //操作类型
        dmlVo.setDmlType("C");
        //新增字段
        List<DataObjectAttribute> attributes = new ArrayList<>();
        DataObjectAttribute attribute = new DataObjectAttribute();
        attribute.setColumnName("field1");
        attribute.setType("text");
        attributes.add(attribute);
        //表信息
        DataObject dataObject = new DataObject();
        dataObject.setName("test1");
        //组装请求信息
        dmlVo.setDataObject(dataObject);
        dmlVo.setAttributes(attributes);
        RequestVo<DataStorageDMLVo> requestVo = new RequestVo<>(dmlVo);
        //调用dml服务
        ResponseVo responseVo = dataStorageDMLService.doServer(requestVo);
        assertTrue(responseVo.isSuccess());
    }
}