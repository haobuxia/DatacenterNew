package com.tianyi.code.server.entity;

/**
 * 数据库字段封装类
 * Created by Ay on 2017/5/3.
 */
public class ColumnClass {

    /**
     * 数据库字段名称
     **/
    private String columnName;
    /**
     * 数据库字段类型
     **/
    private String columnType;
    /**
     * 数据库字段首字母小写且去掉下划线字符串
     **/
    private String changeColumnName;

    /**
     * 首字母大写 主要应用到gett 和sett方法中
     **/
    private String firstUpcaseName;

    /**
     * 数据库字段注释
     **/
    private String columnComment;

    private String type;

    private String dicRes;

    private Integer resId;

    private String isNull;

    public String getIsNull() {
        return isNull;
    }

    public void setIsNull(String isNull) {
        this.isNull = isNull;
    }

    public Integer getResId() {
        return resId;
    }

    public void setResId(Integer resId) {
        this.resId = resId;
    }

    public String getDicRes() {
        return dicRes;
    }

    public void setDicRes(String dicRes) {
        this.dicRes = dicRes;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getColumnComment() {
        return columnComment;
    }

    public void setColumnComment(String columnComment) {
        this.columnComment = columnComment;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getColumnType() {
        return columnType;
    }

    public void setColumnType(String columnType) {
        this.columnType = columnType;
    }

    public String getChangeColumnName() {
        return changeColumnName;
    }

    public void setChangeColumnName(String changeColumnName) {
        this.changeColumnName = changeColumnName;
    }

    public String getFirstUpcaseName() {

        return firstUpcaseName;
    }

    public void setFirstUpcaseName(String firstUpcaseName) {

        this.firstUpcaseName = firstUpcaseName;
    }
}