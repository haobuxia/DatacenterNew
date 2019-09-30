package com.tianyi.datacenter.server.service.object;

import com.tianyi.datacenter.common.exception.DataCenterException;
import com.tianyi.datacenter.resource.entity.DataObjectAttribute;
import com.tianyi.datacenter.resource.service.DataObjectAttributeService;
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

/**
 * Created by wenxinyan on 2018/11/21.
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class DataObjectAttributeServiceManualTest {

    @Autowired
    private DataObjectAttributeService dataObjectAttributeService;

    @Test
    public void insertTest(){
        DataObjectAttribute dataObjectAttribute = new DataObjectAttribute();
        dataObjectAttribute.setResId(2);
        dataObjectAttribute.setColumnName("test");
        dataObjectAttribute.setDicRes(33);
        dataObjectAttribute.setJdbcType("test");
        dataObjectAttribute.setDescription("test");
        dataObjectAttribute.setLength(50);
        dataObjectAttribute.setColumnName("test");
        dataObjectAttribute.setRule("test");
        dataObjectAttribute.setType("test");
        dataObjectAttribute.setIsKey("no");
        dataObjectAttribute.setIsNull("not null");
        dataObjectAttribute.setIsIncrement("no");
        dataObjectAttribute.setIndexType("index");

        System.out.println(dataObjectAttributeService.insert(dataObjectAttribute));
    }

    @Test
    public void deleteTest(){
        Map<String, Object> map = new HashMap<>();
        map.put("resId", 2);

        System.out.println(dataObjectAttributeService.delete(map));
    }

    @Test
    public void listTest(){
        Map<String, Object> map = new HashMap<>();
        map.put("resId", 1);

        RequestVo<Map> requestVo = new RequestVo<>(map);

        PageListVo pageListVo = new PageListVo(1);
        requestVo.setPageInfo(pageListVo);

        ResponseVo responseVo = ResponseVo.fail("查询对象属性失败！");

        try {
            responseVo = dataObjectAttributeService.list(requestVo);
        } catch (DataCenterException e) {
            e.printStackTrace();
        }

        System.out.println(responseVo.isSuccess());
        System.out.println(responseVo.getData().get("pageInfo"));
        System.out.println(responseVo.getData().get("list"));
    }

}
