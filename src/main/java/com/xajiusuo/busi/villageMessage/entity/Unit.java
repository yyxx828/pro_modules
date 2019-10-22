package com.xajiusuo.busi.villageMessage.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.xajiusuo.jpa.config.BaseEntity;
import io.swagger.annotations.ApiModel;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author GaoYong
 * @Date 19-6-5 下午1:59
 * @Description
 */
@Entity
@ApiModel(value ="unit", description = "单元基本信息")
@Table(name="T_UNIT_19")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Unit extends BaseEntity{
    

    private String    unitno;  // '单元编号';

    private String    standardaddr;  // '标准地址';

    private String    standardaddrcode;  // '标准地址编码';

    private String    housenum;  // '房室数';


    private String  buildingname;// '楼栋名称' 只提供前端查询

    private String    buildingid;  // '所属楼栋编号';

    private String villagename;// 小区名称 只提供前端查询

    private String    villageid;  // '小区编号';

    private String    unit_name;  // '单元名称';

    public String getUnitno() {
        return unitno;
    }

    public void setUnitno(String unitno) {
        this.unitno = unitno;
    }

    public String getStandardaddr() {
        return standardaddr;
    }

    public void setStandardaddr(String standardaddr) {
        this.standardaddr = standardaddr;
    }

    public String getStandardaddrcode() {
        return standardaddrcode;
    }

    public void setStandardaddrcode(String standardaddrcode) {
        this.standardaddrcode = standardaddrcode;
    }

    public String getHousenum() {
        return housenum;
    }

    public void setHousenum(String housenum) {
        this.housenum = housenum;
    }

    public String getBuildingname() {
        return buildingname;
    }

    public void setBuildingname(String buildingname) {
        this.buildingname = buildingname;
    }

    public String getBuildingid() {
        return buildingid;
    }

    public void setBuildingid(String buildingid) {
        this.buildingid = buildingid;
    }

    public String getVillagename() {
        return villagename;
    }

    public void setVillagename(String villagename) {
        this.villagename = villagename;
    }

    public String getVillageid() {
        return villageid;
    }

    public void setVillageid(String villageid) {
        this.villageid = villageid;
    }

    public String getUnit_name() {
        return unit_name;
    }

    public void setUnit_name(String unit_name) {
        this.unit_name = unit_name;
    }
}
