package com.xajiusuo.busi.motorVehicle.entity;

/**
 * 车牌识别系统统计vo
 * Created by shirenjing on 2019/6/27.
 */
public class MotorVehicleCountVo {
    private String plateNo; // 车牌号
    private String days; // 日期、周、月
    private String passType; //进出类型
    private String nums; //统计值

    public MotorVehicleCountVo() {
    }

    public MotorVehicleCountVo(String days, String passType, String nums) {
        this.days = days;
        this.passType = passType;
        this.nums = nums;
    }

    public String getPlateNo() {
        return plateNo;
    }

    public void setPlateNo(String plateNo) {
        this.plateNo = plateNo;
    }

    public String getDays() {
        return days;
    }

    public void setDays(String days) {
        this.days = days;
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

    public String getNums() {
        return nums;
    }

    public void setNums(String nums) {
        this.nums = nums;
    }
}
