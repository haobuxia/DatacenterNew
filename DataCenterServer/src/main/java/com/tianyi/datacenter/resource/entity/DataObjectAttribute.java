package com.tianyi.datacenter.resource.entity;

/**
 * 数据对象属性
 *
 * @author wenxinyan
 * @version 0.1
 */
public class DataObjectAttribute {
    private int id;
    private int resId;
    private String columnName;
    private String jdbcType;
    private int length;
    private String name;
    private String description;
    private String type;
    private int dicRes;
    private int dicKey;
    private int dicValue;
    private String rule;
    private String isKey;
    private String isNull;
    private String isIncrement;
    private String indexType;
    private String value; // 不写入数据库，生成查询条件用
    private String oper; // 不写入数据库，生成查询条件用

    private String oldColumnName;// 不写入数据库，更新列名用

    private String dicResName;
    private String dicResColumnName;
    private String dicKeyColumnName;
    private String dicValueColumnName;
    private String dicKeyName;
    private String dicValueName;

    public String getDicResName() {
        return dicResName;
    }

    public void setDicResName(String dicResName) {
        this.dicResName = dicResName;
    }

    public String getDicResColumnName() {
        return dicResColumnName;
    }

    public void setDicResColumnName(String dicResColumnName) {
        this.dicResColumnName = dicResColumnName;
    }

    public String getDicKeyColumnName() {
        return dicKeyColumnName;
    }

    public void setDicKeyColumnName(String dicKeyColumnName) {
        this.dicKeyColumnName = dicKeyColumnName;
    }

    public String getDicValueColumnName() {
        return dicValueColumnName;
    }

    public void setDicValueColumnName(String dicValueColumnName) {
        this.dicValueColumnName = dicValueColumnName;
    }

    public String getDicKeyName() {
        return dicKeyName;
    }

    public void setDicKeyName(String dicKeyName) {
        this.dicKeyName = dicKeyName;
    }

    public String getDicValueName() {
        return dicValueName;
    }

    public void setDicValueName(String dicValueName) {
        this.dicValueName = dicValueName;
    }

    public String getOldColumnName() {
        return oldColumnName;
    }

    public void setOldColumnName(String oldColumnName) {
        this.oldColumnName = oldColumnName;
    }

    public String getIndexType() {
        return indexType;
    }

    public void setIndexType(String indexType) {
        this.indexType = indexType;
    }

    public String getIsKey() {
        return isKey;
    }

    public void setIsKey(String isKey) {
        this.isKey = isKey;
    }

    public String getIsNull() {
        return isNull;
    }

    public void setIsNull(String isNull) {
        this.isNull = isNull;
    }

    public String getIsIncrement() {
        return isIncrement;
    }

    public void setIsIncrement(String isIncrement) {
        this.isIncrement = isIncrement;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getResId() {
        return resId;
    }

    public void setResId(int resId) {
        this.resId = resId;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getJdbcType() {
        return jdbcType;
    }

    public void setJdbcType(String jdbcType) {
        this.jdbcType = jdbcType;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getDicRes() {
        return dicRes;
    }

    public void setDicRes(int dicRes) {
        this.dicRes = dicRes;
    }

    public int getDicKey() {
        return dicKey;
    }

    public void setDicKey(int dicKey) {
        this.dicKey = dicKey;
    }

    public int getDicValue() {
        return dicValue;
    }

    public void setDicValue(int dicValue) {
        this.dicValue = dicValue;
    }

    public String getRule() {
        return rule;
    }

    public void setRule(String rule) {
        this.rule = rule;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public String getOper() {
        return oper;
    }

    public void setOper(String oper) {
        this.oper = oper;
    }
}
