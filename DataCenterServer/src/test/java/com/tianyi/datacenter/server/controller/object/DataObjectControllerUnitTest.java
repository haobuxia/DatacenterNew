package com.tianyi.datacenter.server.controller.object;

import com.alibaba.fastjson.JSONObject;
import com.tianyi.datacenter.common.exception.DataCenterException;
import com.tianyi.datacenter.resource.controller.DataObjectController;
import com.tianyi.datacenter.resource.entity.DataObject;
import com.tianyi.datacenter.resource.service.DataObjectAttributeService;
import com.tianyi.datacenter.resource.service.DataObjectService;
import com.tianyi.datacenter.storage.service.DataStorageDDLService;
import com.tianyi.datacenter.storage.vo.DataStorageDDLVo;
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
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.when;

/**
 * 数据对象接口单元测试
 *
 * @author wenxinyan
 * @version 0.1
 */
public class DataObjectControllerUnitTest {

    @InjectMocks
    DataObjectController dataObjectController = new DataObjectController();

    @Mock
    private DataObjectService dataObjectService;

    @Mock
    private DataObjectAttributeService dataObjectAttributeService;

    @Mock
    private DataStorageDDLService dataStorageDDLService;

    @Before
    public void setUp() throws DataCenterException {
        List<DataObject> dataObjectList = new ArrayList<>();
        RequestVo<DataStorageDDLVo> requestVo = new RequestVo<>(new DataStorageDDLVo());
        MockitoAnnotations.initMocks(this);
        when(dataObjectService.listNoPage(anyMap())).thenReturn(dataObjectList);
        when(dataObjectService.getById(anyInt())).thenReturn(new DataObject());
        when(dataObjectAttributeService.delete(anyMap())).thenReturn(anyInt());
        when(dataStorageDDLService.doServer(requestVo)).thenReturn(ResponseVo.success());
    }

    @Test
    public void addDataObject() {
        DataObject dataObject = new DataObject();
        assertTrue(dataObjectController.add(dataObject).isSuccess());
    }

    @Test
    public void deleteDataObject() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", 1);
        assertTrue(dataObjectController.delete(jsonObject).isSuccess());
    }

    @Test
    public void updateDataObject() {
        DataObject dataObject = new DataObject();
        assertTrue(dataObjectController.update(dataObject).isSuccess());
    }

    @Test
    public void listDataObject() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", "test");
        jsonObject.put("isDic", "false");
        jsonObject.put("name", "test");

        Map<String, Object> pageInfo = new HashMap<>();
        pageInfo.put("page", 0);
        pageInfo.put("pageSize", 0);
        jsonObject.put("pageInfo", pageInfo);

        assertTrue(dataObjectController.list(jsonObject).isSuccess());
    }

    @Test
    public void bindDicAttribute() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("dataObjectId", 1);
        jsonObject.put("keyName", "test");
        jsonObject.put("valueName", "test");
        assertTrue(dataObjectController.binding(jsonObject).isSuccess());
    }

}
