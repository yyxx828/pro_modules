package com.xajiusuo.busi.diction.entity;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.xajiusuo.jpa.config.BaseEntity;
import io.swagger.annotations.ApiModel;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author GaoYong
 * @Date 19-6-6 下午5:16
 * @Description 所属分局码表实体
 */
@Entity
@ApiModel(value = "departPolice", description = "所属分局码表实体")
@Table(name = "P_DEPARTPOLICE_19")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class DepartPolice extends BaseEntity{


   private String areaid;// '所属区域id';

   private String departkey;// '分局编码值';

   private String departname;// '分局名称';

   private String lon;// '经度';

   private String lat;// '纬度';

   private String phonecode;// '分局号码';


    public String getAreaid() {
        return areaid;
    }

    public void setAreaid(String areaid) {
        this.areaid = areaid;
    }

    public String getDepartkey() {
        return departkey;
    }

    public void setDepartkey(String departkey) {
        this.departkey = departkey;
    }

    public String getDepartname() {
        return departname;
    }

    public void setDepartname(String departname) {
        this.departname = departname;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getPhonecode() {
        return phonecode;
    }

    public void setPhonecode(String phonecode) {
        this.phonecode = phonecode;
    }
}
