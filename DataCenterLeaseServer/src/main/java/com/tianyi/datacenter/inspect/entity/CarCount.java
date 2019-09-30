package com.tianyi.datacenter.inspect.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * @author liulele ${Date}
 * @version 0.1
 **/
@Data
@TableName("k_carcount")
public class CarCount {
    private static final long serialVersionUID = 1L;

    /**
     * 唯一标识
     */
    @TableId(value="plt_oid", type= IdType.AUTO)
    private Integer pltOid;
    /**
     * 创建人员
     */
    @TableField("plt_creator")
    private Integer pltCreator;
    /**
     * 创建时间
     */
    @TableField("plt_createtime")
    private Date pltCreatetime;
    /**
     * 修改人员
     */
    @TableField("plt_lastmodifier")
    private Integer pltLastmodifier;
    /**
     * 修改时间
     */
    @TableField("plt_lastmodifytime")
    private Date pltLastmodifytime;

    @TableField("carCount")
    private Integer carCount;
}
