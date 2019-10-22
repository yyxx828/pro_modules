package com.xajiusuo.busi.flow.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.xajiusuo.jpa.config.BaseIntEntity;
import io.swagger.annotations.ApiModel;

import javax.persistence.*;

/**
 * Created by fanhua on 18-1-22.审批信息表
 */
@ApiModel
@Entity
@Table(name = "F_SPINFO_11", catalog = "village")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class SpInfo extends BaseIntEntity {

    public final static int save = 0;//保存
    public final static int submit = 3;//提交
    public final static int reject = 1;//驳回
    public final static int agree = 2;//同意
    public final static int recall = 4;//撤回
    public final static int adopt = 9;//通过
    public final static int refuse = -1;//不同意

    /**
     * 审批流程ID
     * */
    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.REFRESH}, optional = true)
    @JoinColumn(name = "insId")
    private FlowIns flowIns;//流程实体
    private Integer spType;//审批操作0保存,-1驳回,2同意,
    private String spInfo;//审批意见


    /***
     * 当前审批前节点位置
     */
    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.REFRESH}, optional = true)
    @JoinColumn(name = "fnid")
    private FlowNode curNode;

    @Transient
    private Integer order;//顺序

    @Transient
    private String spUserName;//审批人员名称

    public FlowIns getFlowIns() {
        return flowIns;
    }

    public void setFlowIns(FlowIns flowIns) {
        this.flowIns = flowIns;
    }

    public Integer getSpType() {
        return spType;
    }

    public void setSpType(Integer spType) {
        this.spType = spType;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public String getSpUserName() {
        return spUserName;
    }

    public void setSpUserName(String spUserName) {
        this.spUserName = spUserName;
    }

    /***
     * @desc 增加操作对应的描述
     * @author 杨勇 18-2-10 下午2:12
     * @return
     */
    public String getSpTypes() {
        if(spType == null){
            return "";
        }
        switch (spType){
            case refuse:
                return "不同意";
            case save:
                return "保存";
            case reject:
                return "驳回";
            case agree:
                return "同意";
            case submit:
                return "提交";
            case recall:
                return "撤回";
            case adopt:
                return "通过";
        }
        return "未知[" + spType + "]";
    }


    public String getSpInfo() {
        return spInfo;
    }

    public void setSpInfo(String spInfo) {
        this.spInfo = spInfo;
    }

    public FlowNode getCurNode() {
        return curNode;
    }

    public void setCurNode(FlowNode curNode) {
        this.curNode = curNode;
    }

    public SpInfo(SpInfo spInfo) {
        super.setId(spInfo.getId());
        this.spInfo = spInfo.getSpInfo();
        this.createTime = spInfo.getCreateTime();
    }

    public SpInfo() {
    }
}
