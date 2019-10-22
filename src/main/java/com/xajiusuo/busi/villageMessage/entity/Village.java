package com.xajiusuo.busi.villageMessage.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.xajiusuo.busi.diction.entity.CitysData;
import com.xajiusuo.busi.diction.entity.DepartPolice;
import com.xajiusuo.busi.diction.entity.ProvincesData;
import com.xajiusuo.jpa.config.BaseEntity;
import io.swagger.annotations.ApiModel;

import javax.persistence.*;

/**
 * @author GaoYong
 * @Date 19-6-5 下午1:59
 * @Description
 */
@Entity
@ApiModel(value ="village", description = "小区基本信息")
@Table(name="T_VILLAGE_19")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Village  extends BaseEntity{
    

    private String  villageid;//      '小区编号';

    private String  villagetype;//      '小区类型';

    private String villagename;//      '小区名称';

    private String villageimg;//      '照片（url';

    private String villageregion;//      '小区区域(有经纬度描述顶点的多边形)';

    @ManyToOne(cascade = {CascadeType.MERGE,CascadeType.REFRESH},optional = true,fetch = FetchType.EAGER)
    @JoinColumn(name = "subbureau")
    private DepartPolice departPolice;//      '所属分局(需要字典表这里存的应该是分局标识)';

    @ManyToOne(cascade = {CascadeType.MERGE,CascadeType.REFRESH},optional = true,fetch = FetchType.EAGER)
    @JoinColumn(name = "district",referencedColumnName = "areaid")
    private ProvincesData provincesData;//      '所属行政区划(需要行政区域字典表存的应该是标识)';


    private String  standardaddr;// '详细地址';

    @ManyToOne(cascade = {CascadeType.MERGE,CascadeType.REFRESH},optional = true,fetch = FetchType.EAGER)
    @JoinColumn(name = "standardaddrcode")
    private CitysData standardaddrcode;// '标准地址';

//        private String  standardaddrcode;// '标准地址';

    private String buildingnum;//      '居民楼数';

    private String housenum;//      '房屋数';

    private String residentnum;//      '总人数';

    private String permanentresidentnum;//      '常住人口数';

    private String temporaryresidentnum;//      '暂住人口数';

    private String guestresidentnum;//      '寄住人口数';

    private String parkingspacenum;//      '停车位数';

    private String motorvehiclenum;//      '机动车数';

    private String nonmotorvehiclenum;//      '非机动车数';

    private String shednum;//      '车棚数';

    private String unitnum;//      '单元数量';

    private String foreignnum;//      '境外人口数量';

    private String chargnum;//      '充电庄数量';

    private String probennum;//      '探头数量';

    private String logo1;//      'logo1';

    private String logo2;//      'logo2';

    private String logo3;//      'logo3';

    private String logo4;//      'logo4';

    public String getVillageid() {
        return villageid;
    }

    public void setVillageid(String villageid) {
        this.villageid = villageid;
    }

    public String getVillagetype() {
        return villagetype;
    }

    public void setVillagetype(String villagetype) {
        this.villagetype = villagetype;
    }

    public String getVillagename() {
        return villagename;
    }

    public void setVillagename(String villagename) {
        this.villagename = villagename;
    }

    public String getVillageimg() {
        return villageimg;
    }

    public void setVillageimg(String villageimg) {
        this.villageimg = villageimg;
    }

    public String getVillageregion() {
        return villageregion;
    }

    public void setVillageregion(String villageregion) {
        this.villageregion = villageregion;
    }

    public DepartPolice getDepartPolice() {
        return departPolice;
    }

    public void setDepartPolice(DepartPolice departPolice) {
        this.departPolice = departPolice;
    }

    public ProvincesData getProvincesData() {
        return provincesData;
    }

    public void setProvincesData(ProvincesData provincesData) {
        this.provincesData = provincesData;
    }

    public String getStandardaddr() {
        return standardaddr;
    }

    public void setStandardaddr(String standardaddr) {
        this.standardaddr = standardaddr;
    }

    public CitysData getStandardaddrcode() {
        return standardaddrcode;
    }

    public void setStandardaddrcode(CitysData standardaddrcode) {
        this.standardaddrcode = standardaddrcode;
    }

    public String getBuildingnum() {
        return buildingnum;
    }

    public void setBuildingnum(String buildingnum) {
        this.buildingnum = buildingnum;
    }

    public String getHousenum() {
        return housenum;
    }

    public void setHousenum(String housenum) {
        this.housenum = housenum;
    }

    public String getResidentnum() {
        return residentnum;
    }

    public void setResidentnum(String residentnum) {
        this.residentnum = residentnum;
    }

    public String getPermanentresidentnum() {
        return permanentresidentnum;
    }

    public void setPermanentresidentnum(String permanentresidentnum) {
        this.permanentresidentnum = permanentresidentnum;
    }

    public String getTemporaryresidentnum() {
        return temporaryresidentnum;
    }

    public void setTemporaryresidentnum(String temporaryresidentnum) {
        this.temporaryresidentnum = temporaryresidentnum;
    }

    public String getGuestresidentnum() {
        return guestresidentnum;
    }

    public void setGuestresidentnum(String guestresidentnum) {
        this.guestresidentnum = guestresidentnum;
    }

    public String getParkingspacenum() {
        return parkingspacenum;
    }

    public void setParkingspacenum(String parkingspacenum) {
        this.parkingspacenum = parkingspacenum;
    }

    public String getMotorvehiclenum() {
        return motorvehiclenum;
    }

    public void setMotorvehiclenum(String motorvehiclenum) {
        this.motorvehiclenum = motorvehiclenum;
    }

    public String getNonmotorvehiclenum() {
        return nonmotorvehiclenum;
    }

    public void setNonmotorvehiclenum(String nonmotorvehiclenum) {
        this.nonmotorvehiclenum = nonmotorvehiclenum;
    }

    public String getShednum() {
        return shednum;
    }

    public void setShednum(String shednum) {
        this.shednum = shednum;
    }

    public String getUnitnum() {
        return unitnum;
    }

    public void setUnitnum(String unitnum) {
        this.unitnum = unitnum;
    }

    public String getForeignnum() {
        return foreignnum;
    }

    public void setForeignnum(String foreignnum) {
        this.foreignnum = foreignnum;
    }

    public String getChargnum() {
        return chargnum;
    }

    public void setChargnum(String chargnum) {
        this.chargnum = chargnum;
    }

    public String getProbennum() {
        return probennum;
    }

    public void setProbennum(String probennum) {
        this.probennum = probennum;
    }

    public String getLogo1() {
        return logo1;
    }

    public void setLogo1(String logo1) {
        this.logo1 = logo1;
    }

    public String getLogo2() {
        return logo2;
    }

    public void setLogo2(String logo2) {
        this.logo2 = logo2;
    }

    public String getLogo3() {
        return logo3;
    }

    public void setLogo3(String logo3) {
        this.logo3 = logo3;
    }

    public String getLogo4() {
        return logo4;
    }

    public void setLogo4(String logo4) {
        this.logo4 = logo4;
    }
}
