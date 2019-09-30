package com.tianyi.datacenter.storage.factory;

import com.tianyi.datacenter.storage.entity.DdlTypeEnum;
import com.tianyi.datacenter.storage.service.DataStorageDDLService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * Created by tianxujin on 2019/3/29 16:00
 */
@Component
public class DataStorageDDLFactory {

    @Autowired
    @Qualifier("tableDDLService")
    private DataStorageDDLService tableDDLService;

    @Autowired
    @Qualifier("columnDDLService")
    private DataStorageDDLService columnDDLService;
    /**
     * 获得类型对应service实例
     *
     * @param type
     * @return
     */
    public DataStorageDDLService getDDLService(DdlTypeEnum type) {
        switch (type) {
            case table:
                return tableDDLService;
            case column:
                return columnDDLService;
            default:
                return null;
        }
    }
}
