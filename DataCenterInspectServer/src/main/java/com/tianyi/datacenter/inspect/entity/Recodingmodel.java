package com.tianyi.datacenter.inspect.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author liulele ${Date}
 * @version 0.1
 **/
@Data
@TableName("k_recodingmodel")
public class Recodingmodel {
    @TableId("rid")
    private String rid;
    private String recodingModel;
    private Integer time;
}
