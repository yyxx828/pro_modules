package com.xajiusuo.busi.mail.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.xajiusuo.jpa.config.BaseIntEntity;
import com.xajiusuo.busi.mail.send.conf.TipsConf;
import io.swagger.annotations.ApiModel;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Date;

/**
 * 待发信息,发送后删除,所有发送消息转换为touch
 * Created by hadoop on 19-3-29.
 */
@Entity
@ApiModel(value = "Touch", description = "待发消息")
@Table(name = "M_TOUCH_11", catalog = "village")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Touch extends BaseIntEntity{

    private String tips;//消息类型
    private Integer receiveUserId;//接收人用户ID1
    private String content;//内容
    private String caseId;//案件ID
    private String entityId;//业务ID
    private Date valid;//有效时间
    private Integer msgId;//对应消息ID
    private Integer duration;//展示时长,-1为不关闭

    private String param;//其它参数

    @Transient
    private Integer[] receiveUserIds;

    @Transient
    private static Touch online = null;
    @Transient
    private static Touch msg = null;
    @Transient
    private static Touch mail = null;

    @Transient
    private Integer sendUserId;//发送人用户ID

    @Transient
    private int type;//消息指向: 0-提醒消息,1-人数变化 2-消息 3-群聊

    @Transient
    private boolean must = false;//必须发送,数据库读取默认为true,若转化则为false

    @Transient
    private boolean send = false;//已发送

    public Touch() {
        this(false);
    }

    public Touch(boolean must) {
        this(must,0);
    }

    public Touch(int type){
        this(false,type);
    }

    public Touch(boolean must,int type){
        this.must = must;
        this.type = type;
        if(must){
            this.duration = 0;
        }
    }

    public String getTips() {
        return tips;
    }

    public void setTips(String tips) {
        this.tips = tips;
    }

    public TipsConf getTipsConf() {
        try {
            return TipsConf.valueOf(tips);
        }catch (Exception e){
            return TipsConf.TIPS_NO;
        }
    }

    public void setTipsConf(TipsConf tipsConf) {
        if(tipsConf != null){
            this.tips = tipsConf.toString();
            if(!must){
                this.duration = tipsConf.getDuration();
            }
        }else{
            this.duration = 3;
        }
    }

    public String getTitle(){
        return getTipsConf().getName() + "消息";
    }

    public boolean isSend() {
        return send;
    }

    /***
     * 消息已发送
     */
    public void send() {
        this.send = true;
    }

    public Integer getReceiveUserId() {
        return receiveUserId;
    }

    public Integer getSendUserId() {
        return sendUserId;
    }

    public void setSendUserId(Integer sendUserId) {
        this.sendUserId = sendUserId;
    }

    public void setReceiveUserId(Integer receiveUserId) {
        this.receiveUserId = receiveUserId;
    }

    public Boolean getMust() {
        return must;
    }

    public void setMust(Boolean must) {
        this.must = must;
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

    public Date getValid() {
        return valid;
    }

    public void setValid(Date valid) {
        this.valid = valid;
    }

    public int getType() {
        return type;
    }

    public int getSign() {
        return 1;
    }

    public Integer getMsgId() {
        return msgId;
    }

    public void setMsgId(Integer msgId) {
        this.msgId = msgId;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public String getRemote() {
        return getTipsConf().getRemote();
    }

    public String getModule() {
        return getTipsConf().getModule();
    }

    public String getCaseId() {
        return caseId;
    }

    public void setCaseId(String caseId) {
        this.caseId = caseId;
    }

    public Integer[] getReceiveUserIds() {
        return receiveUserIds;
    }

    public void setReceiveUserIds(Integer[] receiveUserIds) {
        this.receiveUserIds = receiveUserIds;
    }

    public String getParam() {
        return param;
    }

    public void setParam(String param) {
        this.param = param;
    }

    //在线人数变化
    public synchronized static Touch getOnlineTouch() {
        if(online == null){
            online = new Touch(1);
        }
        return online;
    }

    //消息数量变化
    public synchronized static Touch getMsgTouch(Integer userId) {
        if(userId == null){
            if(msg == null){
                msg = new Touch(2);
            }
            return msg;
        }
        Touch msg = new Touch(2);
        msg.setReceiveUserId(userId);
        return msg;
    }

    //站内信数量变化
    public synchronized static Touch getMailTouch(Integer userId) {
        if(userId == null){
            if(mail == null){
                mail = new Touch(3);
            }
            return mail;
        }
        Touch mail = new Touch(3);
        mail.setReceiveUserId(userId);
        return mail;
    }

    public Msg toMsg(){
        Msg msg = new Msg();
        msg.setTipsConf(this.getTipsConf());
        msg.setContent(this.content);
        msg.setEntityId(this.entityId);
        msg.setRead(false);
        msg.setReceiveUserId(this.receiveUserId);
        msg.setCaseId(this.caseId);
        msg.setParam(this.param);
        msg.setMsgStar(false);
        msg.setMsgTop(false);
        msg.setCreateTime(this.createTime);
        msg.setCreateTime(new Date());
        msg.setLastModifyTime(new Date());
        msg.setCreateUID(this.createUID);
        msg.setLastModifyUID(this.lastModifyUID);
        return msg;
    }

    //有效期3天
    public void valid() {
        this.valid = new Date(System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000);
    }
}
