package com.xajiusuo.busi.motorVehicle.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * 机动车（停车场）信息采集表
 * Created by shirenjing on 2019/6/6.
 */
@ApiModel
@Entity
@Table(name = "T_MOTORVEHICLELOG_39", catalog = "village")
@JsonIgnoreProperties(value = {"hibernateLazyInitializer", "handler"})
public class MotorVehicleLog implements Serializable{

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
    @ApiModelProperty(required = true,value="小区编号",dataType = "String")
    private String villageID;
    @ApiModelProperty(required = true,value="设备编码",dataType = "String")
    private	String	apeID;
    @ApiModelProperty(required = true,value="车牌号",dataType = "String")
    private	String	plateNo;
    @ApiModelProperty(required = true,value="进出类型 0-出，1-进",dataType = "String")
    private	String	passType;
    @ApiModelProperty(required = true,value="照片1",dataType = "String")
    private	String	photo1;
    @ApiModelProperty(required = true,value="照片2",dataType = "String")
    private	String	photo2;
    @ApiModelProperty(required = true,value="采集时间",dataType = "Date")
    @JsonFormat( timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date passTime;
    @ApiModelProperty(required = true,value="创建时间",dataType = "Date")
    @JsonFormat( timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private	Date	createDate;
    @ApiModelProperty(required = true,value="进出状态",dataType = "String")
    private	String	inOutStatus;
    @ApiModelProperty(required = true,value="进出标识",dataType = "String")
    private	String	inOutSign;
    @ApiModelProperty(required = true,value="车牌遮挡 plate 车牌 cover 覆盖",dataType = "String")
    private	String	plateCover;
    @ApiModelProperty(required = true,value="套牌车辆 deck 套牌 ",dataType = "String")
    private	String	deckCar;
    @ApiModelProperty(required = true,value="无牌车辆",dataType = "String")
    private	String	noPlateCar;
    @ApiModelProperty(required = true,value="车身颜色",dataType = "String")
    private	String	vehiclecolor;
    @ApiModelProperty(required = true,value="车辆品牌",dataType = "String")
    private	String	vehiclebrand;
    @ApiModelProperty(required = true,value="车型信息",dataType = "String")
    private	String	vehiclemodel;
    @ApiModelProperty(required = true,value="车辆类型",dataType = "String")
    private	String	vehicleclass;
    @ApiModelProperty(required = true,value="车架号",dataType = "String")
    private	String	vehicleIdentNumber;
    @ApiModelProperty(required = true,value="逾期未年检",dataType = "String")
    private	String	timeOutNoCare;
    @ApiModelProperty(required = true,value="是否系安全带 safety belt 安全带",dataType = "String")
    private	String	noSafetyBelt;
    @ApiModelProperty(required = true,value="是否拨打手机",dataType = "String")
    private	String	callPhone;
    @ApiModelProperty(required = true,value="临时号牌",dataType = "String")
    private	String	tempPlate;
    @ApiModelProperty(required = true,value="车牌种类（蓝/黄/武警/军）",dataType = "String")
    private	String	plateclass;

    public MotorVehicleLog() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVillageID() {
        return villageID;
    }

    public void setVillageID(String villageID) {
        this.villageID = villageID;
    }

    public String getApeID() {
        return apeID;
    }

    public void setApeID(String apeID) {
        this.apeID = apeID;
    }

    public String getPlateNo() {
        return plateNo;
    }

    public void setPlateNo(String plateNo) {
        this.plateNo = plateNo;
    }

    public String getPassType() {
        return passType;
    }

    public String getPassTypes() {
        if("1".equals(passType)){
            return "进";
        }
        if("0".equals(passType)){
            return "出";
        }
        return passType;
    }

    public void setPassType(String passType) {
        this.passType = passType;
    }

    public String getPhoto1() {
        return photo1;
    }

    public void setPhoto1(String photo1) {
        this.photo1 = photo1;
    }

    public String getPhoto2() {
        return photo2;
    }

    public void setPhoto2(String photo2) {
        this.photo2 = photo2;
    }

    public Date getPassTime() {
        return passTime;
    }

    public void setPassTime(Date passTime) {
        this.passTime = passTime;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getInOutStatus() {
        return inOutStatus;
    }

    public void setInOutStatus(String inOutStatus) {
        this.inOutStatus = inOutStatus;
    }

    public String getInOutSign() {
        return inOutSign;
    }

    public void setInOutSign(String inOutSign) {
        this.inOutSign = inOutSign;
    }

    public String getPlateCover() {
        return plateCover;
    }

    public void setPlateCover(String plateCover) {
        this.plateCover = plateCover;
    }

    public String getDeckCar() {
        return deckCar;
    }

    public void setDeckCar(String deckCar) {
        this.deckCar = deckCar;
    }

    public String getNoPlateCar() {
        return noPlateCar;
    }

    public void setNoPlateCar(String noPlateCar) {
        this.noPlateCar = noPlateCar;
    }

    public String getVehiclecolor() {
        return vehiclecolor;
    }

    public void setVehiclecolor(String vehiclecolor) {
        this.vehiclecolor = vehiclecolor;
    }

    public String getVehiclebrand() {
        return vehiclebrand;
    }

    public void setVehiclebrand(String vehiclebrand) {
        this.vehiclebrand = vehiclebrand;
    }

    public String getVehiclemodel() {
        return vehiclemodel;
    }

    public void setVehiclemodel(String vehiclemodel) {
        this.vehiclemodel = vehiclemodel;
    }

    public String getVehicleclass() {
        return vehicleclass;
    }

    public void setVehicleclass(String vehicleclass) {
        this.vehicleclass = vehicleclass;
    }

    public String getVehicleIdentNumber() {
        return vehicleIdentNumber;
    }

    public void setVehicleIdentNumber(String vehicleIdentNumber) {
        this.vehicleIdentNumber = vehicleIdentNumber;
    }

    public String getTimeOutNoCare() {
        return timeOutNoCare;
    }

    public void setTimeOutNoCare(String timeOutNoCare) {
        this.timeOutNoCare = timeOutNoCare;
    }

    public String getNoSafetyBelt() {
        return noSafetyBelt;
    }

    public void setNoSafetyBelt(String noSafetyBelt) {
        this.noSafetyBelt = noSafetyBelt;
    }

    public String getCallPhone() {
        return callPhone;
    }

    public void setCallPhone(String callPhone) {
        this.callPhone = callPhone;
    }

    public String getTempPlate() {
        return tempPlate;
    }

    public void setTempPlate(String tempPlate) {
        this.tempPlate = tempPlate;
    }

    public String getPlateclass() {
        return plateclass;
    }

    public void setPlateclass(String plateclass) {
        this.plateclass = plateclass;
    }
}
