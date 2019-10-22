package com.xajiusuo.busi.user.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.xajiusuo.jpa.config.BaseIntEntity;
import io.swagger.annotations.ApiModel;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by hadoop on 18-3-9.
 */
@Entity
@ApiModel(value = "operate", description = "操作")
@Table(name = "S_OPERATE_11", catalog = "village")
@JsonIgnoreProperties(value={"hibernateLazyInitializer","handler"})
public class Operate extends BaseIntEntity {

    private String ocode;//操作编码
    private String oname;//操作名称
    private String typeName;//分组名 便于分组快速读取 建议使用类名
    private String ourl;//对应url
    private String odesc;//操作描述

    private String depName;

    public Operate(){
    }

    public Operate(Integer id){
        this.setId(id);
    }

    public String getOcode() {
        return ocode;
    }

    public String getOname() {
        return oname;
    }

    public void setOname(String oname) {
        this.oname = oname;
    }

    public void setOcode(String ocode) {
        this.ocode = ocode;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getOurl() {
        return ourl;
    }

    public void setOurl(String ourl) {
        this.ourl = ourl;
    }

    public String getOdesc() {
        return odesc;
    }

    public void setOdesc(String odesc) {
        this.odesc = odesc;
    }
}
