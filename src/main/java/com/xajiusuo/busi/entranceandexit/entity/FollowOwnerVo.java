package com.xajiusuo.busi.entranceandexit.entity;

/**
 * Created by shirenjing on 2019/6/20.
 */
public class FollowOwnerVo {
    private String id;
    private String ownerName; //车主姓名
    private String ownerIdcard;  //车主证件编号
    private String villageId;  //小区编号
    private String villageName;  //小区编号

    public FollowOwnerVo() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getOwnerIdcard() {
        return ownerIdcard;
    }

    public void setOwnerIdcard(String ownerIdcard) {
        this.ownerIdcard = ownerIdcard;
    }

    public String getVillageId() {
        return villageId;
    }

    public void setVillageId(String villageId) {
        this.villageId = villageId;
    }

    public String getVillageName() {
        return villageName;
    }

    public void setVillageName(String villageName) {
        this.villageName = villageName;
    }
}
