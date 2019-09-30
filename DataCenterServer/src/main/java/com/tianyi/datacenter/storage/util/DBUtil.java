package com.tianyi.datacenter.storage.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 执行数据库相关操作，获取数据库相关信息工具方法
 *
 * @author zhouwei
 * 2018/11/22 10:10
 * @version 0.1
 **/
@Component
public class DBUtil {

    @Autowired
    @Qualifier("businessJdbcTemplate")
    private JdbcTemplate jdbcTemplate;

    /**
     * 执行DDL语句，失败抛出异常
     * @author zhouwei
     * 2018/11/22 10:11
     * @param operType 操作类型
     * @param sql 执行sql
     * @exception RuntimeException 执行ddl语句失败
    */
    public void executeDDL(String operType, String sql) throws RuntimeException{
        if ("C".equals(operType)) {
            //创建表语句
            createTable(sql);
        } else if ("U".equals(operType)) {
            //修改表语句
            alterTable(sql);
        } else if ("R".equals(operType)) {
            //修改表名语句
            alterTable(sql);
        }
    }

    /**
     * 执行dml语句，查询除外
     * @author zhouwei
     * 2018/11/28 10:05
     * @param operType 操作类型，C-新增；U-更新；D-删除
     * @return 执行影响条数int
    */
    public int executeDML(String operType, String sql){
        int rst = 0;
        switch (operType) {
            case "C":
                rst = update(sql);
                break;
            case "U":
                rst = update(sql);
                break;
            case "D":
                rst = update(sql);
                break;
        }
        return rst;
    }

    /**
     * 执行查询sql
     * @author zhouwei
     * 2018/11/28 10:07
     * @param sql 查询sql语句
     * @return 查询结果List
    */
    public List<Map<String, Object>> executeQuery(String sql){
        return queryForList(sql);
    }

    private void createTable(String sql) throws RuntimeException{
        jdbcTemplate.update(sql);
    }

    private void alterTable(String sql) throws RuntimeException{
        jdbcTemplate.update(sql);
    }

    private int update(String sql) {
        return jdbcTemplate.update(sql);
    }

    private List<Map<String, Object>> queryForList(String sql) {
        return jdbcTemplate.queryForList(sql);
    }

    /**
     * 获取指定表的字段信息
     * @author zhouwei
     * 2018/11/28 19:47
     * @param tableName 指定表名
     * @return 表的所有字段信息
    */
    public Map<String, Map<String, Object>> getColumnsInfo(String tableName) throws SQLException {
        //获取schema信息（字段信息）
        DatabaseMetaData metaData = jdbcTemplate.getDataSource().getConnection().getMetaData();
        ResultSet rs = metaData.getColumns(null, null, tableName, null);
        //组装字段属性并返回
        Map<String, Map<String, Object>> columnsInfo = new HashMap<>();
        while (rs.next()) {
            Map<String, Object> tmp = new HashMap<>();
            tmp.put("TABLE_CAT", rs.getObject("TABLE_CAT"));
            tmp.put("TABLE_SCHEM", rs.getObject("TABLE_SCHEM"));
            tmp.put("TABLE_NAME", rs.getObject("TABLE_NAME"));
            tmp.put("COLUMN_NAME", rs.getObject("COLUMN_NAME"));
            tmp.put("DATA_TYPE", rs.getObject("DATA_TYPE"));
            tmp.put("TYPE_NAME", rs.getObject("TYPE_NAME"));
            tmp.put("COLUMN_SIZE", rs.getObject("COLUMN_SIZE"));
            tmp.put("BUFFER_LENGTH", rs.getObject("BUFFER_LENGTH"));
            tmp.put("DECIMAL_DIGITS", rs.getObject("DECIMAL_DIGITS"));
            tmp.put("NUM_PREC_RADIX", rs.getObject("NUM_PREC_RADIX"));
            tmp.put("NULLABLE", rs.getObject("NULLABLE"));
            tmp.put("REMARKS", rs.getObject("REMARKS"));
            tmp.put("COLUMN_DEF", rs.getObject("COLUMN_DEF"));
            tmp.put("SQL_DATA_TYPE", rs.getObject("SQL_DATA_TYPE"));
            tmp.put("SQL_DATETIME_SUB", rs.getObject("SQL_DATETIME_SUB"));
            tmp.put("CHAR_OCTET_LENGTH", rs.getObject("CHAR_OCTET_LENGTH"));
            tmp.put("ORDINAL_POSITION", rs.getObject("ORDINAL_POSITION"));
            tmp.put("IS_NULLABLE", rs.getObject("IS_NULLABLE"));
            tmp.put("SCOPE_CATALOG", rs.getObject("SCOPE_CATALOG"));
            tmp.put("SCOPE_SCHEMA", rs.getObject("SCOPE_SCHEMA"));
            tmp.put("SCOPE_TABLE", rs.getObject("SCOPE_TABLE"));
            tmp.put("SOURCE_DATA_TYPE", rs.getObject("SOURCE_DATA_TYPE"));
            tmp.put("IS_AUTOINCREMENT", rs.getObject("IS_AUTOINCREMENT"));
            tmp.put("IS_GENERATEDCOLUMN", rs.getObject("IS_GENERATEDCOLUMN"));
            columnsInfo.put(rs.getString("COLUMN_NAME").toUpperCase(), tmp);
        }
        return columnsInfo;

    }

    /**
     * 获取指定表的主键信息
     * @author zhouwei
     * 2018/11/30 23:40
     * @param table 表名
     * @return 返回主键信息
    */
    public Map<String, Object> getPrimaryKey(String table) throws SQLException {
        //获取schema信息（字段信息）
        DatabaseMetaData metaData;
        metaData = jdbcTemplate.getDataSource().getConnection().getMetaData();
        ResultSet rs = metaData.getPrimaryKeys(null, null, table);
        //组装主键信息并返回
        Map<String, Object> pkInfo = null;
        while (rs.next()) {
            pkInfo = new HashMap<>();
            pkInfo.put("TABLE_CAT", rs.getObject("TABLE_CAT"));
            pkInfo.put("TABLE_SCHEM", rs.getObject("TABLE_SCHEM"));
            pkInfo.put("TABLE_NAME", rs.getObject("TABLE_NAME"));
            pkInfo.put("COLUMN_NAME", rs.getObject("COLUMN_NAME"));
            pkInfo.put("KEY_SEQ", rs.getObject("KEY_SEQ"));
            pkInfo.put("PK_NAME", rs.getObject("PK_NAME"));
        }
        return pkInfo;
    }
}
