package com.tianyi.datacenter.server.service.storage.util;

import com.tianyi.datacenter.storage.util.DBUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.sql.SQLException;
import java.util.List;

import static org.junit.Assert.*;

@SpringBootTest
@RunWith(SpringRunner.class)
public class DBUtilManualTest {

    @Autowired
    private DBUtil database;

    @Test
    public void executeCreateDDL() throws Exception {
        assertNotNull(database);
        database.executeDDL("C", "CREATE TABLE Test( field1 int (10)COMMENT 'comment for column field1' ,  " +
                "field2 " +
                "int (20)COMMENT 'comment for column field2' ) default character set = 'utf8' COMMENT 'comment for " +
                "table test'");

        assertTrue(true);
    }

    @Test
    public void executeDDL() {

        assertNotNull(database);
        List rtn = database.executeQuery("select 1");
        assertEquals(rtn.size(), 1);
    }

    @Test
    public void getColumnsInfo() throws SQLException {
        database.getColumnsInfo("data_center_attr");
    }

}