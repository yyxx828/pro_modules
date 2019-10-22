package com.xajiusuo.busi.buildingaccess.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.xajiusuo.busi.entranceandexit.i.InOutBean;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.Date;

/**
 * 楼宇门禁采集表
 * Created by wangdou on 2019/6/6
 */
@ApiModel
@Entity
@Table(name = "T_BUILDINGENTRANCEGUARDLOG_33", catalog = "village")
public class BuildingEntranceGuardLog implements Serializable,InOutBean {
    @Id
    private String id;
    @ApiModelProperty(required = false, value = "小区编号", dataType = "String")//
    private String villageid;
    @ApiModelProperty(required = false, value = "设备编号", dataType = "String")
    private String apeid;
    @ApiModelProperty(required = false, value = "门禁卡编号", dataType = "String")//
    private String entrancecardno;
    @ApiModelProperty(required = false, value = "采集时间", dataType = "date")
    @JsonFormat( timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date passtime;
    @ApiModelProperty(required = false, value = "进出类型0出1进", dataType = "String")//
    private String passtype;
    @ApiModelProperty(required = false, value = "门禁卡人员姓名", dataType = "String")
    private String personname;
    @ApiModelProperty(required = false, value = "门禁卡证件号码", dataType = "String")
    private String personidnumber;
    @ApiModelProperty(required = false, value = "访客名称", dataType = "String")
    private String ownername;
    @ApiModelProperty(required = false, value = "访客类型", dataType = "String")
    private String ownertype;
    @ApiModelProperty(required = false, value = "访客图片", dataType = "String")
    private String visitorpicture;
    @ApiModelProperty(required = false, value = "房产地址", dataType = "String")
    private String homelocation;
    @ApiModelProperty(required = false, value = "房屋id", dataType = "String")
    private String houseid;
    @ApiModelProperty(required = false, value = "用户名", dataType = "String")
    private String user_name;
    @ApiModelProperty(required = false, value = "联系人", dataType = "String")
    private String owner_name;
    @ApiModelProperty(required = false, value = "联系电话", dataType = "String")
    private String phone;
    @ApiModelProperty(required = false, value = "联系地址", dataType = "String")
    private String address;
    @ApiModelProperty(required = false, value = "0为原始数据，1为测试数据", dataType = "String")
    private String flag;
    @ApiModelProperty(required = false, value = "创建时间", dataType = "date")
    @JsonFormat( timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createdate;
    @Transient
    private String times;
    @Transient
    private String name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVillageid() {
        return villageid;
    }

    public void setVillageid(String villageid) {
        this.villageid = villageid;
    }

    public String getApeid() {
        return apeid;
    }

    public void setApeid(String apeid) {
        this.apeid = apeid;
    }

    public String getEntrancecardno() {
        return entrancecardno;
    }

    public void setEntrancecardno(String entrancecardno) {
        this.entrancecardno = entrancecardno;
    }

    public Date getPasstime() {
        return passtime;
    }

    public void setPasstime(Date passtime) {
        this.passtime = passtime;
    }

    public String getPasstype() {
        return passtype;
    }

    public void setPasstype(String passtype) {
        this.passtype = passtype;
    }

    public String getPersonname() {
        return personname;
    }

    public void setPersonname(String personname) {
        this.personname = personname;
    }

    public String getPersonidnumber() {
        return personidnumber;
    }

    public void setPersonidnumber(String personidnumber) {
        this.personidnumber = personidnumber;
    }

    public String getOwnername() {
        return ownername;
    }

    public void setOwnername(String ownername) {
        this.ownername = ownername;
    }

    public String getOwnertype() {
        return ownertype;
    }

    public void setOwnertype(String ownertype) {
        this.ownertype = ownertype;
    }

    public String getVisitorpicture() {
        return visitorpicture;
    }

    public void setVisitorpicture(String visitorpicture) {
        this.visitorpicture = visitorpicture;
    }

    public String getHomelocation() {
        return homelocation;
    }

    public void setHomelocation(String homelocation) {
        this.homelocation = homelocation;
    }

    public String getHouseid() {
        return houseid;
    }

    public void setHouseid(String houseid) {
        this.houseid = houseid;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getOwner_name() {
        return owner_name;
    }

    public void setOwner_name(String owner_name) {
        this.owner_name = owner_name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public Date getCreatedate() {
        return createdate;
    }

    public void setCreatedate(Date createdate) {
        this.createdate = createdate;
    }

    public String getTimes() {
        return times;
    }

    public void setTimes(String times) {
        this.times = times;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
