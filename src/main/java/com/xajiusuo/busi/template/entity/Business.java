package com.xajiusuo.busi.template.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.xajiusuo.busi.user.entity.UserInfoVo;
import com.xajiusuo.jpa.config.BaseIntEntity;
import io.swagger.annotations.ApiModel;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;


/**
 * Created by 杨勇 on 19-1-2.
 */
@Entity
@ApiModel(value = "BUSINESS", description = "业务表管理")
@Table(name = "C_BUSINESS_11", catalog = "mailMana")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Business extends BaseIntEntity {

    private String tcode;//表标识 BUSINESS

    private String tname;//表名称 F_BUSINESS

    private String tdesc;//表 业务表描述

    private String createname;//创建人

    private String multiCol;//重复列过滤

    private Boolean errorSkip;//错误跳过默认true

    private Boolean useType;//数据用处 true:数据库,false:服务接口

    private String serName;//服务类名

    private String newOrder;//最新排序

//    @OneToMany(mappedBy = "business",cascade = { CascadeType.MERGE, CascadeType.REFRESH }, fetch = FetchType.EAGER,targetEntity = Business.class)
//    @JoinColumn(name = "bsid")
//    private Set<BusField> set;//业务类对应的字段,除系统内置字段不加载

    public Business(){
    }

    public Business(Integer id){
        this.setId(id);
    }

    public String getTcode() {
        return tcode;
    }

    public void setTcode(String tcode) {
        this.tcode = tcode;
    }

    public String getTname() {
        return tname;
    }

    public void setTname(String tname) {
        this.tname = tname;
    }

    public String getTdesc() {
        return tdesc;
    }

    public void setTdesc(String tdesc) {
        this.tdesc = tdesc;
    }

    public Boolean getErrorSkip() {
        return errorSkip;
    }

    public void setErrorSkip(Boolean errorSkip) {
        this.errorSkip = errorSkip;
    }

    public String getMultiCol() {
        return multiCol;
    }

    public void setMultiCol(String multiCol) {
        this.multiCol = multiCol;
    }

    public Boolean getUseType() {
        return useType;
    }

    public void setUseType(Boolean useType) {
        this.useType = useType;
    }

    public String getSerName() {
        return serName;
    }

    public void setSerName(String serName) {
        this.serName = serName;
    }

    public String getNewOrder() {
        return newOrder;
    }

    public void setNewOrder(String newOrder) {
        this.newOrder = newOrder;
    }

    public String getCreatename() {
        return createname;
    }

    public void setCreatename(String createname) {
        this.createname = createname;
    }

}
