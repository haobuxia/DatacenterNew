package com.tianyi.datacenter.server.service.object;

import com.tianyi.datacenter.common.exception.DataCenterException;
import com.tianyi.datacenter.resource.entity.DataObject;
import com.tianyi.datacenter.resource.service.DataObjectService;
import com.tianyi.datacenter.storage.vo.PageListVo;
import com.tianyi.datacenter.storage.vo.RequestVo;
import com.tianyi.datacenter.storage.vo.ResponseVo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by wenxinyan on 2018/11/21.
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class DataObjectServiceManualTest {

    @Autowired
    private DataObjectService dataObjectService;

    @Test
    public void insertTest(){
        DataObject dataObject = new DataObject();

        dataObject.setName("test");
        dataObject.setType("duixiang");
        dataObject.setIsDic("true");
        dataObject.setDefined("test");
        dataObject.setDescription("test insert");

        System.out.println(dataObjectService.insert(dataObject));
    }

    @Test
    public void deleteTest(){
        System.out.println(dataObjectService.delete(2));
    }

    @Test
    public void updateTest(){
        DataObject dataObject = new DataObject();

        dataObject.setId(1);
        dataObject.setName("省份");
        dataObject.setType("对象");
        dataObject.setIsDic("true");
        dataObject.setDefined("data_center_province");
        dataObject.setDescription("test update");

        System.out.println(dataObjectService.update(dataObject));
    }

    @Test
    public void listTest(){
        Map<String, Object> map = new HashMap<>();
        map.put("type", "对象");
        map.put("isDic", "true");
        map.put("name", "省");

        RequestVo<Map> requestVo = new RequestVo<>(map);

        PageListVo pageListVo = new PageListVo(1);
        requestVo.setPageInfo(pageListVo);

        ResponseVo responseVo = ResponseVo.fail("查询对象失败！");

        try {
            responseVo = dataObjectService.list(requestVo);
        } catch (DataCenterException e) {
            e.printStackTrace();
        }

        System.out.println(responseVo.isSuccess());
        System.out.println(responseVo.getData().get("pageInfo"));
        System.out.println(responseVo.getData().get("list"));
    }

    @Test
    public void testGet(){
        DataObject dataObject = dataObjectService.getById(12);

        System.out.println(dataObject.getName());
    }

    @Test
    public void listDwf() {
        assertNotNull(dataObjectService);

        ResponseVo responseVo = new ResponseVo();
        try {
            assertTrue(dataObjectService.listDwf().size() == 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
