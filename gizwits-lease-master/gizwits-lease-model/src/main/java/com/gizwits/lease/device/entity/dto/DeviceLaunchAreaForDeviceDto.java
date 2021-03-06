package com.gizwits.lease.device.entity.dto;

import com.gizwits.lease.device.entity.DeviceLaunchArea;
import org.springframework.beans.BeanUtils;

/**
 * @author lilh
 * @date 2017/7/21 14:51
 */
public class DeviceLaunchAreaForDeviceDto {

    private Integer launchAreaId;

    private String launchAreaName;

    private String province;

    private String city;

    private String area;

    private String address;

    private String personInCharge;

    private String mobile;

    private String pictureUrl;

    public DeviceLaunchAreaForDeviceDto(DeviceLaunchArea deviceLaunchArea) {
        BeanUtils.copyProperties(deviceLaunchArea, this);
        this.launchAreaId = deviceLaunchArea.getId();
        this.launchAreaName = deviceLaunchArea.getName();
        this.pictureUrl = deviceLaunchArea.getPictureUrl();
    }

    public Integer getLaunchAreaId() {
        return launchAreaId;
    }

    public void setLaunchAreaId(Integer launchAreaId) {
        this.launchAreaId = launchAreaId;
    }

    public String getLaunchAreaName() {
        return launchAreaName;
    }

    public void setLaunchAreaName(String launchAreaName) {
        this.launchAreaName = launchAreaName;
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

    public String getPersonInCharge() {
        return personInCharge;
    }

    public void setPersonInCharge(String personInCharge) {
        this.personInCharge = personInCharge;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getPictureUrl() { return pictureUrl; }

    public void setPictureUrl(String pictureUrl) { this.pictureUrl = pictureUrl; }
}
