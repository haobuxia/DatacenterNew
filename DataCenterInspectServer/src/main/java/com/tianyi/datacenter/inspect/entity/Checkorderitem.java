package com.tianyi.datacenter.inspect.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;

/**
 * <p>
 * 检查工单项目
 * </p>
 *
 * @author liulele
 * @since 2019-06-11
 */
@Data
@TableName("k_checkorderitem")
public class Checkorderitem extends Model<Checkorderitem> {

    private static final long serialVersionUID = 1L;

//    /**
//     * 唯一标识
//     */
//	@TableId(value="plt_oid", type= IdType.AUTO)
//	private Integer pltOid;
//    /**
//     * 创建人员
//     */
//	@TableField("plt_creator")
//	private Integer pltCreator;
//    /**
//     * 创建时间
//     */
//	@TableField("plt_createtime")
//	private Date pltCreatetime;
//    /**
//     * 修改人员
//     */
//	@TableField("plt_lastmodifier")
//	private Integer pltLastmodifier;
//    /**
//     * 修改时间
//     */
//	@TableField("plt_lastmodifytime")
//	private Date pltLastmodifytime;
    /**
     * 检查单ID
     */
	private String checkorderId;
    /**
     * 项目名称
     */
	private String projectName;
    /**
     * 检查结果
     */
	private String result;
    /**
     * 检查项目编号
     */
	private String projectNum;
    /**
     * 机种名称
     */
	private String typeName;
    /**
     * 机型编号
     */
	private String modelNum;
    /**
     * 机型名称
     */
	private String modelName;
    /**
     * 工位
     */
	private String station;
    /**
     * 检查项类别编号
     */
	private String checktypeNum;
    /**
     * 检查项类别名称
     */
	private String checktypeName;
    /**
     * 检查项名称
     */
	private String checkitemName;
    /**
     * 判定标准
     */
	private String standard;
    /**
     * 解决办法
     */
	private String solvePlan;
    /**
     * 判定办法
     */
	private String decisionMethod;
    /**
     * 状态
     */
	private String status;
    /**
     * 检查人
     */
	private String checker;
    /**
     * 检查值
     */
	private String checkValue;
    /**
     * 确认人
     */
	private String confirmMan;
    /**
     * 确认时间
     */
	private String confirmTime;
    /**
     * 资料记录
     */
	private String dataRecoding;
    /**
     * 检查工单项目ID
     */
	private String cid;
    /**
     * 异常详情
     */
	private String problems;
    /**
     * 检查开始时间
     */
	private String checkStart;
    /**
     * 检查结束时间
     */
	private String checkEnd;




}
