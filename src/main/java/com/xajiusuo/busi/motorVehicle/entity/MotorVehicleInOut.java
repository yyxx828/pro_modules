package com.xajiusuo.busi.motorVehicle.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.xajiusuo.jpa.config.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 *  由Log表生成的新数据结构用来做异常车辆查询和统计
 * Created by liujiankang on 19-6-10.
 */
@ApiModel
@Entity
@Table(name = "T_MOTORVEHICLEINOUT_39", catalog = "village")
public class MotorVehicleInOut implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(
            generator = "uuid"
    )
    @GenericGenerator(
            name = "uuid",
            strategy = "uuid"
    )
    private String id;
    @ApiModelProperty(required = true,value="车牌编号",dataType = "String")
    private String plateno;
    @ApiModelProperty(required = true,value="进时间",dataType = "Integer")
    private Integer intime;
    @ApiModelProperty(required = true,value="出时间",dataType = "Integer")
    private Integer outtime;
    @ApiModelProperty(required = true,value="状态",dataType = "Integer")
    private Integer status;
    @ApiModelProperty(required = true,value="停留时长",dataType = "Integer")
    private Integer stoptime;
    @ApiModelProperty(required = true,value="进源数据ID",dataType = "String")
    private String inid;
    @ApiModelProperty(required = true,value="出源数据ID",dataType = "String")
    private String outid;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @ApiModelProperty(required = true,value="进日期",dataType = "Date")
    private Date indata;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @ApiModelProperty(required = true,value="出日期",dataType = "Date")
    private Date outdata;

    @Transient
    private int times;

    public int getTimes() {
        return times;
    }

    public void setTimes(int times) {
        this.times = times;
    }

    public MotorVehicleInOut(){}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPlateno() {
        return plateno;
    }

    public void setPlateno(String plateno) {
        this.plateno = plateno;
    }

    public Integer getIntime() {
        return intime;
    }

    public void setIntime(Integer intime) {
        this.intime = intime;
    }

    public Integer getOuttime() {
        return outtime;
    }

    public void setOuttime(Integer outtime) {
        this.outtime = outtime;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getStoptime() {
        return stoptime/24;
    }

    public void setStoptime(Integer stoptime) {
        this.stoptime = stoptime;
    }

    public String getInid() {
        return inid;
    }

    public void setInid(String inid) {
        this.inid = inid;
    }

    public String getOutid() {
        return outid;
    }

    public void setOutid(String outid) {
        this.outid = outid;
    }

    public Date getIndata() {
        return indata;
    }

    public void setIndata(Date indata) {
        this.indata = indata;
    }

    public Date getOutdata() {
        return outdata;
    }

    public void setOutdata(Date outdata) {
        this.outdata = outdata;
    }
}
