package com.gizwits.boot.sys.entity;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 设备投放点表
 * </p>
 *
 * @author yinhui
 * @since 2017-07-12
 */
@TableName("device_launch_area")
public class SysLaunchArea extends Model<SysLaunchArea> {

    private static final long serialVersionUID = 1L;

    /**
     * 主键,自增长
     */
	@TableId(value="id", type= IdType.AUTO)
	private Integer id;
    /**
     * 添加时间
     */
	private Date ctime;
    /**
     * 更新时间
     */
	private Date utime;
    /**
     * 仓库名称
     */
	private String name;
    /**
     * 省
     */
	private String province;
    /**
     * 市
     */
	private String city;
    /**
     * 区/县
     */
	private String area;
    /**
     * 详细地址
     */
	private String address;
    /**
     * 投放点创建人员
     */
	@TableField("sys_user_id")
	private Integer sysUserId;
    /**
     * 投放点创建者id
     */
	@TableField("sys_user_name")
	private String sysUserName;
	/**
	 * 负责人电话
	 */
	private String  mobile;
    /**
     * 运营商
     */
	@TableField("operator_id")
	private Integer operatorId;
    /**
     * 运营商名称
     */
	@TableField("operator_name")
	private String operatorName;
	/**
	 * 维护人员姓名
	 */
	@TableField("maintainer_name")
	private String maintainerName;
	/**
	 * 维护人id
	 */
	@TableField("maintainer_id")
	private Integer maintainerId;
	/**
	 * 投放点经度
	 */
	private String longitude;
	/**
	 * 投放点纬度
	 */
	private String latitude;
	/**
	 * 负责人姓名
	 */
	@TableField("person_in_charge")
	private String personInCharge;
	/**
	 * 负责人id
	 */
	@TableField("person_in_charge_id")
	private Integer personInChargeId;

	/**
	 * 状态  0.正常、1.停用
	 */
	private Integer status;
	/**
	 * 是否删除
	 */
	@TableField("is_deleted")
	private  Integer isDelete;

	/**
	 * 归属
	 */
	@TableField("owner_id")
	private Integer ownerId;

	@TableField("picture_url")
	private String pictureUrl;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Date getCtime() {
		return ctime;
	}

	public void setCtime(Date ctime) {
		this.ctime = ctime;
	}

	public Date getUtime() {
		return utime;
	}

	public void setUtime(Date utime) {
		this.utime = utime;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Integer getSysUserId() {
		return sysUserId;
	}

	public void setSysUserId(Integer sysUserId) {
		this.sysUserId = sysUserId;
	}

	public String getSysUserName() {
		return sysUserName;
	}

	public void setSysUserName(String sysUserName) {
		this.sysUserName = sysUserName;
	}

	public Integer getOperatorId() {
		return operatorId;
	}

	public void setOperatorId(Integer operatorId) {
		this.operatorId = operatorId;
	}

	public String getOperatorName() {
		return operatorName;
	}

	public void setOperatorName(String operatorName) {
		this.operatorName = operatorName;
	}

	public String getMobile() {return mobile;}

	public void setMobile(String mobile) {this.mobile = mobile;}

	public String getMaintainerName() {return maintainerName;}

	public void setMaintainerName(String maintainerName) {this.maintainerName = maintainerName;}

	public Integer getMaintainerId() {return maintainerId;}

	public void setMaintainerId(Integer maintainerId) {this.maintainerId = maintainerId;}

	public String getLongitude() {return longitude;}

	public void setLongitude(String longitude) {this.longitude = longitude;}

	public String getLatitude() {return latitude;}

	public void setLatitude(String latitude) {this.latitude = latitude;}

	public String getPersonInCharge() {return personInCharge;}

	public void setPersonInCharge(String personInCharge) {this.personInCharge = personInCharge;}

	public Integer getPersonInChargeId() {return personInChargeId;}

	public void setPersonInChargeId(Integer personInChargeId) {this.personInChargeId = personInChargeId;}

	public Integer getStatus() {return status;}

	public void setStatus(Integer status) {this.status = status;}

	public Integer getIsDelete() {
		return isDelete;
	}

	public void setIsDelete(Integer isDelete) {
		this.isDelete = isDelete;
	}

	public Integer getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(Integer ownerId) {
		this.ownerId = ownerId;
	}

	public String getPictureUrl() { return pictureUrl; }

	public void setPictureUrl(String pictureUrl) { this.pictureUrl = pictureUrl; }

	@Override
	protected Serializable pkVal() {
		return this.id;
	}

}
