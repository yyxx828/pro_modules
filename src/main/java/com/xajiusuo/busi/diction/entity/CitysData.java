package com.xajiusuo.busi.diction.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.xajiusuo.jpa.config.BaseEntity;
import io.swagger.annotations.ApiModel;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author GaoYong
 * @Date 19-6-25 下午2:30
 * @Description
 */
@Entity
@ApiModel(value = "citysData", description = "地址编码码表")
@Table(name ="P_CITYSDATA_19")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class CitysData  extends BaseEntity{

    private String   provinceid;// '省id';

    private String   provincename;// '省名称';

    private String   cityid;// '市id';

    private String   cityname;// '市名称';

    private String   areaid;// '区/县/城镇id';

    private String   areaname;// '区/县/城镇名称';

    private String   contryid;// '乡镇/街道id';

    private String   contryname;// '乡镇街道名称';

    private String   lon;// '经度';

    private String   lat;// '纬度';

    private String   phonecode;// '电话区号';

    public String getProvinceid() {
        return provinceid;
    }

    public void setProvinceid(String provinceid) {
        this.provinceid = provinceid;
    }

    public String getProvincename() {
        return provincename;
    }

    public void setProvincename(String provincename) {
        this.provincename = provincename;
    }

    public String getCityid() {
        return cityid;
    }

    public void setCityid(String cityid) {
        this.cityid = cityid;
    }

    public String getCityname() {
        return cityname;
    }

    public void setCityname(String cityname) {
        this.cityname = cityname;
    }

    public String getAreaid() {
        return areaid;
    }

    public void setAreaid(String areaid) {
        this.areaid = areaid;
    }

    public String getAreaname() {
        return areaname;
    }

    public void setAreaname(String areaname) {
        this.areaname = areaname;
    }

    public String getContryid() {
        return contryid;
    }

    public void setContryid(String contryid) {
        this.contryid = contryid;
    }

    public String getContryname() {
        return contryname;
    }

    public void setContryname(String contryname) {
        this.contryname = contryname;
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
