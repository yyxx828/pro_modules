package com.xajiusuo.busi.entranceandexit.vo;

/**
 * 关注人员亲属
 * Created by shirenjing on 2019/6/20.
 */
public class FollowPersonRelationVo {

    private String followIdcard; //关注人员证件编号
    private String relationIdcard; //亲属证件编号
    private String relationName;  //亲属姓名
    private String relationPhone; //亲属电话
    private String relationPhoto; //亲属照片

    public String getFollowIdcard() {
        return followIdcard;
    }

    public void setFollowIdcard(String followIdcard) {
        this.followIdcard = followIdcard;
    }

    public String getRelationIdcard() {
        return relationIdcard;
    }

    public void setRelationIdcard(String relationIdcard) {
        this.relationIdcard = relationIdcard;
    }

    public String getRelationName() {
        return relationName;
    }

    public void setRelationName(String relationName) {
        this.relationName = relationName;
    }

    public String getRelationPhone() {
        return relationPhone;
    }

    public void setRelationPhone(String relationPhone) {
        this.relationPhone = relationPhone;
    }

    public String getRelationPhoto() {
        return relationPhoto;
    }

    public void setRelationPhoto(String relationPhoto) {
        this.relationPhoto = relationPhoto;
    }
}
