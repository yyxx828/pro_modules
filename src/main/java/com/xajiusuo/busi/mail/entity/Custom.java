package com.xajiusuo.busi.mail.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.xajiusuo.jpa.config.BaseIntEntity;
import com.xajiusuo.busi.mail.send.conf.TipsConf;
import io.swagger.annotations.ApiModel;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * 消息推送订阅
 * Created by hadoop on 19-3-29.
 */
@Entity
@ApiModel(value = "Custom", description = "消息订阅")
@Table(name = "M_CUSTOM_11", catalog = "village")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Custom  extends BaseIntEntity{

    private Integer userId;//订阅用户
    private String tipsConf;//对应消息类型
    private Boolean subscribe;//是否订阅,消息保存
    private Boolean remind;//是否提醒,有下角弹出消息

    @Transient
    private boolean enble;//是否可进行订阅,false,无法进行订阅

    public Custom(){
    }

    public Custom(Integer userId, String tipsConf, Boolean subscribe, Boolean remind) {
        this.userId = userId;
        this.tipsConf = tipsConf;
        this.subscribe = subscribe;
        this.remind = remind;
    }

    public Custom(Integer userId,TipsConf tc) {
        this(userId,tc.toString(),tc.isSubscribe(),true);
        this.enble = tc.isSubscribe();
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getTipsConf() {
        return tipsConf;
    }

    public String getTipsName(){
        TipsConf t = TipsConf.valueOf(tipsConf);
        if(t == null){
            t = TipsConf.TIPS_NO;
        }
        return t.getName();
    }

    public void setTipsConf(String tipsConf) {
        this.tipsConf = tipsConf;
    }

    public Boolean getSubscribe() {
        return subscribe;
    }

    public void setSubscribe(Boolean subscribe) {
        this.subscribe = subscribe;
    }

    public Boolean getRemind() {
        return remind;
    }

    public void setRemind(Boolean remind) {
        this.remind = remind;
    }

    public boolean isEnble() {
        return enble;
    }

    public void setEnble(boolean enble) {
        this.enble = enble;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Custom custom = (Custom) o;

        if (userId != null ? !userId.equals(custom.userId) : custom.userId != null) return false;
        return !(tipsConf != null ? !tipsConf.equals(custom.tipsConf) : custom.tipsConf != null);

    }

    @Override
    public int hashCode() {
        int result = userId != null ? userId.hashCode() : 0;
        result = 31 * result + (tipsConf != null ? tipsConf.hashCode() : 0);
        return result;
    }
}
