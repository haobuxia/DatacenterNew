package com.tianyi.datacenter.inspect.entity;



import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

/**
 * Created by tianxujin on 2019/5/16 15:13
 */
@TableName("user_test")
public class User  {
    @TableId(value = "id")
    private int id;
    private String name;
    private int organisation;

    public Integer getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getOrganisation() {
        return organisation;
    }

    public void setOrganisation(int organisation) {
        this.organisation = organisation;
    }
}
