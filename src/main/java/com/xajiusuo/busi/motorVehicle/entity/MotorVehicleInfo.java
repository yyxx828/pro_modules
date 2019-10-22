package com.xajiusuo.busi.motorVehicle.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.xajiusuo.busi.diction.entity.Diction;
import com.xajiusuo.jpa.config.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;

/**
 * 机动车基本信息
 * Created by shirenjing on 2019/6/14.
 */
@ApiModel
@Entity
@Table(name = "T_MOTORVEHICLE_16", catalog = "village")
@JsonIgnoreProperties(value = {"hibernateLazyInitializer", "handler"})
public class MotorVehicleInfo extends BaseEntity {

    @ApiModelProperty(required = true,value="车辆编号",dataType = "String")
    private	String	motorVehicleID;

//    @ApiModelProperty(required = true,value="号牌种类",dataType = "String")
    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.REFRESH}, optional = true)
    @JoinColumn(name="plateclass",referencedColumnName = "keys")
    private Diction plateClassDiction;

    @ApiModelProperty(required = true,value="车牌号",dataType = "String")
    private	String	plateNo;
//    @ApiModelProperty(required = true,value="车辆类型",dataType = "String")
    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.REFRESH}, optional = true)
    @JoinColumn(name="vehicleclass",referencedColumnName = "keys")
    private	Diction	vehicleClassDiction;

//    @ApiModelProperty(required = true,value="车辆品牌",dataType = "String")
    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.REFRESH}, optional = true)
    @JoinColumn(name="vehiclebrand",referencedColumnName = "keys")
    private	Diction	vehicleBrandDiction;

    @ApiModelProperty(required = false,value="车辆型号",dataType = "String")
    private	String	vehicleModel;
    @ApiModelProperty(required = false,value="车身颜色",dataType = "String")
    private	String	vehicleColor;
    @ApiModelProperty(required = false,value="近景照片",dataType = "String")
    private	String	storageUrl1;
    @ApiModelProperty(required = false,value="车牌照片",dataType = "String")
    private	String	storageUrl2;
    @ApiModelProperty(required = false,value="远景照片",dataType = "String")
    private	String	storageUrl3;

//    @ApiModelProperty(required = true,value="车辆状态 是否年审",dataType = "String")
    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.REFRESH}, optional = true)
    @JoinColumn(name="vehiclestatus",referencedColumnName = "keys")
    private	Diction	vehicleStatusDiction;

    @ApiModelProperty(required = false,value="车主ID",dataType = "String")
    private	String	ownerID;
    @ApiModelProperty(required = true,value="车主姓名",dataType = "String")
    private	String	ownerName;
    @ApiModelProperty(required = true,value="车主证件号",dataType = "String")
    private	String	ownerIDNumber;
    @ApiModelProperty(required = true,value="小区编号",dataType = "String")
    private	String	villageID;
    @ApiModelProperty(required = false,value="创建人",dataType = "String")
    private String createName;
//    private Boolean delFlag;

    public MotorVehicleInfo() {
    }

    public String getMotorVehicleID() {
        return motorVehicleID;
    }

    public void setMotorVehicleID(String motorVehicleID) {
        this.motorVehicleID = motorVehicleID;
    }

    public String getPlateClass() {
        if(plateClassDiction!=null){
            return plateClassDiction.getVal();
        }
        return null;
    }
//
//    public void setPlateClass(String plateClass) {
//        this.plateClass = plateClass;
//    }

    public String getPlateNo() {
        return plateNo;
    }

    public void setPlateNo(String plateNo) {
        this.plateNo = plateNo;
    }

    public String getVehicleClass() {
        if(vehicleClassDiction!=null){
            return vehicleClassDiction.getVal();
        }
        return null;
    }
//
//    public void setVehicleClass(String vehicleClass) {
//        this.vehicleClass = vehicleClass;
//    }
//
    public String getVehicleBrand() {
        if(vehicleBrandDiction!=null){
            return vehicleBrandDiction.getVal();
        }
        return null;
    }
//
//    public void setVehicleBrand(String vehicleBrand) {
//        this.vehicleBrand = vehicleBrand;
//    }

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
        if(vehicleStatusDiction!=null){
            return vehicleStatusDiction.getVal();
        }
        return null;
    }
//
//    public void setVehicleStatus(String vehicleStatus) {
//        this.vehicleStatus = vehicleStatus;
//    }

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

    @Override
    public String getCreateName() {
        return createName;
    }

    @Override
    public void setCreateName(String createName) {
        this.createName = createName;
    }

//    @Override
//    public Boolean getDelFlag() {
//        return delFlag;
//    }
//
//    @Override
//    public void setDelFlag(Boolean delFlag) {
//        this.delFlag = delFlag;
//    }

    public Diction getPlateClassDiction() {
        return plateClassDiction;
    }

    public void setPlateClassDiction(Diction plateClassDiction) {
        this.plateClassDiction = plateClassDiction;
    }

    public Diction getVehicleClassDiction() {
        return vehicleClassDiction;
    }

    public void setVehicleClassDiction(Diction vehicleClassDiction) {
        this.vehicleClassDiction = vehicleClassDiction;
    }

    public Diction getVehicleBrandDiction() {
        return vehicleBrandDiction;
    }

    public void setVehicleBrandDiction(Diction vehicleBrandDiction) {
        this.vehicleBrandDiction = vehicleBrandDiction;
    }

    public Diction getVehicleStatusDiction() {
        return vehicleStatusDiction;
    }

    public void setVehicleStatusDiction(Diction vehicleStatusDiction) {
        this.vehicleStatusDiction = vehicleStatusDiction;
    }
}
