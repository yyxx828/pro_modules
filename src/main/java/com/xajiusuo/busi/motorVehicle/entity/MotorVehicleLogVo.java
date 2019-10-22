package com.xajiusuo.busi.motorVehicle.entity;

/**
 * 机动车（停车场）信息采集 列表查询字段VO
 * Created by shirenjing on 2019/6/6.
 */
public class MotorVehicleLogVo {
    private String plateNo; //车牌号
    private String startpPassTime; //采集时间
    private String endPassTime; //采集时间
    private String passType;//0-出，1-进

    public String getPlateNo() {
        return plateNo;
    }

    public void setPlateNo(String plateNo) {
        this.plateNo = plateNo;
    }

    public String getStartpPassTime() {
        return startpPassTime;
    }

    public void setStartpPassTime(String startpPassTime) {
        this.startpPassTime = startpPassTime;
    }

    public String getEndPassTime() {
        return endPassTime;
    }

    public void setEndPassTime(String endPassTime) {
        this.endPassTime = endPassTime;
    }

    public String getPassType() {
        return passType;
    }

    public void setPassType(String passType) {
        this.passType = passType;
    }
}
