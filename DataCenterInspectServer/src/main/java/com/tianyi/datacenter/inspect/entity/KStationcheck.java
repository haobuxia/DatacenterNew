package com.tianyi.datacenter.inspect.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;

import java.util.Date;

/**
 * <p>
 * 工位检查类别
 * </p>
 *
 * @author liulele
 * @since 2019-06-09
 */

@TableName("k_stationcheck")
public class KStationcheck extends Model<KStationcheck> {

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
     * 检查类别ID
     */
	@TableField("checktypeId")
	private String checktypeId;
    /**
     * 工位ID
     */
	@TableField("stationId")
	private String stationId;

	@TableField("scid")
	private String scid;


	public static long getSerialVersionUID() {
		return serialVersionUID;
	}
	public String getScid() {
		return scid;
	}

	public void setScid(String scid) {
		this.scid = scid;
	}

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

	public String getChecktypeId() {
		return checktypeId;
	}

	public void setChecktypeId(String checktypeId) {
		this.checktypeId = checktypeId;
	}

	public String getStationId() {
		return stationId;
	}

	public void setStationId(String stationId) {
		this.stationId = stationId;
	}
}
