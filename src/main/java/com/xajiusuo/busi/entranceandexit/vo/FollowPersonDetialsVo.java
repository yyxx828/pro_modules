package com.xajiusuo.busi.entranceandexit.vo;

import com.xajiusuo.busi.diction.entity.Diction;

/**
 * 关注人员详情vo
 * Created by shirenjing on 2019/6/20.
 */
public class FollowPersonDetialsVo {

    private String ownerName; //业主姓名
    private String owneridcard; //业主证件编号
    private String followType;  //关注类型
    private String idaddr;  // '户籍地址';
    private String idphoto;  // '证件照片';
    private String identifyType;  // '身份类型（业主、家人、租客、临时客人）1:业主，2：家属，3：租户，4：访客，9：其他';
    private String lastGateEntranceInDate; //出入口最新一条数据的进出时间 1-进
    private String lastGateEntranceOutDate; //出入口最新一条数据的进出时间 0-出
    private String lastBuildingEntranceInDate;  //楼宇门禁最新一条数据的进出时间 1-进
    private String lastBuildingEntranceOutDate; //楼宇门禁最新一条数据的进出时间 0-出

    public FollowPersonDetialsVo() {
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getOwneridcard() {
        return owneridcard;
    }

    public void setOwneridcard(String owneridcard) {
        this.owneridcard = owneridcard;
    }

    public String getFollowType() {
        return followType;
    }

    public void setFollowType(String followType) {
        this.followType = followType;
    }

    public String getIdaddr() {
        return idaddr;
    }

    public void setIdaddr(String idaddr) {
        this.idaddr = idaddr;
    }

    public String getIdphoto() {
        return idphoto;
    }

    public void setIdphoto(String idphoto) {
        this.idphoto = idphoto;
    }

    public String getIdentifyType() {
        return identifyType;
    }

    public void setIdentifyType(String identifyType) {
        this.identifyType = identifyType;
    }

    public String getLastGateEntranceInDate() {
        return lastGateEntranceInDate;
    }

    public void setLastGateEntranceInDate(String lastGateEntranceInDate) {
        this.lastGateEntranceInDate = lastGateEntranceInDate;
    }

    public String getLastGateEntranceOutDate() {
        return lastGateEntranceOutDate;
    }

    public void setLastGateEntranceOutDate(String lastGateEntranceOutDate) {
        this.lastGateEntranceOutDate = lastGateEntranceOutDate;
    }

    public String getLastBuildingEntranceInDate() {
        return lastBuildingEntranceInDate;
    }

    public void setLastBuildingEntranceInDate(String lastBuildingEntranceInDate) {
        this.lastBuildingEntranceInDate = lastBuildingEntranceInDate;
    }

    public String getLastBuildingEntranceOutDate() {
        return lastBuildingEntranceOutDate;
    }

    public void setLastBuildingEntranceOutDate(String lastBuildingEntranceOutDate) {
        this.lastBuildingEntranceOutDate = lastBuildingEntranceOutDate;
    }
}
