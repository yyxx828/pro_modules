package com.xajiusuo.busi.flow.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.xajiusuo.jpa.config.BaseIntEntity;

import javax.persistence.*;

/**
 * Created by yangyong on 18-1-19.流程对象
 */
@Entity
@Table(name = "F_FLOWDEF_11", catalog = "village")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class FlowDef extends BaseIntEntity {

    private String fname;//流程名称
    private String fdesc;//描述
    private String remark;//标记
    private Integer flevel;//0定义,1使用
    private Integer insdefid;//使用实例Id'
    private Boolean initMode;//是否启用(false禁用,true启用)
    private Boolean initUse;//是否默认，有且只有一个，默认时候不可删除，不可禁用


    @ManyToOne(cascade = {CascadeType.MERGE,CascadeType.REFRESH},optional = true,fetch = FetchType.EAGER)
    @JoinColumn(name = "parentId")
    private FlowDef parent;//父类

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getFdesc() {
        return fdesc;
    }

    public void setFdesc(String fdesc) {
        this.fdesc = fdesc;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Integer getFlevel() {
        return flevel;
    }

    public void setFlevel(Integer flevel) {
        this.flevel = flevel;
    }

    public Integer getInsdefid() {
        return insdefid;
    }

    public void setInsdefid(Integer insdefid) {
        this.insdefid = insdefid;
    }

    public FlowDef getParent() {
        return parent;
    }

    public void setParent(FlowDef parent) {
        this.parent = parent;
    }

    public Boolean getInitMode() {
        return initMode;
    }

    public void setInitMode(Boolean initMode) {
        this.initMode = initMode;
    }

    public Boolean getInitUse() {
        return initUse;
    }

    public void setInitUse(Boolean initUse) {
        this.initUse = initUse;
    }

    public FlowDef(FlowDef flowDef) {
        super.setId(flowDef.getId());
        this.fname = flowDef.getFname();
        this.fdesc = flowDef.getFdesc();
        this.createTime = flowDef.getCreateTime();
    }

    public FlowDef() {
    }
}
