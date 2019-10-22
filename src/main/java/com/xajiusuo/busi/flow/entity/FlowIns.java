package com.xajiusuo.busi.flow.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.xajiusuo.jpa.config.BaseIntEntity;
import com.xajiusuo.busi.user.entity.Userinfo;
import io.swagger.annotations.ApiModel;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.*;
import java.util.List;


/**
 * Created by fanhua on 18-1-22.流程定义
 */
@ApiModel
@Entity
@Table(name = "F_FLOWINS_11", catalog = "village")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class FlowIns extends BaseIntEntity {
    private String busiId;//业务数据ID
    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.REFRESH}, optional = true)
    @JoinColumn(name="fdId")
    private FlowDef flowDef;//流程对象ID
    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.REFRESH}, optional = true)
    @JoinColumn(name="fnId")
    private FlowNode flowNode;//流程节点ID
    private String uids;//指定人、审批人

    private String curSpUids;//当前审批人员

    private String sysId;
    private String sysName;

    @Transient
    private FlowNode nextNode;

    private Integer hostId;//待办人

    private Integer status;//流程状态0保存,1驳回,2提交,3审批中,99通过,-1拒绝

    private String statusStr;//状态描述

    @Transient
    private List<Userinfo> nextSpUser;

    @Transient
    private List<Userinfo> curSpUser;

    public FlowIns() {
    }

    public FlowIns(FlowDef flowDef, String busiId) {
        this.flowDef = flowDef;
        this.busiId = busiId;
    }

    public String getBusiId() {
        return busiId;
    }

    public void setBusiId(String busiId) {
        this.busiId = busiId;
    }

    public FlowDef getFlowDef() {
        return flowDef;
    }

    public void setFlowDef(FlowDef flowDef) {
        this.flowDef = flowDef;
    }

    public FlowNode getFlowNode() {
        return flowNode;
    }

    public void setFlowNode(FlowNode flowNode) {
        this.flowNode = flowNode;
    }

    public String getUids() {
        return uids;
    }

    public void setUids(String uids) {
        this.uids = uids;
    }

    public Integer getHostId() {
        return hostId;
    }

    public void setHostId(Integer hostId) {
        this.hostId = hostId;
        if(hostId != null){
            this.curSpUids = hostId + "";
        }
    }

    public List<Userinfo> getNextSpUser() {
        return nextSpUser;
    }

    public void setNextSpUser(List<Userinfo> nextSpUser) {
        this.nextSpUser = nextSpUser;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public FlowNode getCurNode() {
        return flowNode;
    }

    public FlowNode getNextNode() {
        return nextNode;
    }

    public void setNextNode(FlowNode nextNode) {
        this.nextNode = nextNode;
    }

    public List<Userinfo> getCurSpUser() {
        return curSpUser;
    }

    public void setCurSpUser(List<Userinfo> curSpUser) {
        this.curSpUser = curSpUser;
    }

    public String getCurSpUids() {
        return curSpUids;
    }

    public String getSysId() {
        return sysId;
    }

    public void setSysId(String sysId) {
        this.sysId = sysId;
    }

    public String getSysName() {
        return sysName;
    }

    public void setSysName(String sysName) {
        this.sysName = sysName;
    }

    public void setCurSpUids(String curSpUids) {
        this.curSpUids = curSpUids;
    }

    public void statusing(){
        if(flowNode != null){
            this.statusStr = flowNode.getNaName();
        }else{
            switch (status){
                case 0:this.statusStr = "保存";
                    break;
                case 99 : this.statusStr = "通过";
                    break;
                case -1 : this.statusStr = "拒绝";
                    break;
                default:
                    this.statusStr = "结束";
            }
        }
    }

    public String getStatusStr() {
        return statusStr;
    }

    public void setStatusStr(String statusStr) {
        this.statusStr = statusStr;
    }

    public FlowIns(FlowIns flowIns) {
        super.setId(flowIns.getId());
        this.flowDef = flowIns.getFlowDef();
        this.flowNode = flowIns.getFlowNode();
        this.hostId = flowIns.getHostId();
        this.status = flowIns.getStatus();
        this.statusStr = flowIns.getStatusStr();
        this.nextSpUser = flowIns.getNextSpUser();
    }

    /***
     * 设置当前审批人员信息
     */
    public void setCurSpUids() {
        if(flowNode == null || flowNode.getOrders() == 1){
            this.curSpUids = null;
            return;
        }
        if(StringUtils.isNotBlank(uids)){
            this.curSpUids = uids;
        }else{
            if(nextSpUser != null){
                StringBuilder sb = new StringBuilder();
                for(Userinfo u:nextSpUser){
                    if(sb.length() > 0){
                        sb.append(",");
                    }
                    sb.append(u.getId());
                }
                this.curSpUids = sb.toString();
            }
        }
    }

    public boolean saveStatus() {
        if(status == 0){
            if(flowNode == null || flowNode.getOrders() != 1 || StringUtils.isNotBlank(curSpUids) || StringUtils.isNotBlank(uids) || hostId != null){
                curSpUids = null;
                uids = null;
                hostId = null;
                return true;
            }
        }
        return false;
    }
}
