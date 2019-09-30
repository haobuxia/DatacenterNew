package com.tianyi.datacenter.storage.entity;

import java.util.Arrays;

/**
 * DDL类型枚举
 * <p>
 * Created by tianxujin on 2019/03/29.
 */
public enum DdlTypeEnum {
    table(1,"table"),
    column(2,"column");

    int id;
    String name;

    private DdlTypeEnum(int id, String name){
        this.id = id;
        this.name = name;
    }

    public static DdlTypeEnum get(String name){
        return Arrays.stream(DdlTypeEnum.values()).filter(typeEnum -> typeEnum.getName().equals(name)).findAny().orElse(null);
    }

    public static DdlTypeEnum get(int id){
       return Arrays.stream(DdlTypeEnum.values()).filter(typeEnum -> typeEnum.getId() == id).findAny().orElse(null);
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
