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
@ApiModel(value ="building", description = "楼栋基本信息")
@Table(name="T_BUILDING_19")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Building extends BaseEntity{


    private String  buildingno;// '楼栋编号';

    private String  villageid;// '小区编号';

    private String villagename;// 小区名称 只提供前端查询

    private String  buildingname;// '楼栋名称';

    private String  standardaddr;// '标准地址';

    private String  standardaddrcode;// '标准地址编码';

    private String  longitude;// '经度';

    private String  latitude;// '纬度';

    private String  unitnum;// '单元数';

    private String  floornum;// '楼层数';

    private String  floorhousenum;// '每层户数

    private String  overgroundfloornum;// '地面层数';

    private String  undergroundfloornum;// 地下层数

    private String  buildingstructure;// '建筑结构';

    private String  buildingnum;// '栋号';

    private String  building_type;// '楼栋类型';


    public String getBuildingno() {
        return buildingno;
    }

    public void setBuildingno(String buildingno) {
        this.buildingno = buildingno;
    }

    public String getVillageid() {
        return villageid;
    }

    public void setVillageid(String villageid) {
        this.villageid = villageid;
    }

    public String getVillagename() {
        return villagename;
    }

    public void setVillagename(String villagename) {
        this.villagename = villagename;
    }

    public String getBuildingname() {
        return buildingname;
    }

    public void setBuildingname(String buildingname) {
        this.buildingname = buildingname;
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

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getUnitnum() {
        return unitnum;
    }

    public void setUnitnum(String unitnum) {
        this.unitnum = unitnum;
    }

    public String getFloornum() {
        return floornum;
    }

    public void setFloornum(String floornum) {
        this.floornum = floornum;
    }

    public String getFloorhousenum() {
        return floorhousenum;
    }

    public void setFloorhousenum(String floorhousenum) {
        this.floorhousenum = floorhousenum;
    }

    public String getOvergroundfloornum() {
        return overgroundfloornum;
    }

    public void setOvergroundfloornum(String overgroundfloornum) {
        this.overgroundfloornum = overgroundfloornum;
    }

    public String getUndergroundfloornum() {
        return undergroundfloornum;
    }

    public void setUndergroundfloornum(String undergroundfloornum) {
        this.undergroundfloornum = undergroundfloornum;
    }

    public String getBuildingstructure() {
        return buildingstructure;
    }

    public void setBuildingstructure(String buildingstructure) {
        this.buildingstructure = buildingstructure;
    }

    public String getBuildingnum() {
        return buildingnum;
    }

    public void setBuildingnum(String buildingnum) {
        this.buildingnum = buildingnum;
    }

    public String getBuilding_type() {
        return building_type;
    }

    public void setBuilding_type(String building_type) {
        this.building_type = building_type;
    }
}
