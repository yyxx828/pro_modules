package com.xajiusuo.busi.entranceandexit.vo;

import java.util.Date;

/**
 * 人员预警数据vo
 * Created by shirenjing on 2019/6/26.
 */
public class PersonEarlyWarningVo {

    private String entrancecardno; //门禁卡编号
    private String pIdCard; //证件编号
    private String pName; //姓名
    private Integer nums;//次数
    private String ids; //中标详单id

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

    public String getIds() {
        return ids;
    }

    public void setIds(String ids) {
        this.ids = ids;
    }
}
