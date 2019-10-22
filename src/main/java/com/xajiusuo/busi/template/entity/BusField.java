package com.xajiusuo.busi.template.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.xajiusuo.busi.user.entity.UserInfoVo;
import com.xajiusuo.jpa.config.BaseIntEntity;
import com.xajiusuo.utils.SelectList;
import io.swagger.annotations.ApiModel;

import javax.persistence.*;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 杨勇 on 19-1-2.
 */
@Entity
@ApiModel(value = "BUSFIELD", description = "业务表字段")
@Table(name = "C_BUSFIELD_11", catalog = "mailMana")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler","orderDesc"})
public class BusField extends BaseIntEntity {

    private String fCol;//列名

    private String fproper;//属性名称

    private String fType;//字段格式,date,string,int,double

    private String fdesc;//字段注释

    private String createname;//创建人

    private Integer orders;//顺序

    private Integer fromType;//保存方式1:一键生成,0:手动新增

    @ManyToOne(cascade = { CascadeType.MERGE, CascadeType.REFRESH }, optional = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "bsid")
    private Business business;//业务表

    @Transient
    private String orderDesc = "";//排序描述

    @Transient
    private List<SelectList> list = null;

    @Transient
    private Map<String,String> cvMap = new HashMap<String,String>();

    @Transient
    private boolean noToVal = false;//true:原值,false:忽略

    public BusField(){
    }

    public BusField(Integer id){
        this.setId(id);
    }

    public String getfCol() {
        return fCol;
    }

    public void setfCol(String fCol) {
        this.fCol = fCol;
    }

    public String getFproper() {
        return fproper;
    }

    public void setFproper(String fproper) {
        this.fproper = fproper;
    }

    public String getfType() {
        return fType;
    }

    public void setfType(String fType) {
        this.fType = fType;
    }

    public String getFdesc() {
        return fdesc;
    }

    public void setFdesc(String fdesc) {
        this.fdesc = fdesc;
    }

    public void setOrders(Integer orders) {
        this.orders = orders;
    }

    public void setOrderDesc(String orderDesc) {
        this.orderDesc = orderDesc;
    }

    public String getOrders(){
        return orders + orderDesc;
    }

    public Integer getOrder(){
        return orders;
    }

    public Integer getFromType() {
        return fromType;
    }

    public void setFromType(Integer fromType) {
        this.fromType = fromType;
    }


    public Business getBusiness() {
        return business;
    }

    public void setBusiness(Business business) {
        this.business = business;
    }

    public String getCreatename() {
        return createname;
    }

    public void setCreatename(String createname) {
        this.createname = createname;
    }

    public List<SelectList> getList() {
        return list;
    }

    public void setList(List<SelectList> list) {
        this.list = list;
    }

    public boolean isNoToVal() {
        return noToVal;
    }

    public void setNoToVal(boolean noToVal) {
        this.noToVal = noToVal;
    }

    public Map<String, String> getCvMap() {
        return cvMap;
    }

    public void setCvMap(Map<String, String> cvMap) {
        this.cvMap = cvMap;
    }

    public void setUser(UserInfoVo user){
        if(getId() == null){
            setCreateUID(user != null ? user.getUserId() : null);
            setCreateTime(new Date());
        }
        setLastModifyUID(user != null ? user.getUserId() : null);
        setCreatename(user != null ? user.getFullName() : null);
        setLastModifyTime(new Date());
    }
}
