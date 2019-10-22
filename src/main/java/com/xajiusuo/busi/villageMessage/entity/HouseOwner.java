package com.xajiusuo.busi.villageMessage.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.xajiusuo.jpa.config.BaseEntity;
import io.swagger.annotations.ApiModel;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author GaoYong
 * @Date 19-6-15 下午3:05
 * @Description
 */
@Entity
@ApiModel(value ="houseowner", description = "房屋业主关联表")
@Table(name="T_HOUSEOWNER_19")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class HouseOwner extends BaseEntity {
    private String ownerid; //业主主键;

    private String houseid; //房屋主键;

//    private String unitid;
//    private String buildingid;
//    private String villageid;

    public String getOwnerid() {
        return ownerid;
    }

    public void setOwnerid(String ownerid) {
        this.ownerid = ownerid;
    }

    public String getHouseid() {
        return houseid;
    }

    public void setHouseid(String houseid) {
        this.houseid = houseid;
    }


//    public String getUnitid() {
//        return unitid;
//    }
//
//    public void setUnitid(String unitid) {
//        this.unitid = unitid;
//    }
//
//    public String getBuildingid() {
//        return buildingid;
//    }
//
//    public void setBuildingid(String buildingid) {
//        this.buildingid = buildingid;
//    }
//
//    public String getVillageid() {
//        return villageid;
//    }
//
//    public void setVillageid(String villageid) {
//        this.villageid = villageid;
//    }
}
