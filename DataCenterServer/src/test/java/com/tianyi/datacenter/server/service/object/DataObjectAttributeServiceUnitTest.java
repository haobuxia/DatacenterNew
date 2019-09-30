package com.tianyi.datacenter.server.service.object;

import com.tianyi.datacenter.common.exception.DataCenterException;
import com.tianyi.datacenter.resource.dao.DataObjectAttributeDao;
import com.tianyi.datacenter.resource.entity.DataObjectAttribute;
import com.tianyi.datacenter.resource.service.DataObjectAttributeService;
import com.tianyi.datacenter.resource.service.impl.DataObjectAttributeServiceImpl;
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
 * 数据对象属性服务单元测试
 *
 * @author wenxinyan
 * @version 0.1
 */
public class DataObjectAttributeServiceUnitTest {

    @InjectMocks
    private DataObjectAttributeService dataObjectAttributeService = new DataObjectAttributeServiceImpl();

    @Mock
    private DataObjectAttributeDao dataObjectAttributeDao;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void addAttribute() {
        assertNotNull(dataObjectAttributeService);

        DataObjectAttribute dataObjectAttribute = new DataObjectAttribute();

        assertTrue(dataObjectAttributeService.insert(dataObjectAttribute) == 0);
    }

    @Test
    public void deleteAttribute() {
        assertNotNull(dataObjectAttributeService);

        Map<String, Object> map = new HashMap<>();

        assertTrue(dataObjectAttributeService.delete(map) == 0);
    }

    @Test
    public void updateAttribute() {
        assertNotNull(dataObjectAttributeService);

        DataObjectAttribute dataObjectAttribute = new DataObjectAttribute();

        assertTrue(dataObjectAttributeService.update(dataObjectAttribute) == 0);
    }

    @Test
    public void listAttribute() {
        assertNotNull(dataObjectAttributeService);

        Map<String, Object> map = new HashMap<>();
        PageListVo pageListVo = new PageListVo(1, 20);
        RequestVo requestVo =  new RequestVo(map);
        requestVo.setPageInfo(pageListVo);

        ResponseVo responseVo = new ResponseVo();
        try {
            responseVo = dataObjectAttributeService.list(requestVo);
        } catch (DataCenterException e) {
            e.printStackTrace();
        }

        assertTrue(responseVo.isSuccess());
    }

    @Test
    public void getAttribute() {
        assertNotNull(dataObjectAttributeService);

        int id = 1;

        assertTrue(dataObjectAttributeService.getById(id) == null);
    }

    @Test
    public void listNoPageAttribute() {
        assertNotNull(dataObjectAttributeService);

        Map<String, Object> map = new HashMap<>();

        assertTrue(dataObjectAttributeService.listNoPage(map).size() == 0);
    }

}
