package com.xajiusuo.busi.wifilog.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.xajiusuo.jpa.config.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 *  WIFI探针信息采集表
 * Created by liujiankang on 19-6-10.
 */
@ApiModel
@Entity
@Table(name = "T_WIFILOG_39", catalog = "village")
public class WifiLog  implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(
            generator = "uuid"
    )
    @GenericGenerator(
            name = "uuid",
            strategy = "uuid"
    )
    private String id;
    @ApiModelProperty(required = true,value="小区编号",dataType = "String")
    private String villageid;
    @ApiModelProperty(required = true,value="设备编码",dataType = "String")
    private	String	apeid;
    @ApiModelProperty(required = true,value="名称",dataType = "String")
    private String name;
    @ApiModelProperty(required = true,value="型号",dataType = "String")
    private String model;
    @ApiModelProperty(required = true,value="IP地址",dataType = "String")
    private String ipaddr;
    @ApiModelProperty(required = true,value="IPV6地址",dataType = "String")
    private String ipv6addr;
    @ApiModelProperty(required = true,value="经度",dataType = "String")
    private String longitude;
    @ApiModelProperty(required = true,value="纬度",dataType = "String")
    private String latitude;
    @ApiModelProperty(required = true,value="安装地点行政区划代码",dataType = "String")
    private String placecode;
    @ApiModelProperty(required = true,value="位置名",dataType = "String")
    private String place;
    @ApiModelProperty(required = true,value="管辖单位代码",dataType = "String")
    private String orgcode;
    @ApiModelProperty(required = true,value="抓取mac地址1",dataType = "String")
    private String mac1;
    @ApiModelProperty(required = true,value="抓取mac地址2",dataType = "String")
    private String mac2;
    @ApiModelProperty(required = true,value="抓取mac地址3",dataType = "String")
    private String mac3;
    @ApiModelProperty(required = true,value="抓取mac地址4",dataType = "String")
    private String mac4;
    @ApiModelProperty(required = true,value="抓取mac地址5",dataType = "String")
    private String mac5;
    @ApiModelProperty(required = true,value="抓取mac地址6",dataType = "String")
    private String mac6;
    @ApiModelProperty(required = true,value="抓取mac地址7",dataType = "String")
    private String mac7;
    @ApiModelProperty(required = true,value="抓取mac地址8",dataType = "String")
    private String mac8;
    @ApiModelProperty(required = true,value="抓取mac地址9",dataType = "String")
    private String mac9;
    @ApiModelProperty(required = true,value="抓取mac地址10",dataType = "String")
    private String mac10;
    @ApiModelProperty(required = true,value="附加信息",dataType = "Date")
    private String extinfo;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @ApiModelProperty(required = true,value="采集时间",dataType = "Date")
    private Date passtime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @ApiModelProperty(required = true,value="创建时间",dataType = "Date")
    private Date createdate;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @ApiModelProperty(required = true,value="进入时间",dataType = "Date")
    private Date timeenter;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @ApiModelProperty(required = true,value="离开时间",dataType = "Date")
    private Date timeleave;


    public WifiLog() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getTimeleave() {
        return timeleave;
    }

    public void setTimeleave(Date timeleave) {
        this.timeleave = timeleave;
    }

    public String getVillageid() {
        return villageid;
    }

    public void setVillageid(String villageid) {
        this.villageid = villageid;
    }

    public String getApeid() {
        return apeid;
    }

    public void setApeid(String apeid) {
        this.apeid = apeid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getIpaddr() {
        return ipaddr;
    }

    public void setIpaddr(String ipaddr) {
        this.ipaddr = ipaddr;
    }

    public String getIpv6addr() {
        return ipv6addr;
    }

    public void setIpv6addr(String ipv6addr) {
        this.ipv6addr = ipv6addr;
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

    public String getPlacecode() {
        return placecode;
    }

    public void setPlacecode(String placecode) {
        this.placecode = placecode;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getOrgcode() {
        return orgcode;
    }

    public void setOrgcode(String orgcode) {
        this.orgcode = orgcode;
    }

    public String getMac1() {
        return mac1;
    }

    public void setMac1(String mac1) {
        this.mac1 = mac1;
    }

    public String getMac2() {
        return mac2;
    }

    public void setMac2(String mac2) {
        this.mac2 = mac2;
    }

    public String getMac3() {
        return mac3;
    }

    public void setMac3(String mac3) {
        this.mac3 = mac3;
    }

    public String getMac4() {
        return mac4;
    }

    public void setMac4(String mac4) {
        this.mac4 = mac4;
    }

    public String getMac5() {
        return mac5;
    }

    public void setMac5(String mac5) {
        this.mac5 = mac5;
    }

    public String getMac6() {
        return mac6;
    }

    public void setMac6(String mac6) {
        this.mac6 = mac6;
    }

    public String getMac7() {
        return mac7;
    }

    public void setMac7(String mac7) {
        this.mac7 = mac7;
    }

    public String getMac8() {
        return mac8;
    }

    public void setMac8(String mac8) {
        this.mac8 = mac8;
    }

    public String getMac9() {
        return mac9;
    }

    public void setMac9(String mac9) {
        this.mac9 = mac9;
    }

    public String getMac10() {
        return mac10;
    }

    public void setMac10(String mac10) {
        this.mac10 = mac10;
    }

    public String getExtinfo() {
        return extinfo;
    }

    public void setExtinfo(String extinfo) {
        this.extinfo = extinfo;
    }

    public Date getPasstime() {
        return passtime;
    }

    public void setPasstime(Date passtime) {
        this.passtime = passtime;
    }

    public Date getCreatedate() {
        return createdate;
    }

    public void setCreatedate(Date createdate) {
        this.createdate = createdate;
    }

    public Date getTimeenter() {
        return timeenter;
    }

    public void setTimeenter(Date timeenter) {
        this.timeenter = timeenter;
    }
}
