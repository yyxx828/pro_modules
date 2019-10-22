package com.xajiusuo.busi.user.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.xajiusuo.jpa.config.BaseIntEntity;
import io.swagger.annotations.ApiModel;

import javax.persistence.*;
import java.util.Set;

/**
 * Created by hadoop on 18-3-9.
 */
@Entity
@ApiModel(value = "opergroup", description = "操作权限")
@Table(name = "S_OPERGROUP_11", catalog = "village")
@JsonIgnoreProperties(value={"hibernateLazyInitializer","handler"})
public class OperGroup extends BaseIntEntity {


    private String gname;//权限组名称

    private String gdesc;//权限组描述

    private Boolean initUse;//是否默认，有且只有一个，默认时候不可删除，不可禁用

    @Transient
    private int[] oids;

    @ManyToMany(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
    @JoinTable(name = "S_OPERS_11",
            joinColumns = {@JoinColumn(name = "gid", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "oid", referencedColumnName ="id")})
    private Set<Operate> operateSet;//对应操作集和

    public String getGname() {
        return gname;
    }

    public void setGname(String gname) {
        this.gname = gname;
    }

    public String getGdesc() {
        return gdesc;
    }

    public void setGdesc(String gdesc) {
        this.gdesc = gdesc;
    }

    public Set<Operate> getOperateSet() {
        return operateSet;
    }

    public void setOperateSet(Set<Operate> operateSet) {
        this.operateSet = operateSet;
    }

    public Boolean getInitUse() {
        return initUse;
    }

    public void setInitUse(Boolean initUse) {
        this.initUse = initUse;
    }

    public int[] getOids() {
        return oids;
    }

    public void setOids(int[] oids) {
        this.oids = oids;
    }
}
