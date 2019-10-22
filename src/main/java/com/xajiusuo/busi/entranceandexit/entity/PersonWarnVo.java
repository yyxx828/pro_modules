package com.xajiusuo.busi.entranceandexit.entity;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

/**
 * Created by shirenjing on 2019/6/29.
 */
public class PersonWarnVo {

    private String entrancecardno;  //门禁卡编号
    private String pIdCard;  //人员证件编号
    private String pName;  //人员姓名
    private Integer nums;  //中标次数
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date dataRangeStart; //数据范围开始时间
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date dataRangeEnd;  //数据范围结束时间
    private Integer timeRangeStart;  //开始小时
    private Integer timeRangeEend;  //结束小时
    private String analyType;  //1:昼伏夜出 2：频繁出入
    private Integer flag;    //0:出入口门禁 1：楼宇门禁

    public PersonWarnVo() {
    }

    public String getEntrancecardno() {
        return entrancecardno;
    }

    public void setEntrancecardno(String entrancecardno) {
        this.entrancecardno = entrancecardno;
    }

    public String getpIdCard() {
        return pIdCard;
    }

    public void setpIdCard(String pIdCard) {
        this.pIdCard = pIdCard;
    }

    public String getpName() {
        return pName;
    }

    public void setpName(String pName) {
        this.pName = pName;
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

    public Integer getFlag() {
        return flag;
    }

    public void setFlag(Integer flag) {
        this.flag = flag;
    }
}
