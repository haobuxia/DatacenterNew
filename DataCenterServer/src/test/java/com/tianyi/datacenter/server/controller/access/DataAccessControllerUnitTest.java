package com.tianyi.datacenter.server.controller.access;

import com.alibaba.fastjson.JSONObject;
import com.tianyi.datacenter.access.controller.DataAccessController;
import com.tianyi.datacenter.access.service.DataAccessService;
import com.tianyi.datacenter.common.exception.DataCenterException;
import com.tianyi.datacenter.resource.entity.DataObjectAttribute;
import com.tianyi.datacenter.resource.service.DataObjectAttributeService;
import com.tianyi.datacenter.resource.service.DataObjectService;
import com.tianyi.datacenter.storage.service.DataStorageDMLService;
import com.tianyi.datacenter.storage.vo.DataStorageDMLVo;
import com.tianyi.datacenter.storage.vo.RequestVo;
import com.tianyi.datacenter.storage.vo.ResponseVo;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.anyMap;
import static org.mockito.Mockito.when;


public class DataAccessControllerUnitTest {

    @InjectMocks
    DataAccessController dataAccessController = new DataAccessController();

    @Mock
    private DataAccessService dataAccessService;

    @Mock
    private DataStorageDMLService dataStorageDMLService;

    @Mock
    private DataObjectService dataObjectService;

    @Mock
    private DataObjectAttributeService dataObjectAttributeService;

    @Before
    public void setUp() throws DataCenterException {
        List<DataObjectAttribute> insertAttributes = new ArrayList<>();
        RequestVo<DataStorageDMLVo> dmlVoRequestVo = new RequestVo<>(new DataStorageDMLVo());
        MockitoAnnotations.initMocks(this);
        when(dataAccessService.integrateData(null, "C", 0, 0,
                null, insertAttributes, null)).thenReturn(dmlVoRequestVo);
        when(dataAccessService.integrateData(null, "D", 0, 0,
                insertAttributes, null, null)).thenReturn(dmlVoRequestVo);
        when(dataAccessService.integrateData(null, "U", 0, 0,
                insertAttributes, insertAttributes, null)).thenReturn(dmlVoRequestVo);
        when(dataAccessService.integrateData(null, "R", 0, 0,
                insertAttributes, null, null)).thenReturn(dmlVoRequestVo);
        when(dataObjectService.getById(1)).thenReturn(null);
        when(dataObjectAttributeService.listNoPage(anyMap())).thenReturn(insertAttributes);
        when(dataStorageDMLService.doServer(dmlVoRequestVo)).thenReturn(ResponseVo.success());
    }

    @Test
    public void addBusinessData() {

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("dataObjectId", 1);
        jsonObject.put("data", new HashMap<>());
        ResponseVo responseVo = dataAccessController.add(jsonObject);
        assertTrue(responseVo.isSuccess());
    }

    @Test
    public void deleteBusinessData() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("dataObjectId", 1);

        List<Map<String, Object>> condition = new ArrayList<>();
        jsonObject.put("condition", condition);

        assertTrue(dataAccessController.delete(jsonObject).isSuccess());
    }

    @Test
    public void updateBusinessData() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("dataObjectId", 1);
        jsonObject.put("data", new HashMap<>());

        List<Map<String, Object>> condition = new ArrayList<>();
        jsonObject.put("condition", condition);

        assertTrue(dataAccessController.update(jsonObject).isSuccess());
    }

    @Test
    public void retrieveBusinessData() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("dataObjectId", 1);

        List<Map<String, Object>> condition = new ArrayList<>();
        jsonObject.put("condition", condition);

        Map<String, Object> pageInfo = new HashMap<>();
        pageInfo.put("page", 0);
        pageInfo.put("pageSize", 0);
        jsonObject.put("pageInfo", pageInfo);

        assertTrue(dataAccessController.retrieve(jsonObject).isSuccess());
    }

}