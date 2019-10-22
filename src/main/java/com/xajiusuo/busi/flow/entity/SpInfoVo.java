package com.xajiusuo.busi.flow.entity;

/**
 * Created by fanhua on 18-1-22.审批详情VO
 */
public class SpInfoVo extends SpInfo{
    private String userName;
    private String spTypeName;

    public SpInfoVo(SpInfo spInfo) {
        super(spInfo);
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getSpTypeName() {
        return spTypeName;
    }

    public void setSpTypeName(String spTypeName) {
        this.spTypeName = spTypeName;
    }
}
