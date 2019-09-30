package com.tianyi.datacenter.inspect.entity;


import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * Created by tianxujin on 2019/5/16 15:13
 */
@Data
@TableName("k_stationcheck")
public class Station {
    /**
     * 检查类别ID
     */
    @TableId(value = "checktypeId")
    private String checktypeId;
    /**
     * 工位ID
     */
    @TableId(value = "stationId")
    private String stationId;
    /**
     * 主键
     */
    @TableId(value = "scid")
    private String scid;


}
