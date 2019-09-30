package com.tianyi.datacenter.server.service.object;

import com.tianyi.datacenter.common.exception.DataCenterException;
import com.tianyi.datacenter.resource.dao.DataObjectDao;
import com.tianyi.datacenter.resource.entity.DataObject;
import com.tianyi.datacenter.resource.service.DataObjectService;
import com.tianyi.datacenter.resource.service.impl.DataObjectServiceImpl;
import com.tianyi.datacenter.storage.vo.PageListVo;
import com.tianyi.datacenter.storage.vo.RequestVo;
import com.tianyi.datacenter.storage.vo.ResponseVo;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * 数据对象服务单元测试
 *
 * @author wenxinyan
 * @version 0.1
 */
public class DataObjectServiceUnitTest {

    @InjectMocks
    private DataObjectService dataObjectService = new DataObjectServiceImpl();

    @Mock
    private DataObjectDao dataObjectDao;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void addDataObject() {
        assertNotNull(dataObjectService);

        DataObject dataObject = new DataObject();
        dataObject.setName("test");
        dataObject.setType("test");
        dataObject.setDefined("test");
        dataObject.setIsDic("false");
        dataObject.setDescription("test");

        assertTrue(dataObjectService.insert(dataObject) == 0);
    }

    @Test
    public void deleteDataObject() {
        assertNotNull(dataObjectService);

        int id = 1;

        assertTrue(dataObjectService.delete(id) == 0);
    }

    @Test
    public void updateDataObject() {
        assertNotNull(dataObjectService);

        DataObject dataObject = new DataObject();
        dataObject.setId(1);
        dataObject.setName("test");
        dataObject.setType("test");
        dataObject.setDefined("test");
        dataObject.setIsDic("false");
        dataObject.setDescription("test");

        assertTrue(dataObjectService.update(dataObject) == 0);
    }

    @Test
    public void listDataObject() {
        assertNotNull(dataObjectService);

        Map<String, Object> map = new HashMap<>();
        PageListVo pageListVo = new PageListVo(1, 20);
        RequestVo requestVo =  new RequestVo(map);
        requestVo.setPageInfo(pageListVo);

        ResponseVo responseVo = new ResponseVo();
        try {
            responseVo = dataObjectService.list(requestVo);
        } catch (DataCenterException e) {
            e.printStackTrace();
        }

        assertTrue(responseVo.isSuccess());
    }

    @Test
    public void getDataObject() {
        assertNotNull(dataObjectService);

        int id = 1;

        assertTrue(dataObjectService.getById(id) == null);
    }

    @Test
    public void listNoPageDataObject() {
        assertNotNull(dataObjectService);

        Map<String, Object> map = new HashMap<>();

        assertTrue(dataObjectService.listNoPage(map).size() == 0);
    }

}
