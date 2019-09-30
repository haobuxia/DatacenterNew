package com.tianyi.datacenter.server.service.resource;


import com.tianyi.datacenter.resource.service.ResourceService;
import com.tianyi.datacenter.storage.vo.PageListVo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created by xiayuan on 2018/11/22.
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class ResourceControllerIntegrateTest {

    @Autowired
    private ResourceService resourceService;
    @Test
    public void getResource(){
        Integer dataObjectId = 1;
        String type = "对象";
        String isDic = "false";
        String keyword = "";
        PageListVo pageListVo = new PageListVo(1);
        System.out.println(resourceService.integrateData(dataObjectId,type,isDic,keyword,pageListVo));
    }
}
