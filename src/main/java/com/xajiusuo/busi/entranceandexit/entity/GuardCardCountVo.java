package com.xajiusuo.busi.entranceandexit.entity;

/**
 * Created by Administrator on 2019/6/29.
 */
public class GuardCardCountVo {
    private String days; // 日期、周、月
    private String passType; //进出类型
    private String nums; //统计值

    public GuardCardCountVo() {
    }

    public GuardCardCountVo(String days, String passType, String nums) {
        this.days = days;
        this.passType = passType;
        this.nums = nums;
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
