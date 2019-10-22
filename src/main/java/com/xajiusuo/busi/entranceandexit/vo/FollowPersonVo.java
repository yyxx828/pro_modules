package com.xajiusuo.busi.entranceandexit.vo;

/**
 * 关注人员查询字段
 * Created by shirenjing on 2019/6/18.
 */
public class FollowPersonVo {

    private String fpName; //关注人员姓名
    private String fpCardId;  //证件编号
    private String fpType;  //关注人员类型

    public FollowPersonVo() {
    }

    public String getFpName() {
        return fpName;
    }

    public void setFpName(String fpName) {
        this.fpName = fpName;
    }

    public String getFpCardId() {
        return fpCardId;
    }

    public void setFpCardId(String fpCardId) {
        this.fpCardId = fpCardId;
    }

    public String getFpType() {
        return fpType;
    }

    public void setFpType(String fpType) {
        this.fpType = fpType;
    }
}
