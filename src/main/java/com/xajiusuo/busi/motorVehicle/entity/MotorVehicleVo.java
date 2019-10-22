package com.xajiusuo.busi.motorVehicle.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.xajiusuo.busi.diction.entity.Diction;
import com.xajiusuo.jpa.config.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;
import java.util.Date;

/**
 * 机动车基本信息vo
 * Created by shirenjing on 2019/6/14.
 */

public class MotorVehicleVo {
    private String id;
    private	String	motorVehicleID;
    private String plateClass;
    private	String	plateNo;
    private	String	vehicleClass;
    private	String	vehicleBrand;
    private	String	vehicleModel;
    private	String	vehicleColor;
    private	String	storageUrl1;
    private	String	storageUrl2;
    private	String	storageUrl3;
    private	String	vehicleStatus;
    private	String	ownerID;
    private	String	ownerName;
    private	String	ownerIDNumber;
    private	String	villageID;
    private Boolean delFlag;
    @JsonFormat( timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss" )
    private Date createTime;
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    private Date lastModifyTime;
    private String createName;
    private Integer createUID;
    private Integer lastModifyUID;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMotorVehicleID() {
        return motorVehicleID;
    }

    public void setMotorVehicleID(String motorVehicleID) {
        this.motorVehicleID = motorVehicleID;
    }

    public String getPlateClass() {
        return plateClass;
    }

    public void setPlateClass(String plateClass) {
        this.plateClass = plateClass;
    }

    public String getPlateNo() {
        return plateNo;
    }

    public void setPlateNo(String plateNo) {
        this.plateNo = plateNo;
    }

    public String getVehicleClass() {
        return vehicleClass;
    }

    public void setVehicleClass(String vehicleClass) {
        this.vehicleClass = vehicleClass;
    }

    public String getVehicleBrand() {
        return vehicleBrand;
    }

    public void setVehicleBrand(String vehicleBrand) {
        this.vehicleBrand = vehicleBrand;
    }

    public String getVehicleModel() {
        return vehicleModel;
    }

    public void setVehicleModel(String vehicleModel) {
        this.vehicleModel = vehicleModel;
    }

    public String getVehicleColor() {
        return vehicleColor;
    }

    public void setVehicleColor(String vehicleColor) {
        this.vehicleColor = vehicleColor;
    }

    public String getStorageUrl1() {
        return storageUrl1;
    }

    public void setStorageUrl1(String storageUrl1) {
        this.storageUrl1 = storageUrl1;
    }

    public String getStorageUrl2() {
        return storageUrl2;
    }

    public void setStorageUrl2(String storageUrl2) {
        this.storageUrl2 = storageUrl2;
    }

    public String getStorageUrl3() {
        return storageUrl3;
    }

    public void setStorageUrl3(String storageUrl3) {
        this.storageUrl3 = storageUrl3;
    }

    public String getVehicleStatus() {
        return vehicleStatus;
    }

    public void setVehicleStatus(String vehicleStatus) {
        this.vehicleStatus = vehicleStatus;
    }

    public String getOwnerID() {
        return ownerID;
    }

    public void setOwnerID(String ownerID) {
        this.ownerID = ownerID;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getOwnerIDNumber() {
        return ownerIDNumber;
    }

    public void setOwnerIDNumber(String ownerIDNumber) {
        this.ownerIDNumber = ownerIDNumber;
    }

    public String getVillageID() {
        return villageID;
    }

    public void setVillageID(String villageID) {
        this.villageID = villageID;
    }

    public Boolean getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(Boolean delFlag) {
        this.delFlag = delFlag;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getLastModifyTime() {
        return lastModifyTime;
    }

    public void setLastModifyTime(Date lastModifyTime) {
        this.lastModifyTime = lastModifyTime;
    }

    public String getCreateName() {
        return createName;
    }

    public void setCreateName(String createName) {
        this.createName = createName;
    }

    public Integer getCreateUID() {
        return createUID;
    }

    public void setCreateUID(Integer createUID) {
        this.createUID = createUID;
    }

    public Integer getLastModifyUID() {
        return lastModifyUID;
    }

    public void setLastModifyUID(Integer lastModifyUID) {
        this.lastModifyUID = lastModifyUID;
    }
}
