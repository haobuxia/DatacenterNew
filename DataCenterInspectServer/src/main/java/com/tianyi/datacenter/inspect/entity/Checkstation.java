package com.tianyi.datacenter.inspect.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 检查工位
 * </p>
 *
 * @author liulele
 * @since 2019-06-09
 */
@TableName("k_checkstation")
public class Checkstation extends Model<Checkstation> {

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
    /**
     * 工位ID
     */
	@TableField("cid")
	private String cid;
    /**
     * 工位编号
     */
	@TableField("stationNum")
	private String stationNum;
    /**
     * 工位名称
     */
	@TableField("stationName")
	private String stationName;


	public Integer getPltOid() {
		return pltOid;
	}

	public void setPltOid(Integer pltOid) {
		this.pltOid = pltOid;
	}

	public Integer getPltCreator() {
		return pltCreator;
	}

	public void setPltCreator(Integer pltCreator) {
		this.pltCreator = pltCreator;
	}

	public Date getPltCreatetime() {
		return pltCreatetime;
	}

	public void setPltCreatetime(Date pltCreatetime) {
		this.pltCreatetime = pltCreatetime;
	}

	public Integer getPltLastmodifier() {
		return pltLastmodifier;
	}

	public void setPltLastmodifier(Integer pltLastmodifier) {
		this.pltLastmodifier = pltLastmodifier;
	}

	public Date getPltLastmodifytime() {
		return pltLastmodifytime;
	}

	public void setPltLastmodifytime(Date pltLastmodifytime) {
		this.pltLastmodifytime = pltLastmodifytime;
	}

	public String getId() {
		return cid;
	}

	public void setId(Integer id) {
		this.cid = cid;
	}

	public String getStationNum() {
		return stationNum;
	}

	public void setStationNum(String stationNum) {
		this.stationNum = stationNum;
	}

	public String getStationName() {
		return stationName;
	}

	public void setStationName(String stationName) {
		this.stationName = stationName;
	}

	@Override
	protected Serializable pkVal() {
		return this.pltOid;
	}

}
