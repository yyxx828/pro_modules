package com.xajiusuo.busi.mail.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.xajiusuo.jpa.config.BaseEntity;
import io.swagger.annotations.ApiModel;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.List;

/**
 * 待发信息,发送后删除,所有发送消息转换为touch
 * Created by hadoop on 19-3-29.
 */
@Entity
@ApiModel(value = "Talk", description = "群聊")
@Table(name = "M_TALK_11", catalog = "village")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class  Talk extends BaseEntity {
    //当前线程用户
    @Transient
    private static final ThreadLocal<Integer> curUser = new ThreadLocal<Integer>();

    @Transient
    private Integer me;

    private static SimpleDateFormat ymdhms = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private String sendUserName;//发送姓名

    private Integer receiveUserId;//接收人,空则群聊
    private String receiveUserName;//接收人姓名

    private String content;//聊天内容

    public static void setUserId(Integer userId) {
        curUser.set(userId);
    }

    @Transient
    private Long max;//最大时间
    @Transient
    private Long min;//最小时间
    @Transient
    private List<Talk> list = Collections.emptyList();

    /***
     * 类型为3,群聊
     * @return
     */
    public int getType(){
        return 3;
    }

    public Integer getMe() {
        return me;
    }

    public void setMe(Integer me) {
        this.me = me;
    }

    /***
     * 聊天类型 0 他人私聊本人; 1 本人私聊他人; 2 本人群聊; 3 他人群聊; -1和本人无关
     * @return
     */
    public int getCase(){
        if(isSecret()){//私聊
            if(createUID.equals(me)){
                return 1;
            }
            if(receiveUserId.equals(me)){
                return 0;
            }
            return -1;
        }
        if(receiveUserId == null && createUID != null){
            if(createUID.equals(me)){
                return 2;
            }
            return 3;
        }
        return -1;
    }

    /**
     * 是否私聊,true 私聊,false 群聊
     * @return
     */
    public boolean isSecret(){
        return createUID != null && receiveUserId != null;
    }

    /***
     * 是否自己聊天内容
     * @return
     */
    public boolean isSelf(){
        if(me != null){
            return me.equals(createUID);
        }
        return createUID != null && createUID.equals(curUser.get());
    }

    public void setReceiveUserId(Integer receiveUserId) {
        this.receiveUserId = receiveUserId;
    }

    public String getSendUserName() {
        return sendUserName;
    }

    public void setSendUserName(String sendUserName) {
        this.sendUserName = sendUserName;
    }

    public Integer getReceiveUserId() {
        return receiveUserId;
    }

    public String getReceiveUserName() {
        return receiveUserName;
    }

    public void setReceiveUserName(String receiveUserName) {
        this.receiveUserName = receiveUserName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getMax() {
        return max;
    }

    public void setMax(Long max) {
        this.max = max;
    }

    public Long getMin() {
        return min;
    }

    public void setMin(Long min) {
        this.min = min;
    }

    public List<Talk> getList() {
        return list;
    }

    public void setList(List<Talk> list) {
        this.list = list;
    }

    public String getName(){
        if(isSecret()){
            if(isSelf()){
                return "我 对 " + receiveUserName;
            }else{
                return sendUserName + " 对 我";
            }
        }
        return sendUserName;
    }

    public Integer getUserId(){
        if(isSecret() && isSelf()){
            return receiveUserId;
        }
        return createUID;
    }

    public String getDate(){
        if(createTime == null){
            return null;
        }
        return ymdhms.format(createTime);
    }

}
