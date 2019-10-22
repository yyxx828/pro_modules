package com.xajiusuo.busi.motorVehicle.entity;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

/**
 * Created by hadoop on 19-7-1.
 */
public class MotorVehicleWarnVo {

    private String  plateno;;  //车牌号
    private Integer nums;  //中标次数
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date dataRangeStart; //数据范围开始时间
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date dataRangeEnd;  //数据范围结束时间
    private Integer timeRangeStart;  //开始小时
    private Integer timeRangeEend;  //结束小时
    private String analyType;  //0:长时间停放；1:昼伏夜出 2：频繁出入

    public String getPlateno() {
        return plateno;
    }

    public void setPlateno(String plateno) {
        this.plateno = plateno;
    }

    public Integer getNums() {
        return nums;
    }

    public void setNums(Integer nums) {
        this.nums = nums;
    }

    public Date getDataRangeStart() {
        return dataRangeStart;
    }

    public void setDataRangeStart(Date dataRangeStart) {
        this.dataRangeStart = dataRangeStart;
    }

    public Date getDataRangeEnd() {
        return dataRangeEnd;
    }

    public void setDataRangeEnd(Date dataRangeEnd) {
        this.dataRangeEnd = dataRangeEnd;
    }

    public Integer getTimeRangeStart() {
        return timeRangeStart;
    }

    public void setTimeRangeStart(Integer timeRangeStart) {
        this.timeRangeStart = timeRangeStart;
    }

    public Integer getTimeRangeEend() {
        return timeRangeEend;
    }

    public void setTimeRangeEend(Integer timeRangeEend) {
        this.timeRangeEend = timeRangeEend;
    }

    public String getAnalyType() {
        return analyType;
    }

    public void setAnalyType(String analyType) {
        this.analyType = analyType;
    }
}
