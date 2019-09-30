package com.tianyi.datacenter.server.controller.object;

import com.alibaba.fastjson.JSONObject;
import com.tianyi.datacenter.common.exception.DataCenterException;
import com.tianyi.datacenter.resource.controller.DataObjectAttributeController;
import com.tianyi.datacenter.resource.entity.DataObject;
import com.tianyi.datacenter.resource.entity.DataObjectAttribute;
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
 * 数据对象属性接口单元测试
 *
 * @author wenxinyan
 * @version 0.1
 */
public class DataObjectAttributeControllerUnitTest {

    @InjectMocks
    DataObjectAttributeController dataObjectAttributeController = new DataObjectAttributeController();

    @Mock
    private DataObjectAttributeService dataObjectAttributeService;

    @Mock
    private DataObjectService dataObjectService;

    @Mock
    private DataStorageDDLService dataStorageDDLService;

    @Before
    public void setUp() throws DataCenterException {
        List<DataObjectAttribute> dataObjectAttributeList = new ArrayList<>();
        RequestVo<DataStorageDDLVo> requestVo = new RequestVo<>(new DataStorageDDLVo());
        MockitoAnnotations.initMocks(this);
        when(dataObjectAttributeService.listNoPage(anyMap())).thenReturn(dataObjectAttributeList);
        when(dataObjectAttributeService.getById(anyInt())).thenReturn(new DataObjectAttribute());
        when(dataObjectService.getById(anyInt())).thenReturn(new DataObject());
        when(dataStorageDDLService.doServer(requestVo)).thenReturn(ResponseVo.success());
    }

    @Test
    public void addAttribute() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("objectId", 1);

        List<Map<String, String>> requestList = new ArrayList<>();
        jsonObject.put("list", requestList);

        assertTrue(dataObjectAttributeController.add(jsonObject).isSuccess());
    }

    @Test
    public void deleteAttribute() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", 1);
        assertTrue(dataObjectAttributeController.delete(jsonObject).isSuccess());
    }

    @Test
    public void updateAttribute() {
        DataObjectAttribute dataObjectAttribute = new DataObjectAttribute();
        assertTrue(dataObjectAttributeController.update(dataObjectAttribute).isSuccess());
    }

    @Test
    public void listAttribute() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("resId", 1);

        Map<String, Object> pageInfo = new HashMap<>();
        pageInfo.put("page", 0);
        pageInfo.put("pageSize", 0);
        jsonObject.put("pageInfo", pageInfo);

        assertTrue(dataObjectAttributeController.list(jsonObject).isSuccess());
    }
}
