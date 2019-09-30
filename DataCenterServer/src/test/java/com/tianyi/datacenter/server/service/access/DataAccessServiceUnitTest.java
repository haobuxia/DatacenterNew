package com.tianyi.datacenter.server.service.access;

import com.tianyi.datacenter.access.service.DataAccessService;
import com.tianyi.datacenter.access.service.impl.DataAccessServiceImpl;
import com.tianyi.datacenter.resource.entity.DataObject;
import com.tianyi.datacenter.resource.entity.DataObjectAttribute;
import com.tianyi.datacenter.storage.vo.DataStorageDMLVo;
import com.tianyi.datacenter.storage.vo.RequestVo;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by wenxinyan on 2018/11/21.
 */
public class DataAccessServiceUnitTest {

    private DataAccessService dataAccessService = new DataAccessServiceImpl();

    @Test
    public void createBusinessData() {
//        DataAccessService dataAccessService = new DataAccessServiceImpl();

        //新增数据
        List<DataObjectAttribute> updateAttributes = new ArrayList<>();
        DataObjectAttribute attribute = new DataObjectAttribute();
        attribute.setName("updateField1");
        attribute.setValue("value1");
        attribute.setType("text");
        updateAttributes.add(attribute);
        attribute = new DataObjectAttribute();
        attribute.setName("updateField2");
        attribute.setValue("value2");
        attribute.setType("text");
        updateAttributes.add(attribute);

        List<DataObjectAttribute> conditionAttributes = new ArrayList<>();
        attribute = new DataObjectAttribute();
        attribute.setName("conditionField1");
        attribute.setValue("value3");
        attribute.setType("text");
        conditionAttributes.add(attribute);
        attribute = new DataObjectAttribute();
        attribute.setName("conditionField2");
        attribute.setValue("value4");
        attribute.setType("text");
        conditionAttributes.add(attribute);

        DataObject dataObject = new DataObject();

        RequestVo<DataStorageDMLVo> requestVo = dataAccessService.integrateData(dataObject, "C", 0, 0,
                conditionAttributes, updateAttributes, null);


        assertEquals(requestVo.getRequest().getAttributes(), updateAttributes);
        assertEquals(requestVo.getRequest().getCondition(), conditionAttributes);
    }

}
