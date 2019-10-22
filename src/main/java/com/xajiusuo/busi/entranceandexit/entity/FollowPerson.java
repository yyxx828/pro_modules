package com.xajiusuo.busi.entranceandexit.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.xajiusuo.busi.diction.entity.Diction;
import com.xajiusuo.busi.villageMessage.entity.Owner;
import com.xajiusuo.jpa.config.BaseEntity;
import io.swagger.annotations.ApiModel;

import javax.persistence.*;

/**
 * 关注人员信息
 * Created by shirenjing on 2019/6/17.
 */
@ApiModel
@Entity
@Table(name = "T_FOLLOWPERSON_16", catalog = "village")
@JsonIgnoreProperties(value = {"hibernateLazyInitializer", "handler"})
public class FollowPerson extends BaseEntity{

    private String owneridcard; //业主
    private String followType;  //关注类型
//    private Boolean delFlag;

    public String getOwneridcard() {
        return owneridcard;
    }

    public void setOwneridcard(String owneridcard) {
        this.owneridcard = owneridcard;
    }

//    @Override
//    public Boolean getDelFlag() {
//        return delFlag;
//    }
//
//    @Override
//    public void setDelFlag(Boolean delFlag) {
//        this.delFlag = delFlag;
//    }

    public String getFollowType() {
        return followType;
    }

    public void setFollowType(String followType) {
        this.followType = followType;
    }
}
