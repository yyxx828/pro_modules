package com.xajiusuo.busi.flow.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.xajiusuo.jpa.config.BaseIntEntity;
import io.swagger.annotations.ApiModel;

import javax.persistence.*;


/**
 * Created by fanhua on 18-1-22.流程节点
 */
@ApiModel(value = "FLOWNODE", description = "流程信息")
@Entity
@Table(name = "F_FLOWNODE_11", catalog = "village")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class FlowNode extends BaseIntEntity {
    @ManyToOne(cascade = { CascadeType.MERGE, CascadeType.REFRESH }, optional = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "fdId")
    private FlowDef flowDef;//流程对象ID
    private String nName;//节点名称
    private String nbName;//带审批描述nodeBeforName
    private String naName;//审批点击未审批后描述nodeAtferName
    private Integer spType;//0指定人 -1指定机构+职位 1本机构+职位 2上级机构+职位 3/4/5...依次往上机构+职位 99最高机构+职位
    private Integer depId;//本机构/指定机构
    private String roleIds;//指定职位
    private String userIds;//指定审批人
    private Boolean userBack;//发起人是否可以撤回
    private Boolean spBack;//审批人是否可以撤回
    private Boolean backDot;//驳回接收点
    private Integer orders;//流程顺序
    private Boolean goEnd;//直接通过
    private Boolean reject;//审批人驳回权限

    public FlowNode() {
    }

    public FlowDef getFlowDef() {
        return flowDef;
    }

    public void setFlowDef(FlowDef flowDef) {
        this.flowDef = flowDef;
    }


    public String getnName() {
        return nName;
    }

    public void setnName(String nName) {
        this.nName = nName;
    }

    public String getNbName() {
        return nbName;
    }

    public void setNbName(String nbName) {
        this.nbName = nbName;
    }

    public String getNaName() {
        return naName;
    }

    public void setNaName(String naName) {
        this.naName = naName;
    }

    public Integer getSpType() {
        return spType;
    }

    public void setSpType(Integer spType) {
        this.spType = spType;
    }

    public Integer getDepId() {
        return depId;
    }

    public void setDepId(Integer depId) {
        this.depId = depId;
    }

    public String getRoleIds() {
        return roleIds;
    }

    public void setRoleIds(String roleIds) {
        this.roleIds = roleIds;
    }

    public String getUserIds() {
        return userIds;
    }

    public void setUserIds(String userIds) {
        this.userIds = userIds;
    }

    public Boolean getUserBack() {
        return userBack;
    }

    public void setUserBack(Boolean userBack) {
        this.userBack = userBack;
    }

    public Boolean getSpBack() {
        return spBack;
    }

    public void setSpBack(Boolean spBack) {
        this.spBack = spBack;
    }

    public Boolean getBackDot() {
        return backDot;
    }

    public void setBackDot(Boolean backDot) {
        this.backDot = backDot;
    }

    public Integer getOrders() {
        return orders;
    }

    public void setOrders(Integer orders) {
        this.orders = orders;
    }

    public Boolean getGoEnd() {
        return goEnd;
    }

    public void setGoEnd(Boolean goEnd) {
        this.goEnd = goEnd;
    }

    public Boolean getReject() {
        return reject;
    }

    public void setReject(Boolean reject) {
        this.reject = reject;
    }

    public FlowNode(FlowNode flowNode) {
        super.setId(flowNode.getId());
        this.flowDef = flowNode.getFlowDef();
        this.nName = flowNode.getnName();
        this.nbName = flowNode.getNbName();
        this.naName = flowNode.getNaName();
        this.spType = flowNode.getSpType();
        this.depId = flowNode.getDepId();
        this.roleIds = flowNode.getRoleIds();
        this.userIds = flowNode.getUserIds();
        this.userBack = flowNode.getUserBack();
        this.spBack = flowNode.getSpBack();
        this.backDot = flowNode.getBackDot();
        this.orders = flowNode.getOrders();
        this.goEnd = flowNode.getGoEnd();
        this.reject = flowNode.getReject();
    }
}
