package com.xajiusuo.busi.diction.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.xajiusuo.jpa.config.BaseIntEntity;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.List;

/**
 * Created by 杨勇 on 18-8-21.自定义字段对应值
 */
@Entity
@Table(name = "P_DICTION_11", catalog = "VILLAGE")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Diction extends BaseIntEntity {

    private Integer pid;//字典父类id
    private Integer dlevel;//字典级别1,2
    private String keys;//对应编码值,添加自动添加
    private String val;//对于嗯中文名称
    private String descs;//对应描述

    @Transient
    private List<Diction> children;

    public Diction() {
    }

    public Integer getPid() {
        return pid;
    }

    public void setPid(Integer pid) {
        this.pid = pid;
    }

    public Integer getDlevel() {
        return dlevel;
    }

    public void setDlevel(Integer dlevel) {
        this.dlevel = dlevel;
    }

    public String getKeys() {
        return keys;
    }

    public void setKeys(String keys) {
        this.keys = keys;
    }

    public String getVal() {
        return val;
    }

    public void setVal(String val) {
        this.val = val;
    }

    public String getDescs() {
        return descs;
    }

    public void setDescs(String descs) {
        this.descs = descs;
    }

    public String getTitle(){
        return val;
    }

    public Boolean getLoading(){
        return false;
    }

    public Boolean getExpand(){
        return false;
    }

    public List<Diction> getChildren() {
        return children;
    }

    public void setChildren(List<Diction> children) {
        this.children = children;
    }

}
