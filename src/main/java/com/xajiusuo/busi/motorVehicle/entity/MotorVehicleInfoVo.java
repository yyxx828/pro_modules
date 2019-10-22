package com.xajiusuo.busi.motorVehicle.entity;

/**
 * 机动车基本信息列表查询字段VO
 * Created by shirenjing on 2019/6/15.
 */
public class MotorVehicleInfoVo {

    private String motorVehicleID; // 车辆编号
    private	String	plateClass; //号牌种类
    private	String	plateNo;  //车牌号
    private	String	vehicleClass;  //车辆类型
    private	String	vehicleBrand;  //车辆品牌
    private	String	vehicleModel;  //车辆型号
    private	String	vehicleColor;   //车身颜色
    private	String	vehicleStatus;  //车辆状态 是否年审 0：否 1：是
    private	String	ownerName; //车主姓名
    private	String	ownerIDNumber;  //车主证件号
    private	String	villageName;  //小区名称
    private	String	villageID;  //小区编号

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

    public String getVehicleStatus() {
        return vehicleStatus;
    }

    public void setVehicleStatus(String vehicleStatus) {
        this.vehicleStatus = vehicleStatus;
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

    public String getVillageName() {
        return villageName;
    }

    public void setVillageName(String villageName) {
        this.villageName = villageName;
    }

    public String getVillageID() {
        return villageID;
    }

    public void setVillageID(String villageID) {
        this.villageID = villageID;
    }
}
