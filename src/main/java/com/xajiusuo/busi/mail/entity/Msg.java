package com.xajiusuo.busi.mail.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.xajiusuo.jpa.config.BaseIntEntity;
import com.xajiusuo.busi.mail.send.conf.TipsConf;
import io.swagger.annotations.ApiModel;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * 订阅消息列表,可删除
 * Created by hadoop on 19-3-29.
 */
@Entity
@ApiModel(value = "Msg", description = "订阅消息")
@Table(name = "M_MSG_11", catalog = "village")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Msg extends BaseIntEntity{

    private Integer receiveUserId;//接收人
    private Boolean read;//是否已读
    private String tips;//消息类型
    private String caseId;//案件ID
    private String entityId;//业务ID
    private String content;//内容

    private Boolean msgStar;//收藏
    private Boolean msgTop;//置顶

    private String param;//其它参数

    public Integer getReceiveUserId() {
        return receiveUserId;
    }

    public void setReceiveUserId(Integer receiveUserId) {
        this.receiveUserId = receiveUserId;
    }

    public Boolean getRead() {
        return read;
    }

    public void setRead(Boolean read) {
        this.read = read;
    }

    public String getTips() {
        return tips;
    }

    public void setTips(String tips) {
        this.tips = tips;
    }

    public String getRemote() {
        return getTipsConf().getRemote();
    }

    public String getModule() {
        return getTipsConf().getModule();
    }

    public String getTipsName() {
        return getTipsConf().getName();
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public int getSign() {
        return 1;
    }

    public Integer getMsgId(){
        return getId();
    }

    public String getCaseId() {
        return caseId;
    }

    public void setCaseId(String caseId) {
        this.caseId = caseId;
    }

    public void setTipsConf(TipsConf tipsConf){
        this.tips = tipsConf.toString();
    }

    public String getParam() {
        return param;
    }

    public void setParam(String param) {
        this.param = param;
    }

    public Boolean getMsgStar() {
        return msgStar;
    }

    public void setMsgStar(Boolean msgStar) {
        this.msgStar = msgStar;
    }

    public Boolean getMsgTop() {
        return msgTop;
    }

    public void setMsgTop(Boolean msgTop) {
        this.msgTop = msgTop;
    }

    public TipsConf getTipsConf() {
        try {
            return TipsConf.valueOf(tips);
        }catch (Exception e){
            return TipsConf.TIPS_NO;
        }
    }
}
