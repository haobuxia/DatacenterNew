package com.tianyi.datacenter.inspect.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author liulele ${Date}
 * @version 0.1
 **/
@Data
@TableName("k_checkitem")
public class Checkitem {
    @TableId("cid")
    private String cid;
    private String rmId;
    private String checkitemtypeId;
    private String checkitemNum;
    private String checkitemName;
    private String checkOrder;
    private String standard;
    private String solveMethod;
    private String decisionMethod;
    private String resultType;
    private String minResult;
    private String maxResult;
    private String voiceInfo;
    private String guidance;

}
