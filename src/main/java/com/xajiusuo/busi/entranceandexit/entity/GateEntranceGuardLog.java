package com.xajiusuo.busi.entranceandexit.entity;

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
 * 出入口门禁采集表
 * Created by wangdou on 2019/6/6
 */
@ApiModel
@Entity
@Table(name = "T_GATEENTRANCEGUARDLOG_33", catalog = "village")
public class GateEntranceGuardLog implements Serializable,InOutBean {
    @Id
    private String id;
    @ApiModelProperty(required = false, value = "小区编号", dataType = "String")
    private String villageid;
    @ApiModelProperty(required = false, value = "设备编号", dataType = "String")
    private String apeid;
    @ApiModelProperty(required = false, value = "门禁卡编号", dataType = "String")
    private String entrancecardno;
    @ApiModelProperty(required = false, value = "采集时间", dataType = "date")
    @JsonFormat( timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date passtime;
    @ApiModelProperty(required = false, value = "进出类型0出1进", dataType = "String")
    private String passtype;
    @ApiModelProperty(required = false, value = "进出位置", dataType = "String")
    private String place;
    @ApiModelProperty(required = false, value = "", dataType = "String")
    private String user_name;
    @ApiModelProperty(required = false, value = "", dataType = "String")
    private String owner_name;
    @ApiModelProperty(required = false, value = "", dataType = "String")
    private String phone;
    @ApiModelProperty(required = false, value = "", dataType = "String")
    private String address;
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

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
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
